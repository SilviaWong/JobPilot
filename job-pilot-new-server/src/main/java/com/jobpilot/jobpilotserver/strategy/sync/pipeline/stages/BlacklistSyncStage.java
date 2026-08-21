package com.jobpilot.jobpilotserver.strategy.sync.pipeline.stages;

import com.github.benmanes.caffeine.cache.Cache;
import com.jobpilot.jobpilotserver.mapper.BlacklistedCompanyMapper;
import com.jobpilot.jobpilotserver.model.entity.BlacklistedCompany;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncContext;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncStage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 阶段 1：黑名单企业同步与缓存失效
 */
@Component
public class BlacklistSyncStage implements SyncStage {

    private final BlacklistedCompanyMapper blacklistedCompanyMapper;
    private final Cache<String, Set<String>> blacklistedCompanyCache;

    public BlacklistSyncStage(BlacklistedCompanyMapper blacklistedCompanyMapper,
                              Cache<String, Set<String>> blacklistedCompanyCache) {
        this.blacklistedCompanyMapper = blacklistedCompanyMapper;
        this.blacklistedCompanyCache = blacklistedCompanyCache;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void handle(Map<String, Object> payload, SyncContext context) {
        if (payload.get("blacklisted_companies") instanceof List<?> blList) {
            int count = 0;
            for (Object obj : blList) {
                if (obj instanceof String cName && !cName.trim().isEmpty()) {
                    String name = cName.trim();
                    if (blacklistedCompanyMapper.selectById(name) == null) {
                        BlacklistedCompany bc = new BlacklistedCompany();
                        bc.setCompanyName(name);
                        bc.setCreatedAt(LocalDateTime.now());
                        blacklistedCompanyMapper.insert(bc);
                    }
                    count++;
                }
            }
            context.setBlacklistedCount(count);
            if (count > 0) {
                blacklistedCompanyCache.invalidateAll();
            }
        }
    }
}
