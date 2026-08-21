package com.jobpilot.jobpilotserver.service;

import java.util.List;
import java.util.Map;

/**
 * 数据同步业务接口
 * <p>
 * 支持全量配置包同步以及来自浏览器插件的单次/批量实时抓取数据上报：
 * 1. 全量同步 (syncAll)
 * 2. 职位列表实时上报 (syncJobs)
 * 3. 职位深度详情实时上报 (syncJobDetails)
 * 4. 企业数据实时上报 (syncCompanies)
 * 5. 智联招聘富化缓存实时上报 (syncZhilianCache)
 */
public interface DataSyncService {

    /**
     * 全量数据同步（接收插件/客户端导出的全量大包）
     */
    Map<String, Object> syncAll(Map<String, Object> payload);

    /**
     * 实时/批量职位抓取数据同步 (POST /api/jobs)
     */
    Map<String, Object> syncJobs(List<Map<String, Object>> jobs);

    /**
     * 实时/批量职位深度详情同步 (POST /api/job-details)
     */
    Map<String, Object> syncJobDetails(List<Map<String, Object>> details);

    /**
     * 实时/批量企业信息同步 (POST /api/companies)
     */
    Map<String, Object> syncCompanies(List<Map<String, Object>> companies);

    /**
     * 智联招聘富化缓存同步 (POST /api/zhilian-cache)
     */
    Map<String, Object> syncZhilianCache(Map<String, Object> cacheMap);
}