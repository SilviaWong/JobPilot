package com.jobpilot.jobpilotserver.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LlmResponseParserTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试标准 Markdown 代码块中的 JSON 解析")
    public void testParseStandardMarkdownJson() {
        String response = """
                ```json
                {
                  "score": 92,
                  "matchLevel": "A",
                  "resultText": "候选人技能与岗位高度匹配，具备扎实的分布式系统经验。"
                }
                ```
                """;

        LlmResponseParser.DiagnosisResult result = LlmResponseParser.parseDiagnosis(response, objectMapper);
        assertEquals(92, result.score());
        assertEquals("A", result.matchLevel());
        assertEquals("候选人技能与岗位高度匹配，具备扎实的分布式系统经验。", result.resultText());
    }

    @Test
    @DisplayName("测试无语言标识的代码块和裸 JSON 解析")
    public void testParseBareJsonAndPlainBlock() {
        String response1 = """
                这是分析结果：
                ```
                {
                  "score": 75,
                  "matchLevel": "B",
                  "reason": "项目背景较为契合，但缺少高并发调优经验。"
                }
                ```
                """;
        LlmResponseParser.DiagnosisResult result1 = LlmResponseParser.parseDiagnosis(response1, objectMapper);
        assertEquals(75, result1.score());
        assertEquals("B", result1.matchLevel());
        assertEquals("项目背景较为契合，但缺少高并发调优经验。", result1.resultText());

        String response2 = "{\"score\": 85, \"matchLevel\": \"A\", \"analysis\": \"匹配良好\"}";
        LlmResponseParser.DiagnosisResult result2 = LlmResponseParser.parseDiagnosis(response2, objectMapper);
        assertEquals(85, result2.score());
        assertEquals("A", result2.matchLevel());
        assertEquals("匹配良好", result2.resultText());
    }

    @Test
    @DisplayName("测试非标准/畸形格式时的正则语义兜底提取")
    public void testMalformedFallback() {
        String naturalLanguageResponse = """
                经过评估：
                综合评分：88分
                匹配等级：A
                建议：候选人熟练掌握 Spring Boot 与微服务架构，推荐面试。
                """;

        LlmResponseParser.DiagnosisResult result = LlmResponseParser.parseDiagnosis(naturalLanguageResponse, objectMapper);
        assertEquals(88, result.score());
        assertEquals("A", result.matchLevel());
        assertTrue(result.resultText().contains("综合评分：88分"));
    }

    @Test
    @DisplayName("测试纯文本提取 cleanText")
    public void testCleanText() {
        String wrappedGreeting = "```\n你好，我对贵司的后端工程师岗位非常感兴趣！\n```";
        String cleaned = LlmResponseParser.cleanText(wrappedGreeting);
        assertEquals("你好，我对贵司的后端工程师岗位非常感兴趣！", cleaned);

        String quotedText = "\"您好，请问该岗位目前还在招人吗？\"";
        assertEquals("您好，请问该岗位目前还在招人吗？", LlmResponseParser.cleanText(quotedText));
    }
}
