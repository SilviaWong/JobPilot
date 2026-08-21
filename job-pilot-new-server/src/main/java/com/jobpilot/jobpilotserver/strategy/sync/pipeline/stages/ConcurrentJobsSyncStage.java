package com.jobpilot.jobpilotserver.strategy.sync.pipeline.stages;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncContext;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * 阶段 6：多平台职位多线程并发提取与清洗入库
 */
@Component
public class ConcurrentJobsSyncStage implements SyncStage {

    private static final Logger log = LoggerFactory.getLogger(ConcurrentJobsSyncStage.class);

    private final JobMapper jobMapper;
    private final ObjectMapper objectMapper;
    private final Executor syncDataExecutor;

    public ConcurrentJobsSyncStage(JobMapper jobMapper,
                                   ObjectMapper objectMapper,
                                   @Qualifier("syncDataExecutor") Executor syncDataExecutor) {
        this.jobMapper = jobMapper;
        this.objectMapper = objectMapper;
        this.syncDataExecutor = syncDataExecutor;
    }

    @Override
    public int getOrder() {
        return 60;
    }

    @Override
    public void handle(Map<String, Object> payload, SyncContext context) {
        Set<String> threadSafeActiveIds = ConcurrentHashMap.newKeySet();

        // 1. Boss直聘异步解析
        CompletableFuture<List<Job>> bossFuture = CompletableFuture.supplyAsync(() -> {
            List<Job> list = new ArrayList<>();
            if (payload.get("boss_scraped_v2") instanceof List<?> bJobs) {
                for (Object o : bJobs) {
                    if (o instanceof Map<?, ?> m) {
                        Job j = new Job();
                        String jobId = (String) m.get("encryptJobId");
                        j.setJobId(jobId);
                        j.setTitle((String) m.get("jobName"));
                        j.setCompanyName((String) m.get("brandName"));
                        j.setSalary((String) m.get("salaryDesc"));
                        j.setLocation((String) m.get("cityName"));
                        j.setEducation((String) m.get("jobDegree"));
                        j.setExperience((String) m.get("jobExperience"));
                        j.setCity((String) m.get("cityName"));
                        j.setArea((String) m.get("areaDistrict"));
                        j.setBusinessDistrict((String) m.get("businessDistrict"));
                        j.setHrName((String) m.get("bossName"));
                        j.setHrPosition((String) m.get("bossTitle"));
                        j.setPlatform("Boss直聘");
                        j.setDataSource("boss_scraped_v2");
                        try { j.setRawData(objectMapper.writeValueAsString(m)); } catch (Exception ignored) {}
                        list.add(j);
                        if (jobId != null) threadSafeActiveIds.add(jobId);
                    }
                }
            }
            return list;
        }, syncDataExecutor);

        // 2. 51job 前程无忧异步解析
        CompletableFuture<List<Job>> job51Future = CompletableFuture.supplyAsync(() -> {
            List<Job> list = new ArrayList<>();
            if (payload.get("51job_scraped") instanceof List<?> j51Jobs) {
                for (Object o : j51Jobs) {
                    if (o instanceof Map<?, ?> m) {
                        Job j = new Job();
                        String jobId = String.valueOf(m.get("jobId"));
                        j.setJobId(jobId);
                        j.setTitle((String) m.get("jobName"));
                        j.setCompanyName((String) m.get("companyName"));
                        j.setSalary((String) m.get("provideSalary"));
                        j.setLocation((String) m.get("jobArea"));
                        j.setEducation((String) m.get("degreeString"));
                        j.setExperience((String) m.get("workYearString"));
                        j.setPlatform("前程无忧");
                        j.setDataSource("51job_scraped");
                        try { j.setRawData(objectMapper.writeValueAsString(m)); } catch (Exception ignored) {}
                        list.add(j);
                        if (jobId != null) threadSafeActiveIds.add(jobId);
                    }
                }
            }
            return list;
        }, syncDataExecutor);

        // 3. 智联招聘异步解析
        CompletableFuture<List<Job>> zhilianFuture = CompletableFuture.supplyAsync(() -> {
            List<Job> list = new ArrayList<>();
            if (payload.get("zhilian_scraped") instanceof List<?> zlJobs) {
                for (Object o : zlJobs) {
                    if (o instanceof Map<?, ?> m) {
                        Job j = new Job();
                        String jobId = String.valueOf(m.get("jobId"));
                        j.setJobId(jobId);
                        j.setTitle((String) m.get("jobName"));
                        j.setCompanyName((String) m.get("companyName"));
                        j.setSalary((String) m.get("salary"));
                        j.setLocation((String) m.get("city"));
                        j.setEducation((String) m.get("education"));
                        j.setExperience((String) m.get("workingExp"));
                        j.setCity((String) m.get("city"));
                        j.setPlatform("智联招聘");
                        j.setDataSource("zhilian_scraped");
                        try { j.setRawData(objectMapper.writeValueAsString(m)); } catch (Exception ignored) {}
                        list.add(j);
                        if (jobId != null) threadSafeActiveIds.add(jobId);
                    }
                }
            }
            return list;
        }, syncDataExecutor);

        // 4. 猎聘异步解析
        CompletableFuture<List<Job>> liepinFuture = CompletableFuture.supplyAsync(() -> {
            List<Job> list = new ArrayList<>();
            if (payload.get("liepin_scraped") instanceof List<?> lpJobs) {
                for (Object o : lpJobs) {
                    if (o instanceof Map<?, ?> m) {
                        Job j = new Job();
                        String jobId = String.valueOf(m.get("jobId"));
                        j.setJobId(jobId);
                        j.setTitle((String) m.get("jobName"));
                        j.setCompanyName((String) m.get("compName"));
                        j.setSalary((String) m.get("salary"));
                        j.setLocation((String) m.get("jobCity"));
                        j.setCity((String) m.get("jobCity"));
                        j.setPlatform("猎聘");
                        j.setDataSource("liepin_scraped");
                        try { j.setRawData(objectMapper.writeValueAsString(m)); } catch (Exception ignored) {}
                        list.add(j);
                        if (jobId != null) threadSafeActiveIds.add(jobId);
                    }
                }
            }
            return list;
        }, syncDataExecutor);

        // 5. 聚合等待所有任务完成
        CompletableFuture.allOf(bossFuture, job51Future, zhilianFuture, liepinFuture).join();

        List<Job> allJobs = new ArrayList<>();
        try {
            allJobs.addAll(bossFuture.get());
            allJobs.addAll(job51Future.get());
            allJobs.addAll(zhilianFuture.get());
            allJobs.addAll(liepinFuture.get());
        } catch (Exception e) {
            log.error("Concurrent extraction of platform jobs failed:", e);
        }

        // 6. 批量 Upsert 到数据库
        int updateCount = 0;
        LocalDateTime now = LocalDateTime.now();
        for (Job job : allJobs) {
            Job existing = jobMapper.selectOne(new LambdaQueryWrapper<Job>().eq(Job::getJobId, job.getJobId()));
            if (existing != null) {
                existing.setTitle(job.getTitle());
                existing.setCompanyName(job.getCompanyName());
                existing.setSalary(job.getSalary());
                existing.setLocation(job.getLocation());
                existing.setPlatform(job.getPlatform());
                existing.setRawData(job.getRawData());
                existing.setRawData2(job.getRawData2());
                if (job.getEducation() != null) existing.setEducation(job.getEducation());
                if (job.getExperience() != null) existing.setExperience(job.getExperience());
                if (job.getCity() != null) existing.setCity(job.getCity());
                if (job.getArea() != null) existing.setArea(job.getArea());
                if (job.getBusinessDistrict() != null) existing.setBusinessDistrict(job.getBusinessDistrict());
                if (job.getJobUrl() != null) existing.setJobUrl(job.getJobUrl());
                if (job.getJobDesc() != null) existing.setJobDesc(job.getJobDesc());
                if (job.getSkills() != null) existing.setSkills(job.getSkills());
                if (job.getWelfareList() != null) existing.setWelfareList(job.getWelfareList());
                if (job.getHrName() != null) existing.setHrName(job.getHrName());
                if (job.getHrPosition() != null) existing.setHrPosition(job.getHrPosition());
                if (job.getPublishDate() != null) existing.setPublishDate(job.getPublishDate());
                if (job.getUpdateDate() != null) existing.setUpdateDate(job.getUpdateDate());
                if (job.getIsHeadhunter() != null) existing.setIsHeadhunter(job.getIsHeadhunter());
                if (job.getClientCompanyName() != null) existing.setClientCompanyName(job.getClientCompanyName());
                existing.setUpdatedAt(now);
                jobMapper.updateById(existing);
            } else {
                job.setId(UUID.randomUUID().toString());
                job.setStatus("active");
                job.setCreatedAt(now);
                job.setUpdatedAt(now);
                jobMapper.insert(job);
            }
            updateCount++;
        }

        context.setJobUpdatesCount(updateCount);
        context.addActiveJobIds(threadSafeActiveIds);
    }
}
