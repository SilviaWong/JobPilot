package com.jobpilot.jobpilotserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobDetailMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.mapper.ZhilianEnrichmentCacheMapper;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.entity.JobDetail;
import com.jobpilot.jobpilotserver.model.entity.ZhilianEnrichmentCache;
import com.jobpilot.jobpilotserver.service.impl.DataSyncServiceImpl;
import com.jobpilot.jobpilotserver.strategy.sync.company.CompanyProcessorFactory;
import com.jobpilot.jobpilotserver.strategy.sync.company.DefaultCompanyProcessor;
import com.jobpilot.jobpilotserver.strategy.sync.job.BossJobExtractor;
import com.jobpilot.jobpilotserver.strategy.sync.job.JobExtractorFactory;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncPipelineExecutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DataSyncServiceTest {

    @Test
    @DisplayName("测试职位抓取数据批量同步 syncJobs")
    public void testBatchSyncJobs() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Job> jobStore = new HashMap<>();

        JobMapper jobMapper = (JobMapper) Proxy.newProxyInstance(
                JobMapper.class.getClassLoader(),
                new Class<?>[]{JobMapper.class},
                (proxy, method, args) -> {
                    if ("selectList".equals(method.getName())) {
                        return new ArrayList<>(jobStore.values());
                    } else if ("insert".equals(method.getName())) {
                        Job j = (Job) args[0];
                        jobStore.put(j.getPlatform() + "_" + j.getJobId(), j);
                        return 1;
                    } else if ("updateById".equals(method.getName())) {
                        Job j = (Job) args[0];
                        jobStore.put(j.getPlatform() + "_" + j.getJobId(), j);
                        return 1;
                    }
                    return null;
                }
        );

        CompanyMapper compMapper = (CompanyMapper) Proxy.newProxyInstance(
                CompanyMapper.class.getClassLoader(),
                new Class<?>[]{CompanyMapper.class},
                (proxy, method, args) -> {
                    if ("selectList".equals(method.getName())) {
                        return Collections.emptyList();
                    } else if ("insert".equals(method.getName())) {
                        return 1;
                    }
                    return null;
                }
        );

        JobDetailMapper detailMapper = (JobDetailMapper) Proxy.newProxyInstance(
                JobDetailMapper.class.getClassLoader(),
                new Class<?>[]{JobDetailMapper.class},
                (proxy, method, args) -> Collections.emptyList()
        );

        ZhilianEnrichmentCacheMapper cacheMapper = (ZhilianEnrichmentCacheMapper) Proxy.newProxyInstance(
                ZhilianEnrichmentCacheMapper.class.getClassLoader(),
                new Class<?>[]{ZhilianEnrichmentCacheMapper.class},
                (proxy, method, args) -> Collections.emptyList()
        );

        BossJobExtractor bossExtractor = new BossJobExtractor(jobMapper, compMapper, objectMapper);
        JobExtractorFactory extractorFactory = new JobExtractorFactory(List.of(bossExtractor));
        CompanyProcessorFactory companyProcessorFactory = new CompanyProcessorFactory(List.of(new DefaultCompanyProcessor(compMapper, jobMapper, objectMapper)));
        SyncPipelineExecutor pipelineExecutor = new SyncPipelineExecutor(Collections.emptyList());

        DataSyncServiceImpl service = new DataSyncServiceImpl(
                pipelineExecutor,
                extractorFactory,
                companyProcessorFactory,
                detailMapper,
                jobMapper,
                compMapper,
                cacheMapper,
                objectMapper
        );

        List<Map<String, Object>> jobs = List.of(
                Map.of("platform", "Boss直聘", "encryptJobId", "JOB001", "jobName", "Java 后端工程师", "brandName", "字节跳动", "salaryDesc", "25-35K", "cityName", "北京"),
                Map.of("platform", "Boss直聘", "encryptJobId", "JOB002", "jobName", "全栈工程师", "brandName", "阿里巴巴", "salaryDesc", "30-50K", "cityName", "杭州")
        );

        Map<String, Object> result = service.syncJobs(jobs);
        assertTrue((Boolean) result.get("success"));
        assertEquals(2, jobStore.size());
        assertTrue(jobStore.containsKey("Boss直聘_JOB001"));
        assertEquals("Java 后端工程师", jobStore.get("Boss直聘_JOB001").getTitle());
    }

    @Test
    @DisplayName("测试智联富化缓存批量同步 syncZhilianCache")
    public void testBatchSyncZhilianCache() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, ZhilianEnrichmentCache> cacheStore = new HashMap<>();

        ZhilianEnrichmentCacheMapper cacheMapper = (ZhilianEnrichmentCacheMapper) Proxy.newProxyInstance(
                ZhilianEnrichmentCacheMapper.class.getClassLoader(),
                new Class<?>[]{ZhilianEnrichmentCacheMapper.class},
                (proxy, method, args) -> {
                    if ("selectBatchIds".equals(method.getName())) {
                        return new ArrayList<>(cacheStore.values());
                    } else if ("insert".equals(method.getName())) {
                        ZhilianEnrichmentCache c = (ZhilianEnrichmentCache) args[0];
                        cacheStore.put(c.getJobId(), c);
                        return 1;
                    } else if ("updateById".equals(method.getName())) {
                        ZhilianEnrichmentCache c = (ZhilianEnrichmentCache) args[0];
                        cacheStore.put(c.getJobId(), c);
                        return 1;
                    }
                    return null;
                }
        );

        JobMapper jobMapper = (JobMapper) Proxy.newProxyInstance(
                JobMapper.class.getClassLoader(),
                new Class<?>[]{JobMapper.class},
                (proxy, method, args) -> null
        );

        CompanyMapper compMapper = (CompanyMapper) Proxy.newProxyInstance(
                CompanyMapper.class.getClassLoader(),
                new Class<?>[]{CompanyMapper.class},
                (proxy, method, args) -> null
        );

        JobDetailMapper detailMapper = (JobDetailMapper) Proxy.newProxyInstance(
                JobDetailMapper.class.getClassLoader(),
                new Class<?>[]{JobDetailMapper.class},
                (proxy, method, args) -> null
        );

        DataSyncServiceImpl service = new DataSyncServiceImpl(
                new SyncPipelineExecutor(Collections.emptyList()),
                new JobExtractorFactory(Collections.emptyList()),
                new CompanyProcessorFactory(Collections.emptyList()),
                detailMapper,
                jobMapper,
                compMapper,
                cacheMapper,
                objectMapper
        );

        Map<String, Object> cacheMap = Map.of(
                "ZL001", Map.of("salary", "20k", "company", "腾讯"),
                "ZL002", Map.of("salary", "30k", "company", "美团")
        );

        Map<String, Object> res = service.syncZhilianCache(cacheMap);
        assertTrue((Boolean) res.get("success"));
        assertEquals(2, cacheStore.size());
        assertTrue(cacheStore.containsKey("ZL001"));
    }
}
