package com.jobpilot.jobpilotserver.common.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    /**
     * 黑名单公司名称本地缓存 (写入后10分钟过期，最多容纳5000条)
     */
    @Bean("blacklistedCompanyCache")
    public Cache<String, Set<String>> blacklistedCompanyCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
    }

    /**
     * AI 设置与配置本地缓存 (写入后30分钟过期)
     */
    @Bean("aiSettingsCache")
    public Cache<String, Object> aiSettingsCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(50)
                .build();
    }

    /**
     * 企业并查集别名模型本地缓存 (写入后30分钟过期，别名变更时主动失效)
     */
    @Bean("companyDsuCache")
    public Cache<String, com.jobpilot.jobpilotserver.strategy.algorithm.CompanyDisjointSet> companyDsuCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(10)
                .build();
    }

    /**
     * 企业全景聚合列表本地缓存 (基于 status 缓存全局预聚合列表，写入后15分钟过期，主动变更时失效)
     */
    @Bean("companyPanoramaCache")
    public Cache<String, java.util.List<java.util.Map<String, Object>>> companyPanoramaCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(20)
                .build();
    }
}
