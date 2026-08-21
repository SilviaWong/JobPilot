package com.jobpilot.jobpilotserver.strategy.sync.job;

import java.util.Map;

/**
 * 平台职位提取与清洗策略接口
 */
public interface JobExtractorStrategy {

    /**
     * 判断当前策略是否支持该平台
     *
     * @param platform 平台标识
     */
    boolean supports(String platform);

    /**
     * 提取职位信息并持久化入库（同时初始化 Company 记录）
     *
     * @param job 原始职位抓取数据映射
     * @param platform 传入的平台名称
     */
    void extractAndSave(Map<?, ?> job, String platform);

    /**
     * 仅提取标准职位实体对象（用于全量同步并发解析）
     *
     * @param job 原始职位抓取数据映射
     * @param platform 平台名称
     * @return 提取出的标准 DTO
     */
    JobExtractResultDTO extract(Map<?, ?> job, String platform);
}
