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
}
