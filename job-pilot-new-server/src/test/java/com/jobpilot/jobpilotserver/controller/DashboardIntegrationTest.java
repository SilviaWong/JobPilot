package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DashboardIntegrationTest {

    @Autowired
    private DashboardController dashboardController;

    @Test
    @SuppressWarnings("unchecked")
    public void testGetDashboardStatsWithKpiTrendsAndPlatform() {
        Result<Map<String, Object>> result = dashboardController.getDashboard(true);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        Map<String, Object> data = result.getData();
        assertNotNull(data);

        // 1. KPI 宏观指标校验
        assertTrue(data.containsKey("kpi"), "Data must contain kpi map");
        Map<String, Object> kpi = (Map<String, Object>) data.get("kpi");
        assertNotNull(kpi.get("totalJobs"));
        assertNotNull(kpi.get("recent7d"));
        assertNotNull(kpi.get("recent30d"));
        assertNotNull(kpi.get("stale60d"));
        assertNotNull(kpi.get("newJobRatio"));
        assertNotNull(kpi.get("zombieRatio"));
        assertTrue(((Number) kpi.get("totalJobs")).intValue() > 0, "totalJobs should be > 0");

        // 2. 时序招聘趋势校验 (月度、周度、每日)
        assertTrue(data.containsKey("trends"), "Data must contain trends map");
        Map<String, Object> trends = (Map<String, Object>) data.get("trends");
        assertTrue(trends.get("monthly") instanceof List);
        assertTrue(trends.get("weekly") instanceof List);
        assertTrue(trends.get("daily") instanceof List);

        List<?> daily = (List<?>) trends.get("daily");
        assertEquals(30, daily.size(), "Daily trends should contain exactly 30 continuous day slots");

        // 3. 平台分布校验
        assertTrue(data.containsKey("platform"), "Data must contain platform map");
        Map<String, Integer> platform = (Map<String, Integer>) data.get("platform");
        assertFalse(platform.isEmpty(), "Platform map should not be empty");

        // 4. 薪资与经验分布校验
        assertTrue(data.containsKey("salary"), "Data must contain salary map");
        Map<String, Integer> salary = (Map<String, Integer>) data.get("salary");
        assertTrue(salary.containsKey("10k以下"));
        assertTrue(salary.containsKey("10-20k"));
        assertTrue(salary.containsKey("20-30k"));
        assertTrue(salary.containsKey("30k以上"));
        assertTrue(salary.containsKey("面议"));

        assertTrue(data.containsKey("experience"), "Data must contain experience map");

        // 5. 技能词频与 HR 活跃度
        assertTrue(data.containsKey("skills"), "Data must contain skills map");
        assertTrue(data.containsKey("hrActive"), "Data must contain hrActive map");
        Map<String, Integer> hrActive = (Map<String, Integer>) data.get("hrActive");
        assertTrue(hrActive.containsKey("active"));
        assertTrue(hrActive.containsKey("zombie"));
    }

    @Test
    public void testDashboardCaffeineCachePerformance() {
        // First call: cache warm-up / refresh
        long t1 = System.currentTimeMillis();
        Result<Map<String, Object>> r1 = dashboardController.getDashboard(true);
        long cost1 = System.currentTimeMillis() - t1;
        assertNotNull(r1);

        // Second call: Caffeine cache hit
        long t2 = System.currentTimeMillis();
        Result<Map<String, Object>> r2 = dashboardController.getDashboard(false);
        long cost2 = System.currentTimeMillis() - t2;
        assertNotNull(r2);

        System.out.printf(">>> First call (Cache Miss): %d ms, Second call (Caffeine Cache Hit): %d ms%n", cost1, cost2);
        assertTrue(cost2 <= cost1, "Cache hit should be faster than or equal to cache miss");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetFunnelAnalytics() {
        Result<Map<String, Object>> result = dashboardController.getFunnel(true);
        assertNotNull(result);
        assertTrue(result.getSuccess());
        Map<String, Object> data = result.getData();
        assertNotNull(data);

        assertTrue(data.containsKey("kpi"));
        assertTrue(data.containsKey("funnelSteps"));
        assertTrue(data.containsKey("durations"));
        assertTrue(data.containsKey("rejectionBreakdown"));
        assertTrue(data.containsKey("stagnantJobs"));
    }
}
