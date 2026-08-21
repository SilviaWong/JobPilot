package com.jobpilot.jobpilotserver.strategy.sync.pipeline.stages;

import com.jobpilot.jobpilotserver.mapper.AiSettingsMapper;
import com.jobpilot.jobpilotserver.model.entity.AiSettings;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncContext;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncStage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 阶段 3：AI 基础设置同步
 */
@Component
public class AiSettingsSyncStage implements SyncStage {

    private final AiSettingsMapper aiSettingsMapper;

    public AiSettingsSyncStage(AiSettingsMapper aiSettingsMapper) {
        this.aiSettingsMapper = aiSettingsMapper;
    }

    @Override
    public int getOrder() {
        return 30;
    }

    @Override
    public void handle(Map<String, Object> payload, SyncContext context) {
        if (payload.get("ai_settings") instanceof Map<?, ?> aiMap) {
            String activeProfileId = (String) aiMap.get("activeProfileId");
            if (activeProfileId != null) {
                AiSettings settings = aiSettingsMapper.selectById("default");
                boolean isNew = (settings == null);
                if (isNew) {
                    settings = new AiSettings();
                    settings.setId("default");
                    settings.setCreatedAt(LocalDateTime.now());
                }
                settings.setActiveProfileId(activeProfileId);
                settings.setUpdatedAt(LocalDateTime.now());
                if (isNew) aiSettingsMapper.insert(settings);
                else aiSettingsMapper.updateById(settings);
                context.setAiSettingsUpdated(true);
            }
        }
    }
}
