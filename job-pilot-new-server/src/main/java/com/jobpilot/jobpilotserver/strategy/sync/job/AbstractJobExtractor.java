package com.jobpilot.jobpilotserver.strategy.sync.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.model.entity.Company;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.util.CompanyNameCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * 平台职位提取与持久化模板抽象基类 (Template Method Pattern)
 */
public abstract class AbstractJobExtractor implements JobExtractorStrategy {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final JobMapper jobMapper;
    protected final CompanyMapper companyMapper;
    protected final ObjectMapper objectMapper;

    public AbstractJobExtractor(JobMapper jobMapper,
                                CompanyMapper companyMapper,
                                ObjectMapper objectMapper) {
        this.jobMapper = jobMapper;
        this.companyMapper = companyMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * 核心模板方法：提取职位 -> 清洗规范化 -> Upsert Job -> 初始化 Company
     */
    @Override
    public final void extractAndSave(Map<?, ?> job, String platform) {
        if (job == null || job.isEmpty()) {
            return;
        }

        JobExtractResultDTO dto = extract(job, platform);
        if (dto == null || dto.isEmpty()) {
            return;
        }

        String cleanName = CompanyNameCleaner.clean(dto.getCompanyName());
        String cleanFullName = CompanyNameCleaner.clean(dto.getCompanyFullName());
        String companyId = dto.getCompanyId() != null ? dto.getCompanyId().trim() : "";
        String effectivePlatform = dto.getPlatform() != null ? dto.getPlatform() : platform;

        LocalDateTime now = LocalDateTime.now();

        // 1. Upsert Job 表
        Job existingJob = jobMapper.selectOne(new LambdaQueryWrapper<Job>()
                .eq(Job::getJobId, dto.getJobId())
                .eq(Job::getPlatform, effectivePlatform));

        if (existingJob != null) {
            existingJob.setTitle(dto.getTitle());
            existingJob.setCompanyName(cleanName);
            existingJob.setCompanyFullName(cleanFullName);
            if (!companyId.isEmpty()) existingJob.setCompanyId(companyId);
            existingJob.setSalary(dto.getSalary());
            existingJob.setLocation(dto.getLocation());
            existingJob.setEducation(dto.getEducation());
            if (dto.getRawData2() != null) existingJob.setRawData2(dto.getRawData2());
            if (dto.getRawData() != null) existingJob.setRawData(dto.getRawData());
            // 更新新增展示字段
            fillJobDisplayFields(existingJob, dto);
            existingJob.setUpdatedAt(now);
            jobMapper.updateById(existingJob);
        } else {
            Job newJob = new Job();
            newJob.setId(UUID.randomUUID().toString());
            newJob.setJobId(dto.getJobId());
            newJob.setTitle(dto.getTitle());
            newJob.setCompanyName(cleanName);
            newJob.setCompanyFullName(!cleanFullName.isEmpty() ? cleanFullName : null);
            newJob.setCompanyId(!companyId.isEmpty() ? companyId : null);
            newJob.setSalary(dto.getSalary());
            newJob.setLocation(dto.getLocation());
            newJob.setEducation(dto.getEducation());
            newJob.setPlatform(effectivePlatform);
            newJob.setDataSource(dto.getDataSource());
            newJob.setStatus("normal");
            newJob.setRawData(dto.getRawData() != null ? dto.getRawData() : "{}");
            newJob.setRawData2(dto.getRawData2());
            // 填充新增展示字段
            fillJobDisplayFields(newJob, dto);
            newJob.setCreatedAt(now);
            newJob.setUpdatedAt(now);
            jobMapper.insert(newJob);
        }

        // 2. 初始化写入 Company 表（按 ID 或名称查重，仅在不存在时插入初始记录）
        if (!cleanName.isEmpty() || !companyId.isEmpty()) {
            Company existingCompany = null;
            if (!companyId.isEmpty()) {
                existingCompany = companyMapper.selectOne(new LambdaQueryWrapper<Company>()
                        .eq(Company::getCompanyId, companyId)
                        .eq(Company::getSourcePlatform, effectivePlatform));
            }
            if (existingCompany == null && !cleanName.isEmpty()) {
                existingCompany = companyMapper.selectOne(new LambdaQueryWrapper<Company>()
                        .eq(Company::getCompanyName, cleanName)
                        .eq(Company::getSourcePlatform, effectivePlatform));
            }

            if (existingCompany == null && !cleanName.isEmpty()) {
                Company c = new Company();
                c.setId(UUID.randomUUID().toString());
                c.setCompanyName(cleanName);
                c.setCompanyFullName(!cleanFullName.isEmpty() ? cleanFullName : null);
                c.setSourcePlatform(effectivePlatform);
                c.setCompanyId(!companyId.isEmpty() ? companyId : null);
                c.setRawData(dto.getRawData() != null ? dto.getRawData() : toJson(job));
                // 填充新增公司展示字段
                c.setIndustry(nullIfEmpty(dto.getCompanyIndustry()));
                c.setScale(nullIfEmpty(dto.getCompanyScale()));
                c.setStage(nullIfEmpty(dto.getCompanyStage()));
                c.setCompanyDesc(nullIfEmpty(dto.getCompanyDesc()));
                c.setWelfare(nullIfEmpty(dto.getCompanyWelfare()));
                c.setCreatedAt(now);
                c.setUpdatedAt(now);
                companyMapper.insert(c);
            } else if (existingCompany != null) {
                // 已有公司记录时，补填可能缺失的展示字段
                boolean needUpdate = false;
                if (existingCompany.getIndustry() == null && dto.getCompanyIndustry() != null && !dto.getCompanyIndustry().isEmpty()) {
                    existingCompany.setIndustry(dto.getCompanyIndustry());
                    needUpdate = true;
                }
                if (existingCompany.getScale() == null && dto.getCompanyScale() != null && !dto.getCompanyScale().isEmpty()) {
                    existingCompany.setScale(dto.getCompanyScale());
                    needUpdate = true;
                }
                if (existingCompany.getStage() == null && dto.getCompanyStage() != null && !dto.getCompanyStage().isEmpty()) {
                    existingCompany.setStage(dto.getCompanyStage());
                    needUpdate = true;
                }
                if (existingCompany.getCompanyDesc() == null && dto.getCompanyDesc() != null && !dto.getCompanyDesc().isEmpty()) {
                    existingCompany.setCompanyDesc(dto.getCompanyDesc());
                    needUpdate = true;
                }
                if (existingCompany.getWelfare() == null && dto.getCompanyWelfare() != null && !dto.getCompanyWelfare().isEmpty()) {
                    existingCompany.setWelfare(dto.getCompanyWelfare());
                    needUpdate = true;
                }
                if (needUpdate) {
                    existingCompany.setUpdatedAt(now);
                    companyMapper.updateById(existingCompany);
                }
            }
        }
    }

    /**
     * 将 DTO 中的展示字段填充到 Job 实体
     */
    private void fillJobDisplayFields(Job job, JobExtractResultDTO dto) {
        job.setExperience(nullIfEmpty(dto.getExperience()));
        job.setCity(nullIfEmpty(dto.getCity()));
        job.setArea(nullIfEmpty(dto.getArea()));
        job.setBusinessDistrict(nullIfEmpty(dto.getBusinessDistrict()));
        job.setJobUrl(nullIfEmpty(dto.getJobUrl()));
        job.setJobDesc(nullIfEmpty(dto.getJobDesc()));
        job.setSkills(nullIfEmpty(dto.getSkills()));
        job.setWelfareList(nullIfEmpty(dto.getWelfareList()));
        job.setHrName(nullIfEmpty(dto.getHrName()));
        job.setHrPosition(nullIfEmpty(dto.getHrPosition()));
        job.setPublishDate(nullIfEmpty(dto.getPublishDate()));
        job.setUpdateDate(nullIfEmpty(dto.getUpdateDate()));
        job.setIsHeadhunter(dto.isHeadhunter());
        job.setClientCompanyName(nullIfEmpty(dto.getClientCompanyName()));
    }

    // =========================================================================
    // 基础工具辅助方法
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

    /**
     * 将列表转为 JSON 数组字符串
     */
    protected String toJsonArray(java.util.List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通用标签提取（复用自 BossJobNormalizer 的逻辑）
     */
    protected java.util.List<String> extractTagsFromMap(Map<?, ?> map, String... keys) {
        java.util.List<String> result = new java.util.ArrayList<>();
        for (String key : keys) {
            Object val = map != null ? map.get(key) : null;
            if (val instanceof java.util.List<?> listVal) {
                for (Object item : listVal) {
                    if (item instanceof String s && !s.trim().isEmpty()) {
                        result.add(s.trim());
                    } else if (item instanceof Map<?, ?> m) {
                        Object name = m.get("name");
                        if (name != null && !String.valueOf(name).trim().isEmpty()) {
                            result.add(String.valueOf(name).trim());
                        }
                    }
                }
                if (!result.isEmpty()) break;
            } else if (val instanceof String s && !s.trim().isEmpty()) {
                for (String part : s.split(",")) {
                    if (!part.trim().isEmpty()) result.add(part.trim());
                }
                if (!result.isEmpty()) break;
            }
        }
        return result;
    }

    private String nullIfEmpty(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
