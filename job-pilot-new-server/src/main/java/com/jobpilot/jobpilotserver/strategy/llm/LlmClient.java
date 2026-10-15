package com.jobpilot.jobpilotserver.strategy.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jobpilot.jobpilotserver.util.AiResponseParser;

@Component
public class LlmClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiResponseParser.ExtractedAiResponse callAiWithReasoning(String systemPrompt, String userPrompt, String apiUrl, String apiKey, String model) throws Exception {
        String finalUrl = apiUrl.trim();
        String lowerModel = model.toLowerCase();
        boolean isClaude = lowerModel.contains("claude") || finalUrl.contains("anthropic");
        boolean isGemini = lowerModel.contains("gemini");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> reqBody = new HashMap<>();

        if (isClaude && !finalUrl.contains("chat/completions")) {
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");
            reqBody.put("model", model);
            reqBody.put("max_tokens", 2500);
            reqBody.put("system", systemPrompt);
            reqBody.put("messages", List.of(Map.of("role", "user", "content", userPrompt)));
        } else if (isGemini && !finalUrl.contains("chat/completions")) {
            if (!finalUrl.contains(":generateContent")) {
                finalUrl = finalUrl.replaceAll("/$", "") + "/v1beta/models/" + model + ":generateContent";
            }
            headers.set("x-goog-api-key", apiKey);
            reqBody.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemPrompt))));
            reqBody.put("contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt)))));
        } else {
            headers.setBearerAuth(apiKey);
            reqBody.put("model", model);
            reqBody.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            ));
            reqBody.put("stream", false);
        }

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(reqBody), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(finalUrl, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("AI Gateway error: " + response.getStatusCode() + " " + response.getBody());
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        com.jobpilot.jobpilotserver.util.AiResponseParser.ExtractedAiResponse extracted =
                com.jobpilot.jobpilotserver.util.AiResponseParser.extractAiResponse(root);

        if (extracted.content().isEmpty() && extracted.thinking().isEmpty()) {
            throw new RuntimeException("AI API 返回内容为空");
        }

        return extracted;
    }

    public String callAI(String systemPrompt, String userPrompt, String apiUrl, String apiKey, String model) throws Exception {
        return callAiWithReasoning(systemPrompt, userPrompt, apiUrl, apiKey, model).content();
    }
}