package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    private final DashboardService dashboardService;

    @GetMapping
    public Result<Map<String, Object>> getDashboard(@RequestParam(defaultValue = "false") boolean refresh) {
        return Result.success(dashboardService.getDashboardStats(refresh));
    }

    @GetMapping("/funnel")
    public Result<Map<String, Object>> getFunnel(
            @RequestParam(required = false, defaultValue = "all") String timeRange,
            @RequestParam(defaultValue = "false") boolean refresh) {
        return Result.success(dashboardService.getFunnelAnalytics(timeRange, refresh));
    }

    @PostMapping("/refresh")
    public Result<Void> refresh() {
        dashboardService.invalidateDashboardCache();
        return Result.success(null);
    }
}