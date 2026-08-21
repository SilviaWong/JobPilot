package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.BlacklistedCompanyMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyAliasMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.model.entity.BlacklistedCompany;
import com.jobpilot.jobpilotserver.model.entity.Company;
import com.jobpilot.jobpilotserver.model.entity.CompanyAlias;
import com.jobpilot.jobpilotserver.model.vo.CompanyVO;
import com.jobpilot.jobpilotserver.service.CompanyService;
import com.jobpilot.jobpilotserver.strategy.algorithm.CompanyDisjointSet;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.stream.Collectors;

/**
 * 企业与黑名单管理业务实现类
 * <p>
 * 主要职责：
 * 1. 提供基于并查集（Disjoint-Set / DSU）算法的企业别名聚类归一化与高效物理分页查询；
 * 2. 维护企业黑名单列表（支持增删及 Caffeine 本地缓存的主动失效）；
 * 3. 维护企业主体合并关系与别名映射表（变更时自动刷新 DSU 本地缓存）。
 */
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    private final ObjectMapper objectMapper;
    private final BlacklistedCompanyMapper blacklistedCompanyMapper;
    private final CompanyAliasMapper companyAliasMapper;
    private final Cache<String, Set<String>> blacklistedCompanyCache;
    private final Cache<String, CompanyDisjointSet> companyDsuCache;

    public CompanyServiceImpl(ObjectMapper objectMapper,
                              BlacklistedCompanyMapper blacklistedCompanyMapper,
                              CompanyAliasMapper companyAliasMapper,
                              @Qualifier("blacklistedCompanyCache") Cache<String, Set<String>> blacklistedCompanyCache,
                              @Qualifier("companyDsuCache") Cache<String, CompanyDisjointSet> companyDsuCache) {
        this.objectMapper = objectMapper;
        this.blacklistedCompanyMapper = blacklistedCompanyMapper;
        this.companyAliasMapper = companyAliasMapper;
        this.blacklistedCompanyCache = blacklistedCompanyCache;
        this.companyDsuCache = companyDsuCache;
    }

    /**
     * 获取或构建并查集别名模型（优先命中 Caffeine 本地缓存）
     */
    private CompanyDisjointSet getOrBuildDsu() {
        return companyDsuCache.get("global_company_dsu", k -> {
            log.debug("Building CompanyDisjointSet model into Caffeine cache...");
            CompanyDisjointSet dsu = new CompanyDisjointSet();
            List<CompanyAlias> aliasList = companyAliasMapper.selectList(null);
            for (CompanyAlias alias : aliasList) {
                dsu.union(alias.getAliasName(), alias.getCanonicalName());
            }
            return dsu;
        });
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CompanyServiceImpl.class);

    /**
     * 分页查询企业列表（基于 SQL 物理分页 + Caffeine 缓存 DSU 主体归一化）
     *
     * @param page 页码（从 1 开始）
     * @param pageSize 每页条数
     * @param query 搜索关键词（企业名称或全称模糊匹配）
     * @return 包含 data 列表和 total 总记录数的 Map
     */
    @Override
    public Map<String, Object> getCompanies(int page, int pageSize, String query) {
        Page<Company> companyPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();

        if (query != null && !query.trim().isEmpty()) {
            String q = query.trim();
            queryWrapper.and(w -> w.like(Company::getCompanyName, q)
                    .or()
                    .like(Company::getCompanyFullName, q));
        }

        queryWrapper.orderByDesc(Company::getUpdatedAt);

        Page<Company> resultPage = page(companyPage, queryWrapper);
        CompanyDisjointSet dsu = getOrBuildDsu();

        List<CompanyVO> voList = resultPage.getRecords().stream().map(company -> {
            CompanyVO vo = new CompanyVO();
            BeanUtils.copyProperties(company, vo);
            String canonicalName = dsu.find(company.getCompanyName());
            if (canonicalName != null && !canonicalName.isEmpty()) {
                vo.setCompanyName(canonicalName);
            }
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> res = new HashMap<>();
        res.put("data", voList);
        res.put("total", (int) resultPage.getTotal());
        res.put("hasMore", (page * pageSize) < resultPage.getTotal());
        return res;
    }

    /**
     * 获取数据库中全部原始企业记录
     *
     * @return 企业实体列表
     */
    @Override
    public List<Company> getAllCompanies() {
        return list();
    }

    /**
     * 获取所有被加入黑名单的企业列表
     *
     * @return 黑名单企业实体列表
     */
    @Override
    public List<BlacklistedCompany> getBlacklist() {
        return blacklistedCompanyMapper.selectList(null);
    }

    /**
     * 将指定企业加入黑名单或更新拉黑原因
     * <p>
     * 操作成功后会自动清空 Caffeine 本地黑名单缓存，以确保岗位列表过滤时即时生效。
     *
     * @param companyName 企业名称
     * @param reason 拉黑原因（如：外包、加班严重、PUA 等）
     * @return 是否操作成功
     */
    @Override
    public boolean addBlacklist(String companyName, String reason) {
        boolean success;
        BlacklistedCompany existing = blacklistedCompanyMapper.selectOne(
                new LambdaQueryWrapper<BlacklistedCompany>().eq(BlacklistedCompany::getCompanyName, companyName));
        if (existing != null) {
            existing.setReason(reason);
            existing.setCreatedAt(LocalDateTime.now());
            success = blacklistedCompanyMapper.updateById(existing) > 0;
        } else {
            BlacklistedCompany item = new BlacklistedCompany();
            item.setCompanyName(companyName);
            item.setReason(reason);
            item.setCreatedAt(LocalDateTime.now());
            success = blacklistedCompanyMapper.insert(item) > 0;
        }
        // 拉黑变更成功后，强制失效 Caffeine 本地黑名单缓存
        if (success) {
            blacklistedCompanyCache.invalidateAll();
        }
        return success;
    }

    /**
     * 从黑名单中移除指定企业
     * <p>
     * 移除成功后会自动清空 Caffeine 本地黑名单缓存。
     *
     * @param companyName 企业名称
     * @return 是否移除成功
     */
    @Override
    public boolean removeBlacklist(String companyName) {
        boolean success = blacklistedCompanyMapper.delete(
                new LambdaQueryWrapper<BlacklistedCompany>().eq(BlacklistedCompany::getCompanyName, companyName)) > 0;
        // 移除成功后，强制失效 Caffeine 本地黑名单缓存
        if (success) {
            blacklistedCompanyCache.invalidateAll();
        }
        return success;
    }

    /**
     * 合并企业主体别名
     * <p>
     * 将一组来源别名企业 (sourceNames) 归并映射到目标规范主体 (targetName)，
     * 并持久化到别名拓扑表中供并查集聚类使用。
     *
     * @param targetName 目标标准企业名称
     * @param sourceNames 待合并的别名企业名称列表
     * @return 是否合并成功
     */
    @Override
    public boolean mergeCompanies(String targetName, List<String> sourceNames) {
        for (String source : sourceNames) {
            if (source.equals(targetName)) continue;
            CompanyAlias existing = companyAliasMapper.selectOne(
                    new LambdaQueryWrapper<CompanyAlias>().eq(CompanyAlias::getAliasName, source));
            if (existing != null) {
                existing.setCanonicalName(targetName);
                companyAliasMapper.updateById(existing);
            } else {
                CompanyAlias alias = new CompanyAlias();
                alias.setId(UUID.randomUUID().toString());
                alias.setAliasName(source);
                alias.setCanonicalName(targetName);
                alias.setCreatedAt(LocalDateTime.now());
                companyAliasMapper.insert(alias);
            }
        }
        // 别名拓扑发生变更，强制失效 DSU 本地缓存
        companyDsuCache.invalidateAll();
        return true;
    }
}
