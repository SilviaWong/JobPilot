package com.jobpilot.jobpilotserver.agent.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.agent.model.*;
import com.jobpilot.jobpilotserver.agent.tool.AgentTool;
import com.jobpilot.jobpilotserver.agent.tool.JobAgentTools;
import com.jobpilot.jobpilotserver.strategy.llm.LlmClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ReAct (Reasoning + Acting) 全自主循环智能体执行引擎
 */
@Slf4j
@Service
public class JobAgentRunner {

    private static final int MAX_STEPS = 6;
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("(?i)```(?:json)?\\s*([\\s\\S]*?)\\s*```");

    private final JobAgentTools jobAgentTools;
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public JobAgentRunner(JobAgentTools jobAgentTools, LlmClient llmClient, ObjectMapper objectMapper) {
        this.jobAgentTools = jobAgentTools;
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    /**
     * 格式化所有工具的描述供 System Prompt 使用
     */
    public String formatToolsDoc() {
        StringBuilder sb = new StringBuilder();
        for (AgentTool t : jobAgentTools.getAllTools()) {
            sb.append("### 工具名: ").append(t.getName()).append("\n");
            sb.append("描述: ").append(t.getDescription()).append("\n");
            try {
                sb.append("参数: ").append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(t.getParameters())).append("\n\n");
            } catch (Exception e) {
                sb.append("参数: ").append(t.getParameters()).append("\n\n");
            }
        }
        return sb.toString();
    }

    /**
     * 核心系统提示词
     */
    public String buildSystemPrompt() {
        return """
你是由 Job Dashboard Local 系统驱动的专业全流程求职智能体（Job Agent / 求职管家）。
你的目标是协助求职者高效完成职位检索筛选、五维契合度诊断、打招呼语生成、投递状态追踪和面试备考复盘。

你有权限调用以下本地数据库工具：
%s

=== 交互协议规范 ===
你必须严格以合法的 JSON 格式进行回复（可以使用 ```json ... ``` 包裹，也可以直接输出 JSON 对象）。

每次回复必须是以下两种结构之一：

【情况 A：你需要调用工具时】
{
  "thought": "你的思考过程，清晰解释你为什么需要调用该工具，以及预期获取什么数据",
  "tool": "工具名",
  "params": { ...对应的参数对象... }
}

【情况 B：你已经获取到足够信息，给用户提供最终答复时】
{
  "thought": "你的归纳总结与思考",
  "final_answer": "给求职者的自然语言回答。要求结构清晰、言简意赅、充满鼓励与专业洞察。支持 Markdown 语法与表格。",
  "cards": [
    // 可选：附加富交互结构化卡片，方便前端直接渲染快捷操作
    // card 的 type 支持: "job_list", "job_detail", "greeting", "funnel_summary", "batch_scan_result", "question_list"
    // 例如：
    // { "type": "job_list", "title": "推荐的高匹配职位", "data": [ ... ] }
    // { "type": "greeting", "title": "Boss直聘招呼语", "data": { "jobId": "...", "text": "..." } }
  ]
}

=== 行为原则 ===
1. 真实与严谨：不要凭空编造不存在的职位ID或公司。所有岗位数据、状态必须基于工具从本地库查询获取。
2. 目标驱动：当用户提出诸如“帮我看看有什么好岗位”、“推荐今天适合投的”、“一键巡检”等要求时，主动先查数据、再过滤、再给出针对性建议。
3. 善用卡片（cards）：当推荐职位或产出打招呼语时，在 cards 中附带结构化数据，前端会自动渲染可交互卡片（支持点击查看详情或一键复制招呼语）。
""".formatted(formatToolsDoc());
    }

    /**
     * 安全提取并解析 JSON
     */
    public JsonNode parseJsonSafe(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        String cleaned = text.trim();

        // 剔除 markdown 语法代码块
        Matcher matcher = CODE_BLOCK_PATTERN.matcher(cleaned);
        if (matcher.find()) {
            cleaned = matcher.group(1).trim();
        }

        try {
            return objectMapper.readTree(cleaned);
        } catch (Exception ignored) {}

        // 寻找最外层的 { ... }
        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace > firstBrace) {
            try {
                return objectMapper.readTree(cleaned.substring(firstBrace, lastBrace + 1));
            } catch (Exception ignored) {}
        }

        return null;
    }

    /**
     * 运行 Job Agent 多轮会话
     */
    public AgentRunResult runJobAgent(String userQuery,
                                      List<Map<String, String>> history,
                                      AgentContext context) {
        // 1. 获取 AI 配置
        JobAgentTools.AiProfileConfig activeProfile;
        try {
            activeProfile = jobAgentTools.getActiveProfileConfig();
        } catch (Exception e) {
            log.warn("JobAgent AI profile missing: {}", e.getMessage());
            return AgentRunResult.builder()
                    .success(false)
                    .answer("抱歉，当前系统尚未配置 AI 大模型。请先在左侧菜单「大模型管理」中配置 API Key 与模型地址。")
                    .steps(new ArrayList<>())
                    .cards(new ArrayList<>())
                    .error("NO_AI_CONFIG")
                    .build();
        }

        String systemPrompt = buildSystemPrompt();
        List<AgentStep> steps = new ArrayList<>();
        List<AgentCard> cards = new ArrayList<>();

        // 组装多轮上下文
        List<String> conversationTurns = new ArrayList<>();
        if (history != null && !history.isEmpty()) {
            int start = Math.max(0, history.size() - 6);
            for (int i = start; i < history.size(); i++) {
                Map<String, String> h = history.get(i);
                String role = "user".equalsIgnoreCase(h.get("role")) ? "用户" : "助手";
                conversationTurns.add("[" + role + "]: " + h.get("content"));
            }
        }
        conversationTurns.add("[用户最新需求]: " + userQuery);
        String currentTurnPrompt = String.join("\n\n", conversationTurns);

        // 开始 ReAct 循环
        for (int stepIndex = 1; stepIndex <= MAX_STEPS; stepIndex++) {
            String rawText;
            try {
                rawText = llmClient.callAI(systemPrompt, currentTurnPrompt, activeProfile.url(), activeProfile.key(), activeProfile.model());
            } catch (Exception err) {
                log.error("JobAgent model execution error at step {}: {}", stepIndex, err.getMessage(), err);
                return AgentRunResult.builder()
                        .success(false)
                        .answer("大模型调用发生异常: " + err.getMessage())
                        .steps(steps)
                        .cards(cards)
                        .error(err.getMessage())
                        .build();
            }

            JsonNode parsed = parseJsonSafe(rawText);

            // 如果无法解析成 JSON，可能是模型直接输出了文字答案
            if (parsed == null || !parsed.isObject()) {
                steps.add(AgentStep.builder()
                        .step(stepIndex)
                        .thought("大模型直接输出了自然语言文本回答。")
                        .build());
                return AgentRunResult.builder()
                        .success(true)
                        .answer(rawText != null ? rawText : "抱歉，未能获得有效回答。")
                        .steps(steps)
                        .cards(cards)
                        .build();
            }

            String thought = parsed.path("thought").asText("");
            String toolName = parsed.has("tool") ? parsed.path("tool").asText(null) : null;
            JsonNode paramsNode = parsed.path("params");
            Map<String, Object> toolParams = new HashMap<>();
            if (paramsNode.isObject()) {
                try {
                    toolParams = objectMapper.convertValue(paramsNode, new TypeReference<Map<String, Object>>() {});
                } catch (Exception ignored) {}
            }

            String finalAnswer = parsed.has("final_answer") ? parsed.path("final_answer").asText(null) : null;

            // 如果输出了 final_answer，结束循环
            if (finalAnswer != null) {
                steps.add(AgentStep.builder()
                        .step(stepIndex)
                        .thought(!thought.isEmpty() ? thought : "已完成所有必要工具的执行，组织最终回答。")
                        .build());

                if (parsed.has("cards") && parsed.get("cards").isArray()) {
                    for (JsonNode cNode : parsed.get("cards")) {
                        String cType = cNode.path("type").asText(null);
                        String cTitle = cNode.path("title").asText("");
                        JsonNode cDataNode = cNode.path("data");
                        if (cType != null && !cDataNode.isMissingNode() && !cDataNode.isNull()) {
                            Object dataObj = objectMapper.convertValue(cDataNode, Object.class);
                            cards.add(new AgentCard(cType, cTitle, dataObj));
                        }
                    }
                }

                return AgentRunResult.builder()
                        .success(true)
                        .answer(finalAnswer)
                        .steps(steps)
                        .cards(cards)
                        .build();
            }

            // 检查是否要执行工具
            if (toolName != null && !toolName.trim().isEmpty()) {
                AgentTool targetTool = jobAgentTools.getTool(toolName.trim());
                if (targetTool == null) {
                    Map<String, Object> obs = Map.of("error", "未找到名为 '" + toolName + "' 的工具");
                    steps.add(AgentStep.builder()
                            .step(stepIndex)
                            .thought(thought)
                            .toolName(toolName)
                            .toolParams(toolParams)
                            .observation(obs)
                            .build());
                    currentTurnPrompt += "\n\n[第 " + stepIndex + " 步执行结果] 工具 '" + toolName + "' 不存在。可用工具列表见系统提示。请重新规划。";
                    continue;
                }

                // 执行工具
                Object observation;
                try {
                    observation = targetTool.execute(toolParams, context);
                } catch (Exception execErr) {
                    log.error("Tool execution failed: {} -> {}", toolName, execErr.getMessage());
                    observation = Map.of("error", "执行工具 " + toolName + " 时发生异常: " + execErr.getMessage());
                }

                steps.add(AgentStep.builder()
                        .step(stepIndex)
                        .thought(thought)
                        .toolName(toolName)
                        .toolParams(toolParams)
                        .observation(observation)
                        .build());

                // 智能提取部分高价值工具结果直接加入 cards
                extractSmartCards(toolName, observation, cards);

                // 构造下一次迭代的 Observation 输入
                String obsJson;
                try {
                    obsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(observation);
                } catch (Exception e) {
                    obsJson = String.valueOf(observation);
                }

                currentTurnPrompt += """


[第 %d 步你调用的工具]: %s
[参数]: %s
[执行结果 Observation]:
%s
请根据执行结果，继续思考下一步。如果已经收集完信息，请输出 final_answer。""".formatted(stepIndex, toolName, toolParams, obsJson);
            } else {
                // 既没有 tool 也没有 final_answer，兜底输出
                return AgentRunResult.builder()
                        .success(true)
                        .answer(!thought.isEmpty() ? thought : rawText)
                        .steps(steps)
                        .cards(cards)
                        .build();
            }
        }

        // 超过最大步数兜底
        return AgentRunResult.builder()
                .success(true)
                .answer("已经执行了多轮工具检索与分析，当前为你总结已有发现。请查看执行步骤与推荐卡片。")
                .steps(steps)
                .cards(cards)
                .build();
    }

    /**
     * 智能提取高价值工具结果卡片
     */
    private void extractSmartCards(String toolName, Object observation, List<AgentCard> cards) {
        if (!(observation instanceof Map<?, ?> obsMap)) {
            return;
        }

        if ("search_jobs".equals(toolName) && obsMap.get("jobs") instanceof List<?> jobsList && !jobsList.isEmpty()) {
            cards.add(new AgentCard("job_list", "检索到的职位 (" + jobsList.size() + " 个)", jobsList));
        } else if ("generate_greeting".equals(toolName) && obsMap.get("greeting") != null) {
            String title = (String) obsMap.get("title");
            cards.add(new AgentCard("greeting", "为 " + (title != null ? title : "该岗位") + " 生成的打招呼文案", obsMap));
        } else if ("get_funnel_summary".equals(toolName) && obsMap.get("stages") != null) {
            cards.add(new AgentCard("funnel_summary", "当前求职看板数据漏斗", obsMap));
        } else if ("batch_filter_and_recommend".equals(toolName) && obsMap.get("jobs") instanceof List<?> bJobs && !bJobs.isEmpty()) {
            cards.add(new AgentCard("batch_scan_result", "一键智能巡检结果", obsMap));
        }
    }

    /**
     * 执行批量智能巡检
     */
    public Object runBatchScan(BatchScanRequest req, AgentContext context) throws Exception {
        AgentTool batchTool = jobAgentTools.getTool("batch_filter_and_recommend");
        if (batchTool == null) {
            throw new RuntimeException("未找到批量巡检工具 batch_filter_and_recommend");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("scanCount", req.getScanCount() != null ? req.getScanCount() : 10);
        params.put("minScore", req.getMinScore() != null ? req.getMinScore() : 75);
        params.put("autoMoveToApply", Boolean.TRUE.equals(req.getAutoMoveToApply()));

        return batchTool.execute(params, context);
    }
}

