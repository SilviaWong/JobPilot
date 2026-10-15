package com.jobpilot.jobpilotserver.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.*;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.service.impl.JobServiceImpl;
import com.jobpilot.jobpilotserver.strategy.llm.LlmClient;
import com.jobpilot.jobpilotserver.strategy.normalizer.JobNormalizerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class JobServiceBatchUpdateTest {

    @Test
    @DisplayName("测试 JobService batchUpdate 使用单条批量 Update 执行")
    public void testBatchUpdateSingleSql() {
        AtomicInteger updateCount = new AtomicInteger(0);
        AtomicReference<LambdaUpdateWrapper<Job>> capturedWrapper = new AtomicReference<>();

        JobMapper jobMapper = (JobMapper) Proxy.newProxyInstance(
                JobMapper.class.getClassLoader(),
                new Class<?>[]{JobMapper.class},
                (proxy, method, args) -> {
                    if ("update".equals(method.getName())) {
                        updateCount.incrementAndGet();
                        if (args.length > 1 && args[1] instanceof LambdaUpdateWrapper) {
                            capturedWrapper.set((LambdaUpdateWrapper<Job>) args[1]);
                        }
                        return 3; // 假设更新了3条
                    }
                    return null;
                }
        );

        JobDetailMapper detailMapper = (JobDetailMapper) Proxy.newProxyInstance(
                JobDetailMapper.class.getClassLoader(),
                new Class<?>[]{JobDetailMapper.class},
                (proxy, method, args) -> null
        );

        CompanyMapper compMapper = (CompanyMapper) Proxy.newProxyInstance(
                CompanyMapper.class.getClassLoader(),
                new Class<?>[]{CompanyMapper.class},
                (proxy, method, args) -> null
        );

        BlacklistedCompanyMapper blacklistedCompanyMapper = (BlacklistedCompanyMapper) Proxy.newProxyInstance(
                BlacklistedCompanyMapper.class.getClassLoader(),
                new Class<?>[]{BlacklistedCompanyMapper.class},
                (proxy, method, args) -> null
        );

        AiJobResultMapper aiResultMapper = (AiJobResultMapper) Proxy.newProxyInstance(
                AiJobResultMapper.class.getClassLoader(),
                new Class<?>[]{AiJobResultMapper.class},
                (proxy, method, args) -> null
        );

        JobServiceImpl service = new JobServiceImpl(
                jobMapper,
                aiResultMapper,
                compMapper,
                blacklistedCompanyMapper,
                detailMapper,
                null,
                null,
                null,
                null,
                new LlmClient(),
                new ObjectMapper(),
                new JobNormalizerFactory(Collections.emptyList()),
                null
        );

        List<String> ids = List.of("id-1", "id-2", "id-3");
        int rows = service.batchUpdate(ids, true, false, List.of("核心推荐", "外企"));

        // 验证只调用了 1 次单条批量 update
        assertEquals(1, updateCount.get());
        assertEquals(3, rows);
        assertNotNull(capturedWrapper.get());
    }
}
