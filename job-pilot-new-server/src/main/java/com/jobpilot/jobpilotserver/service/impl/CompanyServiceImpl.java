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

/**
 * 企业与黑名单管理业务实现类
 * <p>
 * 主要职责：
 * 1. 提供基于并查集（Disjoint-Set / DSU）算法的企业别名聚类归一化与分页查询；
 * 2. 维护企业黑名单列表（支持增删及 Caffeine 本地缓存的主动失效）；
 * 3. 维护企业主体合并关系与别名映射表。
 */
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    private final ObjectMapper objectMapper;
    private final BlacklistedCompanyMapper blacklistedCompanyMapper;
    private final CompanyAliasMapper companyAliasMapper;
    private final Cache<String, Set<String>> blacklistedCompanyCache;

    public CompanyServiceImpl(ObjectMapper objectMapper,
                              BlacklistedCompanyMapper blacklistedCompanyMapper,
                              CompanyAliasMapper companyAliasMapper,
                              Cache<String, Set<String>> blacklistedCompanyCache) {
        this.objectMapper = objectMapper;
        this.blacklistedCompanyMapper = blacklistedCompanyMapper;
        this.companyAliasMapper = companyAliasMapper;
        this.blacklistedCompanyCache = blacklistedCompanyCache;
    }

    /**
     * 分页查询企业列表（基于并查集进行别名聚类归一化）
     * <p>
     * 算法与处理流程：
     * 1. 从别名映射表中加载所有别名与标准名称的关联，初始化构建并查集结构；
     * 2. 遍历所有原始企业，通过并查集查找各企业所属的规范主体根名称（Canonical Name）；
     * 3. 归并同主体的企业记录，聚合成统一的 CompanyVO；
     * 4. 针对搜索关键字进行不区分大小写的模糊过滤；
     * 5. 内存级分页切片并返回结果集。
     *
     * @param page 页码（从 1 开始）
     * @param pageSize 每页条数
     * @param query 搜索关键词（企业名称模糊匹配）
     * @return 包含 data 列表和 total 总记录数的 Map
     */
    @Override
    public Map<String, Object> getCompanies(int page, int pageSize, String query) {
        // 1. 初始化并查集并加载企业别名拓扑关系
        CompanyDisjointSet dsu = new CompanyDisjointSet();
        List<CompanyAlias> aliasList = companyAliasMapper.selectList(null);
        for (CompanyAlias alias : aliasList) {
            dsu.union(alias.getAliasName(), alias.getCanonicalName());
        }

        // 2. 查询全部企业并进行主体归并
        List<Company> allCompanies = list();
        Map<String, CompanyVO> aggregatedMap = new HashMap<>();

        for (Company company : allCompanies) {
            // 通过并查集寻根，获取该企业的规范主体名称
            String rootName = dsu.find(company.getCompanyName());
            CompanyVO vo = aggregatedMap.get(rootName);
            if (vo == null) {
                vo = new CompanyVO();
                BeanUtils.copyProperties(company, vo);
                vo.setCompanyName(rootName); // 统一展示为规范主体名称
                aggregatedMap.put(rootName, vo);
            }
        }

        // 3. 关键字过滤
        List<CompanyVO> resultList = new ArrayList<>(aggregatedMap.values());
        if (query != null && !query.trim().isEmpty()) {
            String q = query.toLowerCase();
            resultList.removeIf(c -> c.getCompanyName() == null || !c.getCompanyName().toLowerCase().contains(q));
        }

        // 4. 计算分页切片
        int total = resultList.size();
        int fromIndex = Math.min((page - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<CompanyVO> pageData = resultList.subList(fromIndex, toIndex);

        Map<String, Object> res = new HashMap<>();
        res.put("data", pageData);
        res.put("total", total);
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
        return true;
    }
}
