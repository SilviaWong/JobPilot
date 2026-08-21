package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.AiSettingsMapper;
import com.jobpilot.jobpilotserver.model.entity.AiSettings;
import com.jobpilot.jobpilotserver.service.AiSettingsService;
import com.jobpilot.jobpilotserver.strategy.llm.LlmClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * AI 系统大模型设置业务实现类
 * 负责管理系统的大模型调用配置（API Key、接口地址、模型名称等），
 * 并提供与大模型接口的网络与鉴权连通性测试功能。
 */
@Service
public class AiSettingsServiceImpl extends ServiceImpl<AiSettingsMapper, AiSettings> implements AiSettingsService {

    private final AiSettingsMapper settingsMapper;
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public AiSettingsServiceImpl(AiSettingsMapper settingsMapper, LlmClient llmClient, ObjectMapper objectMapper) {
        this.settingsMapper = settingsMapper;
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取系统 AI 大模型配置
     * 若数据库中暂无配置记录（首次使用），则会自动初始化一条默认配置（默认集成 DeepSeek 模型配置）。
     *
     * @return 包含 activeProfileId、profiles 列表等的 Map 对象
     */
    @Override
    public Map<String, Object> getSettings() {
        // 查询主键为 "default" 的全局唯一配置项
        AiSettings settings = settingsMapper.selectById("default");
        if (settings == null) {
            // 首次启动未配置时，构造默认的 AI 设置记录
            settings = new AiSettings();
            settings.setId("default");
            settings.setActiveProfileId("1");
            try {
                // 默认提供 DeepSeek 模型的配置模板
                List<Map<String, String>> defaultProfiles = List.of(
                        Map.of("id", "1", "name", "DeepSeek", "url", "https://api.deepseek.com/v1", "model", "deepseek-chat", "key", "")
                );
                settings.setProfiles(objectMapper.writeValueAsString(defaultProfiles));
            } catch (Exception e) {
                // 序列化异常时 profiles 留空
            }
            settings.setCreatedAt(LocalDateTime.now());
            settings.setUpdatedAt(LocalDateTime.now());
            // 插入初始默认记录
            settingsMapper.insert(settings);
        }

        // 组装返回给前端的数据结构
        Map<String, Object> res = new HashMap<>();
        res.put("id", settings.getId());
        res.put("activeProfileId", settings.getActiveProfileId());
        res.put("createdAt", settings.getCreatedAt());
        res.put("updatedAt", settings.getUpdatedAt());
        try {
            // 将 JSON 字符串 profiles 反序列化为对象结构返回给前端
            res.put("profiles", objectMapper.readValue(settings.getProfiles(), Object.class));
        } catch (Exception e) {
            res.put("profiles", new ArrayList<>());
        }
        return res;
    }

    /**
     * 更新系统 AI 大模型配置
     *
     * @param body 前端提交的待更新参数，支持更新 activeProfileId、profiles
     * @return 更新后的完整配置信息
     */
    @Override
    public Map<String, Object> updateSettings(Map<String, Object> body) {
        AiSettings settings = settingsMapper.selectById("default");
        if (settings == null) {
            settings = new AiSettings();
            settings.setId("default");
            settings.setCreatedAt(LocalDateTime.now());
        }

        // 按需更新当前激活的 Profile ID
        if (body.containsKey("activeProfileId")) {
            settings.setActiveProfileId((String) body.get("activeProfileId"));
        }
        // 按需更新模型配置列表（序列化为 JSON 存储）
        if (body.containsKey("profiles")) {
            try {
                settings.setProfiles(objectMapper.writeValueAsString(body.get("profiles")));
            } catch (Exception e) {
                // 忽略序列化异常
            }
        }
        settings.setUpdatedAt(LocalDateTime.now());

        // 持久化到数据库（新增或更新）
        if (settingsMapper.selectById("default") == null) {
            settingsMapper.insert(settings);
        } else {
            settingsMapper.updateById(settings);
        }

        return getSettings();
    }

    /**
     * 测试大模型 (LLM) 接口的连通性
     * <p>
     * 支持使用前端临时传入的 url/key/model 进行测试；
     * 若未传参则回退使用当前系统激活 (active) 的 Profile 配置进行测试。
     *
     * @param body 请求参数，可选包含 url、key、model
     * @return 连通成功信息及大模型的简短响应
     */
    @Override
    public String testAiConnection(Map<String, Object> body) {
        String url = body.get("url") != null ? (String) body.get("url") : "";
        String key = body.get("key") != null ? (String) body.get("key") : "";
        String model = body.get("model") != null ? (String) body.get("model") : "";

        // 若前端没有显式传递参数，则从数据库中读取当前激活的配置项进行测试
        if (url.isEmpty() || key.isEmpty() || model.isEmpty()) {
            AiSettings settings = settingsMapper.selectById("default");
            if (settings != null) {
                try {
                    JsonNode profiles = objectMapper.readTree(settings.getProfiles());
                    String activeId = settings.getActiveProfileId();
                    if (profiles.isArray()) {
                        for (JsonNode p : profiles) {
                            if (activeId.equals(p.path("id").asText())) {
                                url = p.path("url").asText();
                                key = p.path("key").asText();
                                model = p.path("model").asText();
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    // 解析异常跳过
                }
            }
        }

        // 校验三要素是否完备
        if (url.isEmpty() || key.isEmpty() || model.isEmpty()) {
            throw new RuntimeException("缺少必要参数 (url, key, model)");
        }

        try {
            // 发送一条极简的 Hello 测试指令验证连通性
            String res = llmClient.callAI("You are a helpful assistant.", "Say 'Hello' in one word.", url, key, model);
            return "AI 连接成功！模型响应: " + res;
        } catch (Exception e) {
            throw new RuntimeException("AI 测试连通失败: " + e.getMessage());
        }
    }
}