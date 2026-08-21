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
import com.jobpilot.jobpilotserver.strategy.sync.company.CompanyFieldsDTO;
import com.jobpilot.jobpilotserver.strategy.sync.company.CompanyProcessorFactory;
import com.jobpilot.jobpilotserver.strategy.sync.company.CompanyProcessorStrategy;
import com.jobpilot.jobpilotserver.strategy.sync.job.JobExtractResultDTO;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 多平台异构数据同步与清洗聚合业务实现类 (Facade Pattern)
 * <p>
 * 作为数据同步业务门面，组合调度：
 * 1. 全量配置大包同步管道 (SyncPipelineExecutor)；
 * 2. 平台职位提取策略工厂 (JobExtractorFactory) 进行批量解析与高效批量落库；
 * 3. 平台企业处理策略工厂 (CompanyProcessorFactory) 进行批量企业解析与落库；
 * 4. 职位深度详情 (JobDetail) 与状态机批量联动；
 * 5. 智联富化缓存批量持久化。
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
     * 实时/批量职位抓取数据同步 (POST /api/jobs) - 高效批量 Batch 模式
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncJobs(List<Map<String, Object>> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return Map.of("success", true, "message", "Successfully processed 0 out of 0 jobs.");
        }

        LocalDateTime now = LocalDateTime.now();
        List<JobExtractResultDTO> extractedDtos = new ArrayList<>();

        // 1. 内存批量解析异构平台职位数据
        for (Map<String, Object> jobMap : jobs) {
            try {
                String rawPlatform = extractPlatform(jobMap);
                JobExtractorStrategy extractor = jobExtractorFactory.getExtractor(rawPlatform);
                JobExtractResultDTO dto = extractor.extract(jobMap, rawPlatform);
                if (dto != null && !dto.isEmpty()) {
                    extractedDtos.add(dto);
                }
            } catch (Exception e) {
                log.error("Failed to extract single job entry: {}", jobMap, e);
            }
        }

        if (extractedDtos.isEmpty()) {
            return Map.of("success", true, "message", String.format("Successfully processed 0 out of %d jobs.", jobs.size()));
        }

        // 2. 一次性批量预查 Job 记录
        List<String> jobIds = extractedDtos.stream().map(JobExtractResultDTO::getJobId).filter(Objects::nonNull).distinct().toList();
        Map<String, Job> existingJobMap = new HashMap<>();
        if (!jobIds.isEmpty()) {
            List<Job> existingJobs = jobMapper.selectList(new LambdaQueryWrapper<Job>().in(Job::getJobId, jobIds));
            for (Job ej : existingJobs) {
                existingJobMap.put(ej.getPlatform() + "_" + ej.getJobId(), ej);
            }
        }

        // 3. 构建待更新/待插入的 Job 实体
        List<Job> jobsToInsert = new ArrayList<>();
        List<Job> jobsToUpdate = new ArrayList<>();
        Set<String> compNamesToQuery = new HashSet<>();
        Set<String> compIdsToQuery = new HashSet<>();

        for (JobExtractResultDTO dto : extractedDtos) {
            String cleanName = CompanyNameCleaner.clean(dto.getCompanyName());
            String cleanFullName = CompanyNameCleaner.clean(dto.getCompanyFullName());
            String companyId = dto.getCompanyId() != null ? dto.getCompanyId().trim() : "";
            String effectivePlatform = dto.getPlatform() != null ? dto.getPlatform() : "未知";

            if (!cleanName.isEmpty()) compNamesToQuery.add(cleanName);
            if (!companyId.isEmpty()) compIdsToQuery.add(companyId);

            Job existing = existingJobMap.get(effectivePlatform + "_" + dto.getJobId());
            if (existing != null) {
                existing.setTitle(dto.getTitle());
                existing.setCompanyName(cleanName);
                existing.setCompanyFullName(!cleanFullName.isEmpty() ? cleanFullName : null);
                if (!companyId.isEmpty()) existing.setCompanyId(companyId);
                existing.setSalary(dto.getSalary());
                existing.setLocation(dto.getLocation());
                existing.setEducation(dto.getEducation());
                if (dto.getRawData2() != null) existing.setRawData2(dto.getRawData2());
                if (dto.getRawData() != null) existing.setRawData(dto.getRawData());
                fillJobDisplayFields(existing, dto);
                existing.setUpdatedAt(now);
                jobsToUpdate.add(existing);
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
                newJob.setDataSource(dto.getDataSource() != null ? dto.getDataSource() : "unknown");
                newJob.setStatus("normal");
                newJob.setRawData(dto.getRawData() != null ? dto.getRawData() : "{}");
                newJob.setRawData2(dto.getRawData2());
                fillJobDisplayFields(newJob, dto);
                newJob.setCreatedAt(now);
                newJob.setUpdatedAt(now);
                jobsToInsert.add(newJob);
                existingJobMap.put(effectivePlatform + "_" + dto.getJobId(), newJob);
            }
        }

        // 4. 执行 Job 批量落库
        int processedCount = 0;
        for (Job j : jobsToInsert) {
            jobMapper.insert(j);
            processedCount++;
        }
        for (Job j : jobsToUpdate) {
            jobMapper.updateById(j);
            processedCount++;
        }

        // 5. 批量预查与维护 Company 表
        Map<String, Company> existingCompMap = new HashMap<>();
        if (!compNamesToQuery.isEmpty() || !compIdsToQuery.isEmpty()) {
            LambdaQueryWrapper<Company> compWrapper = new LambdaQueryWrapper<>();
            if (!compNamesToQuery.isEmpty() && !compIdsToQuery.isEmpty()) {
                compWrapper.in(Company::getCompanyName, compNamesToQuery).or().in(Company::getCompanyId, compIdsToQuery);
            } else if (!compNamesToQuery.isEmpty()) {
                compWrapper.in(Company::getCompanyName, compNamesToQuery);
            } else {
                compWrapper.in(Company::getCompanyId, compIdsToQuery);
            }
            for (Company c : companyMapper.selectList(compWrapper)) {
                if (c.getCompanyId() != null && !c.getCompanyId().isEmpty()) {
                    existingCompMap.put(c.getSourcePlatform() + "_id_" + c.getCompanyId(), c);
                }
                if (c.getCompanyName() != null && !c.getCompanyName().isEmpty()) {
                    existingCompMap.put(c.getSourcePlatform() + "_name_" + c.getCompanyName(), c);
                }
            }
        }

        for (JobExtractResultDTO dto : extractedDtos) {
            String cleanName = CompanyNameCleaner.clean(dto.getCompanyName());
            String cleanFullName = CompanyNameCleaner.clean(dto.getCompanyFullName());
            String companyId = dto.getCompanyId() != null ? dto.getCompanyId().trim() : "";
            String effectivePlatform = dto.getPlatform() != null ? dto.getPlatform() : "未知";

            if (cleanName.isEmpty() && companyId.isEmpty()) continue;

            Company existing = null;
            if (!companyId.isEmpty()) {
                existing = existingCompMap.get(effectivePlatform + "_id_" + companyId);
            }
            if (existing == null && !cleanName.isEmpty()) {
                existing = existingCompMap.get(effectivePlatform + "_name_" + cleanName);
            }

            if (existing == null && !cleanName.isEmpty()) {
                Company c = new Company();
                c.setId(UUID.randomUUID().toString());
                c.setCompanyName(cleanName);
                c.setCompanyFullName(!cleanFullName.isEmpty() ? cleanFullName : null);
                c.setSourcePlatform(effectivePlatform);
                c.setCompanyId(!companyId.isEmpty() ? companyId : null);
                c.setRawData(dto.getRawData() != null ? dto.getRawData() : "{}");
                c.setIndustry(nullIfEmpty(dto.getCompanyIndustry()));
                c.setScale(nullIfEmpty(dto.getCompanyScale()));
                c.setStage(nullIfEmpty(dto.getCompanyStage()));
                c.setCompanyDesc(nullIfEmpty(dto.getCompanyDesc()));
                c.setWelfare(nullIfEmpty(dto.getCompanyWelfare()));
                c.setCreatedAt(now);
                c.setUpdatedAt(now);
                companyMapper.insert(c);
                if (!companyId.isEmpty()) existingCompMap.put(effectivePlatform + "_id_" + companyId, c);
                existingCompMap.put(effectivePlatform + "_name_" + cleanName, c);
            } else if (existing != null) {
                boolean needUpdate = false;
                if (existing.getIndustry() == null && dto.getCompanyIndustry() != null && !dto.getCompanyIndustry().isEmpty()) {
                    existing.setIndustry(dto.getCompanyIndustry());
                    needUpdate = true;
                }
                if (existing.getScale() == null && dto.getCompanyScale() != null && !dto.getCompanyScale().isEmpty()) {
                    existing.setScale(dto.getCompanyScale());
                    needUpdate = true;
                }
                if (existing.getStage() == null && dto.getCompanyStage() != null && !dto.getCompanyStage().isEmpty()) {
                    existing.setStage(dto.getCompanyStage());
                    needUpdate = true;
                }
                if (existing.getCompanyDesc() == null && dto.getCompanyDesc() != null && !dto.getCompanyDesc().isEmpty()) {
                    existing.setCompanyDesc(dto.getCompanyDesc());
                    needUpdate = true;
                }
                if (existing.getWelfare() == null && dto.getCompanyWelfare() != null && !dto.getCompanyWelfare().isEmpty()) {
                    existing.setWelfare(dto.getCompanyWelfare());
                    needUpdate = true;
                }
                if (needUpdate) {
                    existing.setUpdatedAt(now);
                    companyMapper.updateById(existing);
                }
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", String.format("Successfully processed %d out of %d jobs.", processedCount, jobs.size()));
        return res;
    }

    /**
     * 实时/批量企业信息同步 (POST /api/companies) - 高效批量 Batch 模式
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncCompanies(List<Map<String, Object>> companies) {
        if (companies == null || companies.isEmpty()) {
            return Map.of("success", true, "message", "Successfully processed 0 out of 0 companies.");
        }

        LocalDateTime now = LocalDateTime.now();
        List<CompanyBatchItem> parsedList = new ArrayList<>();
        Set<String> compNamesToQuery = new HashSet<>();
        Set<String> compIdsToQuery = new HashSet<>();

        // 1. 内存批量解析企业字段
        for (Map<String, Object> company : companies) {
            try {
                String rawPlatform = extractPlatform(company);
                CompanyProcessorStrategy processor = companyProcessorFactory.getProcessor(rawPlatform);
                CompanyFieldsDTO fields = processor.extractFields(company, rawPlatform);
                if (fields == null || fields.isEmpty()) continue;

                String cName = CompanyNameCleaner.clean(fields.getCompanyName());
                String cFullName = CompanyNameCleaner.clean(fields.getCompanyFullName());
                String companyId = fields.getCompanyId() != null ? fields.getCompanyId().trim() : "";
                String finalCompanyName = !cFullName.isEmpty() ? cFullName : cName;

                if (finalCompanyName.isEmpty() && companyId.isEmpty()) continue;

                String standardizedPlatform = processor.getStandardizedPlatform(rawPlatform);
                String stringifiedData = toJson(fields.getRawData() != null ? fields.getRawData() : company);

                if (!companyId.isEmpty()) compIdsToQuery.add(companyId);
                if (!finalCompanyName.isEmpty()) compNamesToQuery.add(finalCompanyName);
                if (!cName.isEmpty()) compNamesToQuery.add(cName);
                if (!cFullName.isEmpty()) compNamesToQuery.add(cFullName);

                parsedList.add(new CompanyBatchItem(fields, cName, cFullName, companyId, finalCompanyName, standardizedPlatform, stringifiedData));
            } catch (Exception e) {
                log.error("Failed to extract single company: {}", company, e);
            }
        }

        // 2. 批量预查已有 Company
        Map<String, Company> existingCompMap = new HashMap<>();
        if (!compNamesToQuery.isEmpty() || !compIdsToQuery.isEmpty()) {
            LambdaQueryWrapper<Company> compWrapper = new LambdaQueryWrapper<>();
            if (!compNamesToQuery.isEmpty() && !compIdsToQuery.isEmpty()) {
                compWrapper.in(Company::getCompanyName, compNamesToQuery).or().in(Company::getCompanyId, compIdsToQuery);
            } else if (!compNamesToQuery.isEmpty()) {
                compWrapper.in(Company::getCompanyName, compNamesToQuery);
            } else {
                compWrapper.in(Company::getCompanyId, compIdsToQuery);
            }
            for (Company c : companyMapper.selectList(compWrapper)) {
                if (c.getCompanyId() != null && !c.getCompanyId().isEmpty()) {
                    existingCompMap.put(c.getSourcePlatform() + "_id_" + c.getCompanyId(), c);
                }
                if (c.getCompanyName() != null && !c.getCompanyName().isEmpty()) {
                    existingCompMap.put(c.getSourcePlatform() + "_name_" + c.getCompanyName(), c);
                }
                if (c.getCompanyFullName() != null && !c.getCompanyFullName().isEmpty()) {
                    existingCompMap.put(c.getSourcePlatform() + "_name_" + c.getCompanyFullName(), c);
                }
            }
        }

        int successCount = 0;
        for (CompanyBatchItem item : parsedList) {
            Company existing = null;
            if (!item.companyId.isEmpty()) {
                existing = existingCompMap.get(item.standardizedPlatform + "_id_" + item.companyId);
            }
            if (existing == null && !item.finalCompanyName.isEmpty()) {
                existing = existingCompMap.get(item.standardizedPlatform + "_name_" + item.finalCompanyName);
            }
            if (existing == null && !item.cFullName.isEmpty()) {
                existing = existingCompMap.get(item.standardizedPlatform + "_name_" + item.cFullName);
            }
            if (existing == null && !item.cName.isEmpty()) {
                existing = existingCompMap.get(item.standardizedPlatform + "_name_" + item.cName);
            }

            if (existing != null) {
                existing.setRawData2(item.stringifiedData);
                if (!item.companyId.isEmpty()) existing.setCompanyId(item.companyId);
                if (!item.cFullName.isEmpty()) existing.setCompanyFullName(item.cFullName);
                if (!item.cName.isEmpty()) existing.setCompanyName(item.cName);
                existing.setUpdatedAt(now);
                companyMapper.updateById(existing);
            } else if (!item.finalCompanyName.isEmpty()) {
                Company newComp = new Company();
                newComp.setId(UUID.randomUUID().toString());
                newComp.setCompanyName(item.finalCompanyName);
                newComp.setCompanyFullName(!item.cFullName.isEmpty() ? item.cFullName : null);
                newComp.setSourcePlatform(item.standardizedPlatform);
                newComp.setCompanyId(!item.companyId.isEmpty() ? item.companyId : null);
                newComp.setRawData2(item.stringifiedData);
                newComp.setCreatedAt(now);
                newComp.setUpdatedAt(now);
                companyMapper.insert(newComp);
                if (!item.companyId.isEmpty()) existingCompMap.put(item.standardizedPlatform + "_id_" + item.companyId, newComp);
                existingCompMap.put(item.standardizedPlatform + "_name_" + item.finalCompanyName, newComp);
            }

            successCount++;
        }

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", String.format("Successfully processed %d out of %d companies.", successCount, companies.size()));
        return res;
    }

    /**
     * 实时/批量职位深度详情同步 (POST /api/job-details) - 高效批量 Batch 模式
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncJobDetails(List<Map<String, Object>> details) {
        if (details == null || details.isEmpty()) {
            return Map.of("success", true, "message", "Successfully processed 0 out of 0 job details.");
        }

        LocalDateTime now = LocalDateTime.now();
        List<JobDetailBatchItem> parsedList = new ArrayList<>();
        Set<String> jobIds = new HashSet<>();

        // 1. 内存批量解析职位详情
        for (Map<String, Object> detail : details) {
            try {
                String rawPlatform = extractPlatform(detail);
                if ("未知".equals(rawPlatform) && detail.containsKey("职位ID") && detail.containsKey("职位名称") && detail.containsKey("公司名称")) {
                    rawPlatform = "Boss直聘";
                }

                String jobId = getString(detail, "职位ID", "jobId", "id", "job_id");
                if (jobId.isEmpty()) continue;

                String jobTitle = getString(detail, "职位名称", "title", "jobName");
                String companyName = CompanyNameCleaner.clean(getString(detail, "公司名称", "companyName"));
                String companyFullName = CompanyNameCleaner.clean(getString(detail, "公司全称", "companyFullName", "fullCompanyName"));
                String jobStatus = getString(detail, "招聘状态", "jobStatus", "positionStatus");
                String companyId = getString(detail, "公司ID", "companyId");

                boolean isInvalidPage = companyFullName.isEmpty() && jobTitle.isEmpty() && companyName.isEmpty();
                String stringifiedData = toJson(detail);

                jobIds.add(jobId);
                parsedList.add(new JobDetailBatchItem(jobId, rawPlatform, jobTitle, companyName, companyFullName, jobStatus, companyId, isInvalidPage, stringifiedData));
            } catch (Exception e) {
                log.error("Failed to parse single job detail: {}", detail, e);
            }
        }

        // 2. 批量预查已有 JobDetail
        Map<String, JobDetail> existingDetailMap = new HashMap<>();
        if (!jobIds.isEmpty()) {
            List<JobDetail> existingDetails = jobDetailMapper.selectList(new LambdaQueryWrapper<JobDetail>().in(JobDetail::getJobId, jobIds));
            for (JobDetail jd : existingDetails) {
                existingDetailMap.put(jd.getPlatform() + "_" + jd.getJobId(), jd);
            }
        }

        int successCount = 0;
        List<String> expiredJobIds = new ArrayList<>();

        for (JobDetailBatchItem item : parsedList) {
            String stringifiedData = item.stringifiedData;
            JobDetail existingDetail = existingDetailMap.get(item.rawPlatform + "_" + item.jobId);

            if (item.isInvalidPage && existingDetail != null && existingDetail.getRawData() != null) {
                try {
                    Map<String, Object> oldMap = objectMapper.readValue(existingDetail.getRawData(), Map.class);
                    oldMap.put("招聘状态", "职位已失效");
                    stringifiedData = objectMapper.writeValueAsString(oldMap);
                } catch (Exception ignored) {}
            }

            if (existingDetail != null) {
                existingDetail.setRawData(stringifiedData);
                existingDetail.setUpdatedAt(now);
                jobDetailMapper.updateById(existingDetail);
            } else {
                JobDetail jd = new JobDetail();
                jd.setId(UUID.randomUUID().toString());
                jd.setJobId(item.jobId);
                jd.setPlatform(item.rawPlatform);
                jd.setRawData(stringifiedData);
                jd.setCreatedAt(now);
                jd.setUpdatedAt(now);
                jobDetailMapper.insert(jd);
                existingDetailMap.put(item.rawPlatform + "_" + item.jobId, jd);
            }

            // 联动更新 Job 表中的 companyFullName
            if (!item.companyFullName.isEmpty()) {
                jobMapper.update(null, new LambdaUpdateWrapper<Job>()
                        .eq(Job::getJobId, item.jobId)
                        .eq(Job::getPlatform, item.rawPlatform)
                        .set(Job::getCompanyFullName, item.companyFullName)
                        .set(Job::getUpdatedAt, now));
            }

            // 联动收集失效职位列表
            if (item.isInvalidPage || "职位已关闭".equals(item.jobStatus) || "职位已失效".equals(item.jobStatus)
                    || "4".equals(item.jobStatus) || "停止招聘".equals(item.jobStatus)) {
                expiredJobIds.add(item.jobId);
            }

            // 联动更新 Company 表中的 companyFullName
            if (!item.companyId.isEmpty() && !item.companyFullName.isEmpty()) {
                companyMapper.update(null, new LambdaUpdateWrapper<Company>()
                        .eq(Company::getCompanyId, item.companyId)
                        .eq(Company::getSourcePlatform, item.rawPlatform)
                        .set(Company::getCompanyFullName, item.companyFullName)
                        .set(Company::getUpdatedAt, now));
            }

            successCount++;
        }

        // 批量更新过期状态
        if (!expiredJobIds.isEmpty()) {
            jobMapper.update(null, new LambdaUpdateWrapper<Job>()
                    .in(Job::getJobId, expiredJobIds)
                    .set(Job::getStatus, "expired")
                    .set(Job::getUpdatedAt, now));
        }

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", String.format("Successfully processed %d out of %d job details.", successCount, details.size()));
        return res;
    }

    /**
     * 智联招聘富化缓存同步 (POST /api/zhilian-cache) - 高效批量 Batch 模式
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncZhilianCache(Map<String, Object> cacheMap) {
        if (cacheMap == null || cacheMap.isEmpty()) {
            return Map.of("success", true, "message", "No data to sync.");
        }

        LocalDateTime now = LocalDateTime.now();
        List<String> jobIds = cacheMap.keySet().stream().filter(k -> k != null && !k.isBlank()).toList();
        if (jobIds.isEmpty()) {
            return Map.of("success", true, "message", "No data to sync.");
        }

        // 批量预查已有缓存记录
        List<ZhilianEnrichmentCache> existingList = zhilianCacheMapper.selectBatchIds(jobIds);
        Map<String, ZhilianEnrichmentCache> existingMap = existingList.stream()
                .collect(Collectors.toMap(ZhilianEnrichmentCache::getJobId, Function.identity(), (a, b) -> a));

        int successCount = 0;
        for (String jobId : jobIds) {
            Object rawDataObj = cacheMap.get(jobId);
            if (rawDataObj == null) continue;

            try {
                String rawDataStr = toJson(rawDataObj);
                ZhilianEnrichmentCache existing = existingMap.get(jobId);
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
                    existingMap.put(jobId, zc);
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
    // 内部辅助方法与数据结构
    // =========================================================================

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

    private String nullIfEmpty(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

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

    private static class CompanyBatchItem {
        final CompanyFieldsDTO fields;
        final String cName;
        final String cFullName;
        final String companyId;
        final String finalCompanyName;
        final String standardizedPlatform;
        final String stringifiedData;

        CompanyBatchItem(CompanyFieldsDTO fields, String cName, String cFullName, String companyId, String finalCompanyName, String standardizedPlatform, String stringifiedData) {
            this.fields = fields;
            this.cName = cName;
            this.cFullName = cFullName;
            this.companyId = companyId;
            this.finalCompanyName = finalCompanyName;
            this.standardizedPlatform = standardizedPlatform;
            this.stringifiedData = stringifiedData;
        }
    }

    private static class JobDetailBatchItem {
        final String jobId;
        final String rawPlatform;
        final String jobTitle;
        final String companyName;
        final String companyFullName;
        final String jobStatus;
        final String companyId;
        final boolean isInvalidPage;
        final String stringifiedData;

        JobDetailBatchItem(String jobId, String rawPlatform, String jobTitle, String companyName, String companyFullName, String jobStatus, String companyId, boolean isInvalidPage, String stringifiedData) {
            this.jobId = jobId;
            this.rawPlatform = rawPlatform;
            this.jobTitle = jobTitle;
            this.companyName = companyName;
            this.companyFullName = companyFullName;
            this.jobStatus = jobStatus;
            this.companyId = companyId;
            this.isInvalidPage = isInvalidPage;
            this.stringifiedData = stringifiedData;
        }
    }
}
