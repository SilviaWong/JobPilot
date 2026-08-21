package com.jobpilot.jobpilotserver.strategy.sync.job;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 平台职位提取策略工厂 (Factory Pattern)
 */
@Component
public class JobExtractorFactory {

    private final List<JobExtractorStrategy> strategies;
    private final JobExtractorStrategy defaultStrategy;

    public JobExtractorFactory(List<JobExtractorStrategy> strategies) {
        this.strategies = strategies;
        this.defaultStrategy = strategies.stream()
                .filter(s -> s instanceof DefaultJobExtractor)
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据平台名称自动路由到对应的职位提取策略
     *
     * @param platform 平台标识
     * @return 匹配的职位提取策略实例
     */
    public JobExtractorStrategy getExtractor(String platform) {
        if (platform == null || platform.isBlank()) {
            return defaultStrategy;
        }

        return strategies.stream()
                .filter(s -> !(s instanceof DefaultJobExtractor) && s.supports(platform))
                .findFirst()
                .orElse(defaultStrategy);
    }
}
