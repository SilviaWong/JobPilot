package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobDetailMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.mapper.ZhilianEnrichmentCacheMapper;
import com.jobpilot.jobpilotserver.model.entity.Company;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.entity.JobDetail;
import com.jobpilot.jobpilotserver.model.entity.ZhilianEnrichmentCache;
import com.jobpilot.jobpilotserver.service.DataSyncService;
import com.jobpilot.jobpilotserver.strategy.sync.company.CompanyProcessorFactory;
import com.jobpilot.jobpilotserver.strategy.sync.company.CompanyProcessorStrategy;
import com.jobpilot.jobpilotserver.strategy.sync.job.JobExtractorFactory;
import com.jobpilot.jobpilotserver.strategy.sync.job.JobExtractorStrategy;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncPipelineExecutor;
import com.jobpilot.jobpilotserver.util.CompanyNameCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 多平台异构数据同步与清洗聚合业务实现类 (Facade Pattern)
 * <p>
 * 作为数据同步业务门面，组合调度：
 * 1. 全量配置大包同步管道 (SyncPipelineExecutor)；
 * 2. 平台职位提取策略工厂 (JobExtractorFactory)；
 * 3. 平台企业处理策略工厂 (CompanyProcessorFactory)；
 * 4. 职位深度详情 (JobDetail) 与状态机联动；
 * 5. 智联富化缓存持久化。
 */
@Service
public class DataSyncServiceImpl implements DataSyncService {

    private static final Logger log = LoggerFactory.getLogger(DataSyncServiceImpl.class);

    private final SyncPipelineExecutor syncPipelineExecutor;
    private final JobExtractorFactory jobExtractorFactory;
    private final CompanyProcessorFactory companyProcessorFactory;
    private final JobDetailMapper jobDetailMapper;
    private final JobMapper jobMapper;
    private final CompanyMapper companyMapper;
    private final ZhilianEnrichmentCacheMapper zhilianCacheMapper;
    private final ObjectMapper objectMapper;

    public DataSyncServiceImpl(SyncPipelineExecutor syncPipelineExecutor,
                               JobExtractorFactory jobExtractorFactory,
                               CompanyProcessorFactory companyProcessorFactory,
                               JobDetailMapper jobDetailMapper,
                               JobMapper jobMapper,
                               CompanyMapper companyMapper,
                               ZhilianEnrichmentCacheMapper zhilianCacheMapper,
                               ObjectMapper objectMapper) {
        this.syncPipelineExecutor = syncPipelineExecutor;
        this.jobExtractorFactory = jobExtractorFactory;
        this.companyProcessorFactory = companyProcessorFactory;
        this.jobDetailMapper = jobDetailMapper;
        this.jobMapper = jobMapper;
        this.companyMapper = companyMapper;
        this.zhilianCacheMapper = zhilianCacheMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * 全量数据同步接口 (POST /api/sync-all)
     * 委托至责任链管道执行器 (SyncPipelineExecutor)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncAll(Map<String, Object> payload) {
        return syncPipelineExecutor.execute(payload);
    }

    /**
     * 实时/批量职位抓取数据同步 (POST /api/jobs)
     * 委托至平台职位提取策略 (JobExtractorStrategy)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncJobs(List<Map<String, Object>> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return Map.of("success", true, "message", "Successfully processed 0 out of 0 jobs.");
        }

        int successCount = 0;
        for (Map<String, Object> jobMap : jobs) {
            try {
                String rawPlatform = extractPlatform(jobMap);
                JobExtractorStrategy extractor = jobExtractorFactory.getExtractor(rawPlatform);
                extractor.extractAndSave(jobMap, rawPlatform);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to process single job entry: {}", jobMap, e);
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", String.format("Successfully processed %d out of %d jobs.", successCount, jobs.size()));
        return res;
    }

    /**
     * 实时/批量企业信息同步 (POST /api/companies)
     * 委托至平台企业处理策略 (CompanyProcessorStrategy)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncCompanies(List<Map<String, Object>> companies) {
        if (companies == null || companies.isEmpty()) {
            return Map.of("success", true, "message", "Successfully processed 0 out of 0 companies.");
        }

        int successCount = 0;
        for (Map<String, Object> company : companies) {
            try {
                String rawPlatform = extractPlatform(company);
                CompanyProcessorStrategy processor = companyProcessorFactory.getProcessor(rawPlatform);
                processor.process(company, rawPlatform);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to process single company: {}", company, e);
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", String.format("Successfully processed %d out of %d companies.", successCount, companies.size()));
        return res;
    }

    /**
     * 实时/批量职位深度详情同步 (POST /api/job-details)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncJobDetails(List<Map<String, Object>> details) {
        if (details == null || details.isEmpty()) {
            return Map.of("success", true, "message", "Successfully processed 0 out of 0 job details.");
        }

        int successCount = 0;
        LocalDateTime now = LocalDateTime.now();

        for (Map<String, Object> detail : details) {
            try {
                String rawPlatform = extractPlatform(detail);
                if ("未知".equals(rawPlatform) && detail.containsKey("职位ID") && detail.containsKey("职位名称") && detail.containsKey("公司名称")) {
                    rawPlatform = "Boss直聘";
                }

                String jobId = getString(detail, "职位ID", "jobId", "id", "job_id");
                if (jobId.isEmpty()) {
                    continue;
                }

                String jobTitle = getString(detail, "职位名称", "title", "jobName");
                String companyName = CompanyNameCleaner.clean(getString(detail, "公司名称", "companyName"));
                String companyFullName = CompanyNameCleaner.clean(getString(detail, "公司全称", "companyFullName", "fullCompanyName"));
                String jobStatus = getString(detail, "招聘状态", "jobStatus", "positionStatus");
                String companyId = getString(detail, "公司ID", "companyId");

                // 判断是否为异常/失效页面
                boolean isInvalidPage = companyFullName.isEmpty() && jobTitle.isEmpty() && companyName.isEmpty();

                String stringifiedData = toJson(detail);

                // 若为失效页面，尝试保留旧详情内容仅更新状态
                if (isInvalidPage) {
                    JobDetail existingDetail = jobDetailMapper.selectOne(new LambdaQueryWrapper<JobDetail>()
                            .eq(JobDetail::getJobId, jobId)
                            .eq(JobDetail::getPlatform, rawPlatform));
                    if (existingDetail != null && existingDetail.getRawData() != null) {
                        try {
                            Map<String, Object> oldMap = objectMapper.readValue(existingDetail.getRawData(), Map.class);
                            oldMap.put("招聘状态", "职位已失效");
                            stringifiedData = objectMapper.writeValueAsString(oldMap);
                        } catch (Exception ignored) {}
                    }
                }

                // 1. 保存/更新 JobDetail
                JobDetail existingDetail = jobDetailMapper.selectOne(new LambdaQueryWrapper<JobDetail>()
                        .eq(JobDetail::getJobId, jobId)
                        .eq(JobDetail::getPlatform, rawPlatform));
                if (existingDetail != null) {
                    existingDetail.setRawData(stringifiedData);
                    existingDetail.setUpdatedAt(now);
                    jobDetailMapper.updateById(existingDetail);
                } else {
                    JobDetail jd = new JobDetail();
                    jd.setId(UUID.randomUUID().toString());
                    jd.setJobId(jobId);
                    jd.setPlatform(rawPlatform);
                    jd.setRawData(stringifiedData);
                    jd.setCreatedAt(now);
                    jd.setUpdatedAt(now);
                    jobDetailMapper.insert(jd);
                }

                // 2. 联动更新 Job 表中的 companyFullName
                if (!companyFullName.isEmpty()) {
                    jobMapper.update(null, new LambdaUpdateWrapper<Job>()
                            .eq(Job::getJobId, jobId)
                            .eq(Job::getPlatform, rawPlatform)
                            .set(Job::getCompanyFullName, companyFullName)
                            .set(Job::getUpdatedAt, now));
                }

                // 3. 联动更新 Job 表中的过期状态
                if (isInvalidPage || "职位已关闭".equals(jobStatus) || "职位已失效".equals(jobStatus)
                        || "4".equals(jobStatus) || "停止招聘".equals(jobStatus)) {
                    jobMapper.update(null, new LambdaUpdateWrapper<Job>()
                            .eq(Job::getJobId, jobId)
                            .eq(Job::getPlatform, rawPlatform)
                            .set(Job::getStatus, "expired")
                            .set(Job::getUpdatedAt, now));
                }

                // 4. 联动更新 Company 表中的 companyFullName
                if (!companyId.isEmpty() && !companyFullName.isEmpty()) {
                    companyMapper.update(null, new LambdaUpdateWrapper<Company>()
                            .eq(Company::getCompanyId, companyId)
                            .eq(Company::getSourcePlatform, rawPlatform)
                            .set(Company::getCompanyFullName, companyFullName)
                            .set(Company::getUpdatedAt, now));
                }

                successCount++;
            } catch (Exception e) {
                log.error("Failed to process single job detail: {}", detail, e);
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", String.format("Successfully processed %d out of %d job details.", successCount, details.size()));
        return res;
    }

    /**
     * 智联招聘富化缓存同步 (POST /api/zhilian-cache)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncZhilianCache(Map<String, Object> cacheMap) {
        if (cacheMap == null || cacheMap.isEmpty()) {
            return Map.of("success", true, "message", "No data to sync.");
        }

        int successCount = 0;
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<String, Object> entry : cacheMap.entrySet()) {
            String jobId = entry.getKey();
            Object rawDataObj = entry.getValue();
            if (jobId == null || jobId.isBlank() || rawDataObj == null) {
                continue;
            }

            try {
                String rawDataStr = toJson(rawDataObj);
                ZhilianEnrichmentCache existing = zhilianCacheMapper.selectById(jobId);
                if (existing != null) {
                    existing.setRawData(rawDataStr);
                    existing.setUpdatedAt(now);
                    zhilianCacheMapper.updateById(existing);
                } else {
                    ZhilianEnrichmentCache zc = new ZhilianEnrichmentCache();
                    zc.setJobId(jobId);
                    zc.setRawData(rawDataStr);
                    zc.setCreatedAt(now);
                    zc.setUpdatedAt(now);
                    zhilianCacheMapper.insert(zc);
                }
                successCount++;
            } catch (Exception e) {
                log.error("Failed to upsert ZhilianEnrichmentCache for jobId: {}", jobId, e);
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", String.format("Successfully processed %d out of %d items.", successCount, cacheMap.size()));
        return res;
    }

    // =========================================================================
    // 内部辅助方法
    // =========================================================================

    private String extractPlatform(Map<?, ?> map) {
        String p = getString(map, "platform", "平台");
        String lower = p.toLowerCase();
        if (lower.contains("liepin") || lower.contains("猎聘")) return "猎聘";
        if (lower.contains("zhilian") || lower.contains("智联")) return "智联";
        if (lower.contains("boss") || lower.contains("直聘")) return "Boss直聘";
        if (lower.contains("51job") || lower.contains("前程")) return "51job";
        return p.isEmpty() ? "未知" : p;
    }

    private String getString(Map<?, ?> map, String... keys) {
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

    private String toJson(Object obj) {
        if (obj == null) return "{}";
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}
