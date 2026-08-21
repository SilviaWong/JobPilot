package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.service.AiSettingsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class AiSettingsController {

    public AiSettingsController(AiSettingsService aiSettingsService) {
        this.aiSettingsService = aiSettingsService;
    }

    private final AiSettingsService aiSettingsService;

    @GetMapping("/ai")
    public Result<Map<String, Object>> getSettings() {
        return Result.success(aiSettingsService.getSettings());
    }

    @PostMapping("/ai")
    public Result<Map<String, Object>> updateSettings(@RequestBody Map<String, Object> body) {
        return Result.success(aiSettingsService.updateSettings(body));
    }

    @PostMapping("/ai-test")
    public Result<Map<String, String>> testConnection(@RequestBody Map<String, Object> body) {
        String msg = aiSettingsService.testAiConnection(body);
        return Result.success(Map.of("message", msg));
    }
}