package com.jobpilot.jobpilotserver.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 大模型 (LLM) 响应内容鲁棒解析器
 * <p>
 * 解决大模型输出格式不可控问题：
 * 1. 支持自动识别和提取包含 ```json、``` 或裸 JSON 文本块；
 * 2. 针对 AI 简历匹配度诊断提供多级结构化解析与语义正则兜底；
 * 3. 针对打招呼语和面试预测题提供纯净化文本提取与格式剥离。
 */
public class LlmResponseParser {

    private static final Logger log = LoggerFactory.getLogger(LlmResponseParser.class);

    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)\\s*```", Pattern.CASE_INSENSITIVE);
    private static final Pattern BARE_JSON_PATTERN = Pattern.compile("(\\{[\\s\\S]*\\})");

    private static final Pattern SCORE_PATTERN = Pattern.compile("(?:score|评分|分数|匹配度)[\\s\"':：]+(\\d{1,3})", Pattern.CASE_INSENSITIVE);
    private static final Pattern MATCH_LEVEL_PATTERN = Pattern.compile("(?:matchLevel|等级|级别)[\\s\"':：]+[\"']?([A-D]|A\\+|A-|B\\+|B-)[\"']?", Pattern.CASE_INSENSITIVE);

    /**
     * 诊断解析结果数据结构
     */
    public record DiagnosisResult(int score, String matchLevel, String resultText) {}

    /**
     * 解析 AI 简历匹配度诊断响应
     *
     * @param rawResponse 大模型原始返回字符串
     * @param objectMapper Jackson 映射器
     * @return 结构化的 DiagnosisResult 实体
     */
    public static DiagnosisResult parseDiagnosis(String rawResponse, ObjectMapper objectMapper) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return new DiagnosisResult(60, "C", "未能获取有效的 AI 分析结果");
        }

        String jsonCandidate = extractJson(rawResponse);
        if (jsonCandidate != null && !jsonCandidate.isBlank() && objectMapper != null) {
            try {
                JsonNode node = objectMapper.readTree(jsonCandidate);
                int score = 80;
                String matchLevel = "B";
                String resultText = rawResponse;

                if (node.has("score") && node.get("score").isNumber()) {
                    score = Math.min(100, Math.max(0, node.get("score").asInt()));
                }
                if (node.has("matchLevel") && !node.get("matchLevel").asText().isBlank()) {
                    matchLevel = node.get("matchLevel").asText().trim().toUpperCase();
                }
                if (node.has("resultText") && !node.get("resultText").asText().isBlank()) {
                    resultText = node.get("resultText").asText().trim();
                } else if (node.has("reason") && !node.get("reason").asText().isBlank()) {
                    resultText = node.get("reason").asText().trim();
                } else if (node.has("analysis") && !node.get("analysis").asText().isBlank()) {
                    resultText = node.get("analysis").asText().trim();
                }

                return new DiagnosisResult(score, matchLevel, resultText);
            } catch (Exception e) {
                log.warn("Failed to parse extracted JSON block for diagnosis, falling back to regex extraction: {}", e.getMessage());
            }
        }

        // 兜底策略：使用正则表达式从自然语言或畸形文本中提取指标
        int score = extractScoreWithFallback(rawResponse);
        String matchLevel = extractMatchLevelWithFallback(rawResponse, score);
        String cleanResult = cleanText(rawResponse);

        return new DiagnosisResult(score, matchLevel, cleanResult);
    }

    /**
     * 提取文本中的有效 JSON 内容块
     *
     * @param text 原始输入
     * @return 提取出的 JSON 字符串，未找到则返回 null
     */
    public static String extractJson(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        // 1. 优先匹配 markdown 代码块 ```json ... ```
        Matcher blockMatcher = JSON_BLOCK_PATTERN.matcher(text);
        if (blockMatcher.find()) {
            String block = blockMatcher.group(1).trim();
            if ((block.startsWith("{") && block.endsWith("}")) || (block.startsWith("[") && block.endsWith("]"))) {
                return block;
            }
        }

        // 2. 匹配最外层花括号的 JSON 结构
        Matcher bareMatcher = BARE_JSON_PATTERN.matcher(text);
        if (bareMatcher.find()) {
            return bareMatcher.group(1).trim();
        }

        return null;
    }

    /**
     * 纯净化大模型文本输出（剥离多余的代码块标记、引语与无用前后缀）
     *
     * @param text 原始文本
     * @return 纯净字符串
     */
    public static String cleanText(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String cleaned = text.trim();
        // 移除外层 markdown 代码块
        if (cleaned.startsWith("```") && cleaned.endsWith("```")) {
            Matcher m = JSON_BLOCK_PATTERN.matcher(cleaned);
            if (m.find()) {
                cleaned = m.group(1).trim();
            }
        }

        // 移除首尾可能的多余双引号
        if (cleaned.length() >= 2 && cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1).trim();
        }

        return cleaned;
    }

    private static int extractScoreWithFallback(String text) {
        Matcher m = SCORE_PATTERN.matcher(text);
        if (m.find()) {
            try {
                int s = Integer.parseInt(m.group(1));
                return Math.min(100, Math.max(0, s));
            } catch (NumberFormatException ignored) {}
        }
        return 80; // 默认良好分数
    }

    private static String extractMatchLevelWithFallback(String text, int score) {
        Matcher m = MATCH_LEVEL_PATTERN.matcher(text);
        if (m.find()) {
            return m.group(1).toUpperCase();
        }
        // 根据分数推导等级
        if (score >= 85) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        return "D";
    }
}
