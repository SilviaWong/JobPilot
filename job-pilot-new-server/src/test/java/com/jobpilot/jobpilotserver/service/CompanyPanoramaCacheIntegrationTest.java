package com.jobpilot.jobpilotserver.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CompanyPanoramaCacheIntegrationTest {

    @Autowired
    private CompanyService companyService;

    @Test
    @DisplayName("验证企业全景性能优化：列投影、字段轻量化、以及 Caffeine 毫秒级命中")
    public void testPanoramaCachePerformance() {
        // 1. 先主动失效缓存
        companyService.invalidatePanoramaCache();

        // 2. 首次查询 (Cache Miss)，需全量聚合，同时已优化列投影
        long start1 = System.currentTimeMillis();
        Map<String, Object> firstResult = companyService.getCompanies(1, 20, null, "normal", "direct", "all", "all");
        long duration1 = System.currentTimeMillis() - start1;

        assertNotNull(firstResult);
        List<?> data1 = (List<?>) firstResult.get("data");
        assertNotNull(data1);
        assertFalse(data1.isEmpty());
        System.out.println(">>> 首次查询耗时 (Cache Miss + 列投影优化): " + duration1 + " ms, 返回企业数: " + data1.size());

        // 验证返回对象已完成轻量化瘦身 (jobs 数组里的每一项不包含臃肿的 jobDesc)
        Map<String, Object> firstComp = (Map<String, Object>) data1.get(0);
        assertNotNull(firstComp.get("companyName"));
        List<Map<String, Object>> jobs = (List<Map<String, Object>>) firstComp.get("jobs");
        if (jobs != null && !jobs.isEmpty()) {
            Map<String, Object> sampleJob = jobs.get(0);
            assertFalse(sampleJob.containsKey("jobDesc"), "轻量化 DTO 不应包含巨型文本 jobDesc");
            assertTrue(sampleJob.containsKey("title"), "轻量化 DTO 必须保留职位标题");
            assertTrue(sampleJob.containsKey("salary"), "轻量化 DTO 必须保留薪资信息");
        }

        // 3. 第二次查询相同状态不同页码/筛选 (Cache Hit)
        long start2 = System.currentTimeMillis();
        Map<String, Object> secondResult = companyService.getCompanies(2, 20, null, "normal", "direct", "all", "all");
        long duration2 = System.currentTimeMillis() - start2;

        assertNotNull(secondResult);
        System.out.println(">>> 第二次查询耗时 (Caffeine Cache Hit): " + duration2 + " ms");
        assertTrue(duration2 <= 50, "Caffeine 缓存命中耗时应在 50ms 以内，实际为: " + duration2 + " ms");

        // 4. 带搜索关键词的查询 (基于缓存中的 finalArray 内存流过滤)
        long start3 = System.currentTimeMillis();
        Map<String, Object> searchResult = companyService.getCompanies(1, 10, "科技", "normal", "direct", "all", "all");
        long duration3 = System.currentTimeMillis() - start3;
        System.out.println(">>> 内存流搜索过滤耗时 (Cache Hit + Stream Filter): " + duration3 + " ms");
        assertTrue(duration3 <= 50, "流过滤应在 50ms 以内完成");
    }
}
