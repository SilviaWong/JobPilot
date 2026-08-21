package com.jobpilot.jobpilotserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.AiSettingsMapper;
import com.jobpilot.jobpilotserver.model.entity.AiSettings;
import com.jobpilot.jobpilotserver.service.impl.AiSettingsServiceImpl;
import com.jobpilot.jobpilotserver.strategy.llm.LlmClient;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class AiSettingsServiceTest {

    @Test
    public void testGetAndUpdateAiSettings() {
        AtomicReference<AiSettings> stored = new AtomicReference<>();

        AiSettingsMapper mapper = (AiSettingsMapper) Proxy.newProxyInstance(
                AiSettingsMapper.class.getClassLoader(),
                new Class<?>[]{AiSettingsMapper.class},
                (proxy, method, args) -> {
                    if ("selectById".equals(method.getName())) {
                        return stored.get();
                    } else if ("insert".equals(method.getName())) {
                        stored.set((AiSettings) args[0]);
                        return 1;
                    } else if ("updateById".equals(method.getName())) {
                        stored.set((AiSettings) args[0]);
                        return 1;
                    }
                    return null;
                }
        );

        LlmClient llmClient = new LlmClient();
        ObjectMapper objectMapper = new ObjectMapper();
        AiSettingsServiceImpl service = new AiSettingsServiceImpl(mapper, llmClient, objectMapper);

        // 1. Initial get
        Map<String, Object> initial = service.getSettings();
        assertNotNull(initial);
        assertEquals("default", initial.get("id"));
        assertEquals("1", initial.get("activeProfileId"));
        assertNull(initial.get("resume")); // resume should NOT exist in AiSettings

        // 2. Update profiles
        List<Map<String, String>> newProfiles = List.of(
                Map.of("id", "2", "name", "OpenAI", "url", "https://api.openai.com/v1", "model", "gpt-4o", "key", "sk-xxx")
        );
        Map<String, Object> updateResult = service.updateSettings(Map.of("activeProfileId", "2", "profiles", newProfiles));
        assertEquals("2", updateResult.get("activeProfileId"));
        assertNotNull(updateResult.get("profiles"));
    }
}
