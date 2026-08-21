package com.jobpilot.jobpilotserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jobpilot.jobpilotserver.model.entity.AiSettings;

import java.util.Map;

public interface AiSettingsService extends IService<AiSettings> {
    Map<String, Object> getSettings();
    Map<String, Object> updateSettings(Map<String, Object> body);
    String testAiConnection(Map<String, Object> body);
}