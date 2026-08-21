package com.jobpilot.jobpilotserver.strategy.sync.company;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 平台企业处理策略工厂 (Factory Pattern)
 */
@Component
public class CompanyProcessorFactory {

    private final List<CompanyProcessorStrategy> strategies;
    private final CompanyProcessorStrategy defaultStrategy;

    public CompanyProcessorFactory(List<CompanyProcessorStrategy> strategies) {
        this.strategies = strategies;
        this.defaultStrategy = strategies.stream()
                .filter(s -> s instanceof DefaultCompanyProcessor)
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据平台标识自动匹配对应的企业处理策略，未命中则返回默认兜底策略
     *
     * @param platform 平台名称或标识
     * @return 对应的策略实例
     */
    public CompanyProcessorStrategy getProcessor(String platform) {
        if (platform == null || platform.isBlank()) {
            return defaultStrategy;
        }

        return strategies.stream()
                .filter(s -> !(s instanceof DefaultCompanyProcessor) && s.supports(platform))
                .findFirst()
                .orElse(defaultStrategy);
    }
}
