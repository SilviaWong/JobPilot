package com.jobpilot.jobpilotserver.strategy.sync.company;

import java.util.Map;

/**
 * 平台企业处理策略接口
 */
public interface CompanyProcessorStrategy {

    /**
     * 判断当前策略是否支持该平台
     *
     * @param platform 平台标识（如 'Boss直聘', 'boss', '51job', '前程无忧', '猎聘', '智联'）
     */
    boolean supports(String platform);

    /**
     * 执行企业信息的清洗、去重、持久化与职位级联更新
     *
     * @param company 原始公司数据映射
     * @param platform 传入的原始平台名称
     */
    void process(Map<?, ?> company, String platform);

    /**
     * 仅提取标准企业字段 DTO（用于批量同步处理）
     *
     * @param company 原始公司数据映射
     * @param platform 传入的平台名称
     * @return 提取出的企业标准字段 DTO
     */
    CompanyFieldsDTO extractFields(Map<?, ?> company, String platform);

    /**
     * 获取标准化的平台名称
     *
     * @param platform 传入的平台名称
     * @return 标准化后的平台名称
     */
    String getStandardizedPlatform(String platform);
}
