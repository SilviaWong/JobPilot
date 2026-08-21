package com.jobpilot.jobpilotserver.strategy.sync.company;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.model.entity.Company;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.util.CompanyNameCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * 平台企业处理模板抽象基类 (Template Method Pattern)
 * <p>
 * 固化了企业数据同步的通用算法骨架：
 * 1. 提取平台专属字段 (抽象钩子: extractFields)；
 * 2. 规范化清洗企业简称、全称与加密ID；
 * 3. 平台标识标准化；
 * 4. 数据库查重匹配；
 * 5. 是否覆盖更新判断 (钩子: shouldUpdate)；
 * 6. 持久化写入/更新 Company 表 rawData2；
 * 7. 级联同步 Job 职位表的 companyFullName。
 */
public abstract class AbstractCompanyProcessor implements CompanyProcessorStrategy {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final CompanyMapper companyMapper;
    protected final JobMapper jobMapper;
    protected final ObjectMapper objectMapper;

    public AbstractCompanyProcessor(CompanyMapper companyMapper,
                                    JobMapper jobMapper,
                                    ObjectMapper objectMapper) {
        this.companyMapper = companyMapper;
        this.jobMapper = jobMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * 核心模板方法 (固化统一同步处理流程)
     */
    @Override
    public final void process(Map<?, ?> company, String platform) {
        if (company == null || company.isEmpty()) {
            return;
        }

        // 1. 提取平台专属字段
        CompanyFieldsDTO fields = extractFields(company, platform);
        if (fields == null || fields.isEmpty()) {
            return;
        }

        // 2. 统一清洗规范化
        String cName = CompanyNameCleaner.clean(fields.getCompanyName());
        String cFullName = CompanyNameCleaner.clean(fields.getCompanyFullName());
        String companyId = fields.getCompanyId() != null ? fields.getCompanyId().trim() : "";
        String finalCompanyName = !cFullName.isEmpty() ? cFullName : cName;

        if (finalCompanyName.isEmpty() && companyId.isEmpty()) {
            return;
        }

        // 3. 平台标识标准化
        String standardizedPlatform = getStandardizedPlatform(platform);
        LocalDateTime now = LocalDateTime.now();

        // 4. 查重查找已有企业记录
        Company existingCompany = findExistingCompany(companyId, finalCompanyName, cFullName, cName, standardizedPlatform);

        // 5. 序列化原始数据
        String stringifiedData = toJson(fields.getRawData() != null ? fields.getRawData() : company);

        // 6. 执行写入或更新
        if (existingCompany != null) {
            if (shouldUpdate(existingCompany, fields)) {
                existingCompany.setRawData2(stringifiedData);
                if (!companyId.isEmpty()) existingCompany.setCompanyId(companyId);
                if (!cFullName.isEmpty()) existingCompany.setCompanyFullName(cFullName);
                if (!cName.isEmpty()) existingCompany.setCompanyName(cName);
                existingCompany.setUpdatedAt(now);
                companyMapper.updateById(existingCompany);
            }
        } else if (!finalCompanyName.isEmpty()) {
            Company newComp = new Company();
            newComp.setId(UUID.randomUUID().toString());
            newComp.setCompanyName(finalCompanyName);
            newComp.setCompanyFullName(!cFullName.isEmpty() ? cFullName : null);
            newComp.setSourcePlatform(standardizedPlatform);
            newComp.setCompanyId(!companyId.isEmpty() ? companyId : null);
            newComp.setRawData2(stringifiedData);
            newComp.setCreatedAt(now);
            newComp.setUpdatedAt(now);
            companyMapper.insert(newComp);
        }

        // 7. 级联同步 Job 职位表中的 companyFullName
        if (!cFullName.isEmpty()) {
            final String finalCId = companyId;
            final String finalCName = finalCompanyName;
            LambdaUpdateWrapper<Job> updateWrapper = new LambdaUpdateWrapper<Job>()
                    .eq(Job::getPlatform, standardizedPlatform)
                    .set(Job::getCompanyFullName, cFullName)
                    .set(Job::getUpdatedAt, now);
            if (!finalCId.isEmpty() && !finalCName.isEmpty()) {
                updateWrapper.and(w -> w.eq(Job::getCompanyId, finalCId).or().eq(Job::getCompanyName, finalCName));
            } else if (!finalCId.isEmpty()) {
                updateWrapper.eq(Job::getCompanyId, finalCId);
            } else {
                updateWrapper.eq(Job::getCompanyName, finalCName);
            }
            jobMapper.update(null, updateWrapper);
        }
    }

    /**
     * 子类必须实现的平台特定字段提取钩子
     */
    @Override
    public abstract CompanyFieldsDTO extractFields(Map<?, ?> company, String platform);

    /**
     * 平台名称标准化钩子（子类可按需重写）
     */
    @Override
    public String getStandardizedPlatform(String platform) {
        return platform;
    }

    /**
     * 查找已有企业记录（子类如猎聘可定制匹配逻辑）
     */
    protected Company findExistingCompany(String companyId, String finalCompanyName, String cFullName, String cName, String standardizedPlatform) {
        Company existing = null;
        if (!companyId.isEmpty()) {
            existing = companyMapper.selectOne(new LambdaQueryWrapper<Company>()
                    .eq(Company::getCompanyId, companyId)
                    .eq(Company::getSourcePlatform, standardizedPlatform));
        }
        if (existing == null && !finalCompanyName.isEmpty()) {
            existing = companyMapper.selectOne(new LambdaQueryWrapper<Company>()
                    .eq(Company::getCompanyName, finalCompanyName)
                    .eq(Company::getSourcePlatform, standardizedPlatform));
        }
        return existing;
    }

    /**
     * 是否执行更新判定钩子（如猎聘列表数据不覆盖已有详情数据）
     */
    protected boolean shouldUpdate(Company existingCompany, CompanyFieldsDTO fields) {
        return true;
    }

    // =========================================================================
    // 通用工具辅助方法
    // =========================================================================

    protected String getString(Map<?, ?> map, String... keys) {
        if (map == null) return "";
        for (String key : keys) {
            Object val = map.get(key);
            if (val != null) {
                String s = String.valueOf(val).trim();
                if (!s.isEmpty() && !"null".equalsIgnoreCase(s) && !"undefined".equalsIgnoreCase(s)) {
                    return s;
                }
            }
        }
        return "";
    }

    protected Map<?, ?> getMap(Map<?, ?> map, String... keys) {
        if (map == null) return Collections.emptyMap();
        for (String key : keys) {
            Object val = map.get(key);
            if (val instanceof Map<?, ?> m) {
                return m;
            }
        }
        return Collections.emptyMap();
    }

    protected String toJson(Object obj) {
        if (obj == null) return "{}";
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}
