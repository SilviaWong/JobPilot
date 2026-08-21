package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    private final DashboardService dashboardService;

    @GetMapping
    public Result<Map<String, Object>> getDashboard() {
        return Result.success(dashboardService.getDashboardStats());
    }
}