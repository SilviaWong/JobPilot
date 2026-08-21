package com.jobpilot.jobpilotserver.service;

import java.util.Map;

/**
 * 历史数据清洗与回填服务接口
 */
public interface DataBackfillService {
    /**
     * 针对已有 Job 与 Company 记录中空缺的新增展示字段，
     * 解析 rawData 进行全量回填更新。
     *
     * @return 包含 processedJobs、updatedJobs、processedCompanies、updatedCompanies 的结果统计
     */
    Map<String, Object> backfillAll();
}
