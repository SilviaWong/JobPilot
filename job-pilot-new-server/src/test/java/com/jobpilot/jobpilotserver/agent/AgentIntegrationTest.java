package com.jobpilot.jobpilotserver.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobpilot.jobpilotserver.agent.model.AgentContext;
import com.jobpilot.jobpilotserver.agent.model.AgentRunResult;
import com.jobpilot.jobpilotserver.agent.model.ChatRequest;
import com.jobpilot.jobpilotserver.agent.service.JobAgentRunner;
import com.jobpilot.jobpilotserver.agent.tool.AgentTool;
import com.jobpilot.jobpilotserver.agent.tool.JobAgentTools;
import com.jobpilot.jobpilotserver.controller.AgentController;
import com.jobpilot.jobpilotserver.util.AiResponseParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AgentIntegrationTest {

    @Autowired
    private JobAgentTools jobAgentTools;

    @Autowired
    private JobAgentRunner jobAgentRunner;

    @Autowired
    private AgentController agentController;

    @Test
    public void testAiResponseParser() {
        // 1. 测试从包含 <think> 的文本中抽取
        String deepseekOutput = "<think>正在综合分析候选人简历与职位JD...</think>经评估，候选人综合能力良好。";
        AiResponseParser.ExtractedAiResponse ext = AiResponseParser.extractAiResponse(deepseekOutput);
        assertEquals("正在综合分析候选人简历与职位JD...", ext.thinking());
        assertEquals("经评估，候选人综合能力良好。", ext.content());

        // 2. 测试 5 维精确评分匹配 [22, 23, 18, 19, 9]
        String scoreText = """
                五维评分结果：
                [22, 23, 18, 19, 9]
                建议加强微服务治理与分布式事务实战。
                """;
        AiResponseParser.ParsedAiScore score = AiResponseParser.parseAiScore(scoreText);
        assertEquals(91, score.totalScore());
        assertEquals("A", score.matchLevel());
        assertEquals(5, score.dimensionScores().size());

        // 3. 测试文本总分提取
        String fallbackText = "根据五项综合评估，最终匹配度得分为 72 分。";
        AiResponseParser.ParsedAiScore fallbackScore = AiResponseParser.parseAiScore(fallbackText);
        assertEquals(72, fallbackScore.totalScore());
        assertEquals("B", fallbackScore.matchLevel());
    }

    @Test
    public void testJobAgentToolsRegistration() {
        List<AgentTool> tools = jobAgentTools.getAllTools();
        assertEquals(9, tools.size());

        List<String> names = tools.stream().map(AgentTool::getName).toList();
        assertTrue(names.contains("search_jobs"));
        assertTrue(names.contains("get_job_detail"));
        assertTrue(names.contains("auto_diagnose_job"));
        assertTrue(names.contains("generate_greeting"));
        assertTrue(names.contains("update_job_status"));
        assertTrue(names.contains("batch_filter_and_recommend"));
        assertTrue(names.contains("get_funnel_summary"));
        assertTrue(names.contains("search_interview_questions"));
        assertTrue(names.contains("get_my_resumes"));
    }

    @Test
    public void testGetFunnelSummaryTool() throws Exception {
        AgentTool tool = jobAgentTools.getTool("get_funnel_summary");
        assertNotNull(tool);

        AgentContext ctx = AgentContext.builder().build();
        Object res = tool.execute(Map.of(), ctx);
        assertTrue(res instanceof Map);
        Map<?, ?> map = (Map<?, ?>) res;
        assertTrue(map.containsKey("totalJobs"));
        assertTrue(map.containsKey("stages"));
        assertTrue(map.containsKey("diagnosedRatio"));
    }

    @Test
    public void testGetMyResumesTool() throws Exception {
        AgentTool tool = jobAgentTools.getTool("get_my_resumes");
        assertNotNull(tool);

        AgentContext ctx = AgentContext.builder().build();
        Object res = tool.execute(Map.of(), ctx);
        assertTrue(res instanceof Map);
        Map<?, ?> map = (Map<?, ?>) res;
        assertTrue(map.containsKey("count"));
        assertTrue(map.containsKey("profiles"));
    }

    @Test
    public void testSearchJobsTool() throws Exception {
        AgentTool tool = jobAgentTools.getTool("search_jobs");
        assertNotNull(tool);

        AgentContext ctx = AgentContext.builder().build();
        Object res = tool.execute(Map.of("limit", 5), ctx);
        assertTrue(res instanceof Map);
        Map<?, ?> map = (Map<?, ?>) res;
        assertTrue(map.containsKey("totalFound"));
        assertTrue(map.containsKey("jobs"));
    }

    @Test
    public void testBatchFilterAndRecommendTool() throws Exception {
        AgentTool tool = jobAgentTools.getTool("batch_filter_and_recommend");
        assertNotNull(tool);

        AgentContext ctx = AgentContext.builder().build();
        Object res = tool.execute(Map.of("scanCount", 5, "minScore", 70), ctx);
        assertTrue(res instanceof Map);
        Map<?, ?> map = (Map<?, ?>) res;
        assertTrue(map.containsKey("scanned"));
        assertTrue(map.containsKey("filteredOut"));
        assertTrue(map.containsKey("jobs"));
    }

    @Test
    public void testJobAgentRunnerPromptAndParser() {
        String prompt = jobAgentRunner.buildSystemPrompt();
        assertNotNull(prompt);
        assertTrue(prompt.contains("search_jobs"));
        assertTrue(prompt.contains("batch_filter_and_recommend"));

        // 测试 JSON 抽取
        String wrappedJson = "```json\n{\"thought\": \"测试思考\", \"final_answer\": \"已完成\"}\n```";
        JsonNode node = jobAgentRunner.parseJsonSafe(wrappedJson);
        assertNotNull(node);
        assertEquals("测试思考", node.path("thought").asText());
        assertEquals("已完成", node.path("final_answer").asText());
    }

    @Test
    public void testAgentControllerChatEmpty() {
        ChatRequest req = new ChatRequest();
        req.setMessage("   ");
        AgentRunResult res = agentController.chat(req);
        assertFalse(res.isSuccess());
        assertEquals("EMPTY_MESSAGE", res.getError());
    }

    @Test
    public void testAgentControllerBatchScan() {
        Map<String, Object> res = agentController.batchScan(null);
        assertTrue((Boolean) res.get("success"));
        assertNotNull(res.get("data"));
    }
}

