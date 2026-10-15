package com.jobpilot.jobpilotserver.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 大模型响应与评分解析工具集
 * 兼容支持各大模型及各类中转聚合网关返回的差异化数据格式
 */
public class AiResponseParser {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public record ExtractedAiResponse(String content, String thinking) {}

    public record ParsedAiScore(int totalScore, List<Integer> dimensionScores, String matchLevel) {}

    private static final Pattern THINK_PATTERN = Pattern.compile("(?i)<think>([\\s\\S]*?)</think>");

    private static final Pattern EXACT_5_PATTERN = Pattern.compile(
            "(?:\\[|【|［)\\s*(\\d{1,2})\\s*[,，、\\s]+\\s*(\\d{1,2})\\s*[,，、\\s]+\\s*(\\d{1,2})\\s*[,，、\\s]+\\s*(\\d{1,2})\\s*[,，、\\s]+\\s*(\\d{1,2})\\s*(?:\\]|】|］)"
    );

    private static final Pattern SKILL_PATTERN = Pattern.compile("(?:技能匹配度|技能)[^\\n\\r]*?(?:得分|分数)[：:—\\s]*(\\d{1,2})");
    private static final Pattern EXP_PATTERN = Pattern.compile("(?:经验相关性|经验)[^\\n\\r]*?(?:得分|分数)[：:—\\s]*(\\d{1,2})");
    private static final Pattern EDU_PATTERN = Pattern.compile("(?:教育背景|学历)[^\\n\\r]*?(?:得分|分数)[：:—\\s]*(\\d{1,2})");
    private static final Pattern PROJECT_PATTERN = Pattern.compile("(?:成就与项目|项目)[^\\n\\r]*?(?:得分|分数)[：:—\\s]*(\\d{1,2})");
    private static final Pattern SOFT_PATTERN = Pattern.compile("(?:软技能与文化|软技能)[^\\n\\r]*?(?:得分|分数)[：:—\\s]*(\\d{1,2})");

    private static final Pattern ARRAY_FALLBACK_PATTERN = Pattern.compile("(?:\\[|【|［)([\\d,，、\\s]+)(?:\\]|】|］)");

    private static final Pattern TOTAL_SCORE_PATTERN = Pattern.compile("(?:总分|综合得分|最终得分|综合匹配度|匹配度得分|总得分)[^\\d]*?(\\d{1,3})\\s*(?:分|/|满分|$)");

    /**
     * 从大模型原始返回节点中提取 content 与 thinking
     */
    public static ExtractedAiResponse extractAiResponse(JsonNode root) {
        if (root == null) {
            return new ExtractedAiResponse("", "");
        }

        String content = "";
        String thinking = "";

        // 1. 尝试直接在根节点提取 reasoning / thinking
        if (root.has("thinking") && root.get("thinking").isTextual()) {
            thinking = root.get("thinking").asText("");
        } else if (root.has("reasoning") && root.get("reasoning").isTextual()) {
            thinking = root.get("reasoning").asText("");
        } else if (root.has("reasoning_content") && root.get("reasoning_content").isTextual()) {
            thinking = root.get("reasoning_content").asText("");
        }

        // 2. 检查 choices 结构 (OpenAI / DeepSeek)
        if (root.has("choices") && root.get("choices").isArray() && !root.get("choices").isEmpty()) {
            JsonNode first = root.get("choices").get(0);
            JsonNode message = first.path("message");

            if (thinking.isEmpty()) {
                if (message.has("reasoning_content") && message.get("reasoning_content").isTextual()) {
                    thinking = message.get("reasoning_content").asText("");
                } else if (message.has("thinking") && message.get("thinking").isTextual()) {
                    thinking = message.get("thinking").asText("");
                }
            }

            if (message.has("content") && message.get("content").isTextual()) {
                content = message.get("content").asText("");
            } else if (first.has("text") && first.get("text").isTextual()) {
                content = first.get("text").asText("");
            } else if (first.path("delta").has("content") && first.path("delta").get("content").isTextual()) {
                content = first.path("delta").get("content").asText("");
            }
        } else if (root.has("content")) {
            // Claude 或其他格式
            JsonNode contentNode = root.get("content");
            if (contentNode.isTextual()) {
                content = contentNode.asText("");
            } else if (contentNode.isArray()) {
                List<String> textParts = new ArrayList<>();
                for (JsonNode item : contentNode) {
                    if (item.isTextual()) {
                        textParts.add(item.asText());
                    } else if (item.isObject()) {
                        String type = item.path("type").asText("");
                        if ("text".equals(type) || item.has("text")) {
                            textParts.add(item.path("text").asText(""));
                        } else if ("thinking".equals(type) && item.has("thinking")) {
                            if (thinking.isEmpty()) {
                                thinking = item.path("thinking").asText("");
                            }
                        }
                    }
                }
                content = String.join("\n", textParts);
            }
        } else if (root.has("candidates") && root.get("candidates").isArray() && !root.get("candidates").isEmpty()) {
            // Gemini 结构
            JsonNode parts = root.get("candidates").get(0).path("content").path("parts");
            if (parts.isArray()) {
                List<String> textParts = new ArrayList<>();
                for (JsonNode part : parts) {
                    if (part.has("thought") && part.get("thought").asBoolean()) {
                        if (thinking.isEmpty() && part.has("text")) {
                            thinking = part.get("text").asText("");
                        }
                    } else if (part.has("text")) {
                        textParts.add(part.get("text").asText(""));
                    }
                }
                content = String.join("\n", textParts);
            }
        } else if (root.has("response") && root.get("response").isTextual()) {
            content = root.get("response").asText("");
        } else if (root.has("text") && root.get("text").isTextual()) {
            content = root.get("text").asText("");
        }

        // 3. 处理正文中内嵌的 <think>...</think> 标签 (DeepSeek R1 / Qwen 等模型常见)
        if (content.contains("<think>")) {
            Matcher matcher = THINK_PATTERN.matcher(content);
            if (matcher.find()) {
                if (thinking.isEmpty()) {
                    thinking = matcher.group(1).trim();
                }
                content = matcher.replaceAll("").trim();
            }
        }

        return new ExtractedAiResponse(content.trim(), thinking.trim());
    }

    /**
     * 重载：支持字符串直接解析
     */
    public static ExtractedAiResponse extractAiResponse(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return new ExtractedAiResponse("", "");
        }
        try {
            JsonNode node = OBJECT_MAPPER.readTree(rawText);
            return extractAiResponse(node);
        } catch (Exception e) {
            // 非 JSON 格式，按纯文本处理
            String content = rawText;
            String thinking = "";
            if (content.contains("<think>")) {
                Matcher matcher = THINK_PATTERN.matcher(content);
                if (matcher.find()) {
                    thinking = matcher.group(1).trim();
                    content = matcher.replaceAll("").trim();
                }
            }
            return new ExtractedAiResponse(content.trim(), thinking);
        }
    }

    /**
     * 解析五维评分并得出综合得分与匹配等级
     */
    public static ParsedAiScore parseAiScore(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ParsedAiScore(0, null, "C");
        }

        // 1. 精确匹配 5 项分数数组格式 [16, 18, 5, 17, 7]
        Matcher exact5Matcher = EXACT_5_PATTERN.matcher(text);
        if (exact5Matcher.find()) {
            try {
                int s1 = Integer.parseInt(exact5Matcher.group(1).trim());
                int s2 = Integer.parseInt(exact5Matcher.group(2).trim());
                int s3 = Integer.parseInt(exact5Matcher.group(3).trim());
                int s4 = Integer.parseInt(exact5Matcher.group(4).trim());
                int s5 = Integer.parseInt(exact5Matcher.group(5).trim());
                int sum = s1 + s2 + s3 + s4 + s5;
                int totalScore = Math.max(0, Math.min(100, sum));
                String level = totalScore >= 80 ? "A" : (totalScore < 60 ? "C" : "B");
                return new ParsedAiScore(totalScore, List.of(s1, s2, s3, s4, s5), level);
            } catch (Exception ignored) {}
        }

        // 2. 从每个维度的文字详情中提取得分
        Matcher m1 = SKILL_PATTERN.matcher(text);
        Matcher m2 = EXP_PATTERN.matcher(text);
        Matcher m3 = EDU_PATTERN.matcher(text);
        Matcher m4 = PROJECT_PATTERN.matcher(text);
        Matcher m5 = SOFT_PATTERN.matcher(text);

        if (m1.find() && m2.find() && m3.find() && m4.find() && m5.find()) {
            try {
                int s1 = Integer.parseInt(m1.group(1).trim());
                int s2 = Integer.parseInt(m2.group(1).trim());
                int s3 = Integer.parseInt(m3.group(1).trim());
                int s4 = Integer.parseInt(m4.group(1).trim());
                int s5 = Integer.parseInt(m5.group(1).trim());
                int sum = s1 + s2 + s3 + s4 + s5;
                int totalScore = Math.max(0, Math.min(100, sum));
                String level = totalScore >= 80 ? "A" : (totalScore < 60 ? "C" : "B");
                return new ParsedAiScore(totalScore, List.of(s1, s2, s3, s4, s5), level);
            } catch (Exception ignored) {}
        }

        // 3. 通用数字数组兜底匹配（长度至少为 3，且和在 0-100 之间）
        Matcher arrayMatcher = ARRAY_FALLBACK_PATTERN.matcher(text);
        if (arrayMatcher.find()) {
            String numbersPart = arrayMatcher.group(1);
            String[] parts = numbersPart.split("[,，、\\s]+");
            List<Integer> scores = new ArrayList<>();
            int sum = 0;
            for (String p : parts) {
                if (!p.trim().isEmpty()) {
                    try {
                        int num = Integer.parseInt(p.trim());
                        scores.add(num);
                        sum += num;
                    } catch (Exception ignored) {}
                }
            }
            if (scores.size() >= 3 && sum > 0 && sum <= 100) {
                String level = sum >= 80 ? "A" : (sum < 60 ? "C" : "B");
                return new ParsedAiScore(sum, scores, level);
            }
        }

        // 4. 文本总分关键字兜底（如：“总分：75”、“综合得分82分”）
        Matcher totalMatcher = TOTAL_SCORE_PATTERN.matcher(text);
        if (totalMatcher.find()) {
            try {
                int score = Integer.parseInt(totalMatcher.group(1).trim());
                int totalScore = Math.max(0, Math.min(100, score));
                String level = totalScore >= 80 ? "A" : (totalScore < 60 ? "C" : "B");
                return new ParsedAiScore(totalScore, null, level);
            } catch (Exception ignored) {}
        }

        return new ParsedAiScore(0, null, "C");
    }
}

