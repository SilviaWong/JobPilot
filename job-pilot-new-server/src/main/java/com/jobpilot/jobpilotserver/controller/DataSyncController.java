package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.service.DataSyncService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据同步与实时抓取上报控制器
 * <p>
 * 对齐 Nuxt 原版浏览器扩展程序的数据接收接口：
 * 1. POST /api/sync-all    - 全量数据同步大包
 * 2. POST /api/jobs        - 职位抓取数据实时/批量上报
 * 3. POST /api/job-details - 职位深度详情实时/批量上报
 * 4. POST /api/companies   - 企业信息实时/批量上报
 * 5. POST /api/zhilian-cache - 智联招聘富化缓存上报
 */
@RestController
@RequestMapping("/api")
public class DataSyncController {

    private final DataSyncService dataSyncService;

    public DataSyncController(DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    /**
     * 全量数据同步接口
     */
    @PostMapping("/sync-all")
    public Map<String, Object> syncAll(@RequestBody Map<String, Object> payload) {
        return dataSyncService.syncAll(payload);
    }

    /**
     * 职位抓取数据实时上报接口 (POST /api/jobs)
     */
    @PostMapping("/jobs")
    public Map<String, Object> syncJobs(@RequestBody List<Map<String, Object>> jobs) {
        return dataSyncService.syncJobs(jobs);
    }

    /**
     * 职位深度详情实时上报接口 (POST /api/job-details)
     */
    @PostMapping("/job-details")
    public Map<String, Object> syncJobDetails(@RequestBody List<Map<String, Object>> details) {
        return dataSyncService.syncJobDetails(details);
    }

    /**
     * 企业数据实时上报接口 (POST /api/companies)
     */
    @PostMapping("/companies")
    public Map<String, Object> syncCompanies(@RequestBody List<Map<String, Object>> companies) {
        return dataSyncService.syncCompanies(companies);
    }

    /**
     * 智联招聘富化缓存实时上报接口 (POST /api/zhilian-cache)
     */
    @PostMapping("/zhilian-cache")
    public Map<String, Object> syncZhilianCache(@RequestBody Map<String, Object> cacheMap) {
        return dataSyncService.syncZhilianCache(cacheMap);
    }
}