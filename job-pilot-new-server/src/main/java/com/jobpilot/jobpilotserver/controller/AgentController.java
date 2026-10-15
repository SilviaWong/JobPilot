package com.jobpilot.jobpilotserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jobpilot.jobpilotserver.agent.model.*;
import com.jobpilot.jobpilotserver.agent.service.JobAgentRunner;
import com.jobpilot.jobpilotserver.mapper.ResumeProfileMapper;
import com.jobpilot.jobpilotserver.model.entity.ResumeProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 求职智能体 (Job Agent / Job Copilot) 对外 HTTP 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final JobAgentRunner jobAgentRunner;
    private final ResumeProfileMapper resumeProfileMapper;

    public AgentController(JobAgentRunner jobAgentRunner, ResumeProfileMapper resumeProfileMapper) {
        this.jobAgentRunner = jobAgentRunner;
        this.resumeProfileMapper = resumeProfileMapper;
    }

    /**
     * Agent 智能多轮会话
     */
    @PostMapping("/chat")
    public AgentRunResult chat(@RequestBody(required = false) ChatRequest req) {
        if (req == null || req.getMessage() == null || req.getMessage().trim().isEmpty()) {
            return AgentRunResult.builder()
                    .success(false)
                    .answer("消息内容不能为空")
                    .steps(new ArrayList<>())
                    .cards(new ArrayList<>())
                    .error("EMPTY_MESSAGE")
                    .build();
        }

        try {
            AgentContext context = buildAgentContext(req.getResumeProfileId());
            return jobAgentRunner.runJobAgent(req.getMessage().trim(), req.getHistory(), context);
        } catch (Exception e) {
            log.error("Agent chat exception: {}", e.getMessage(), e);
            return AgentRunResult.builder()
                    .success(false)
                    .answer("执行智能体任务时发生异常: " + e.getMessage())
                    .steps(new ArrayList<>())
                    .cards(new ArrayList<>())
                    .error(e.getMessage())
                    .build();
        }
    }

    /**
     * Agent 一键批量初筛与智能巡检
     */
    @PostMapping("/batch-scan")
    public Map<String, Object> batchScan(@RequestBody(required = false) BatchScanRequest req) {
        if (req == null) {
            req = new BatchScanRequest();
        }

        try {
            AgentContext context = buildAgentContext(req.getResumeProfileId());
            Object data = jobAgentRunner.runBatchScan(req, context);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("data", data);
            return res;
        } catch (Exception e) {
            log.error("Agent batch-scan exception: {}", e.getMessage(), e);
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("error", e.getMessage());
            return res;
        }
    }

    private AgentContext buildAgentContext(String resumeProfileId) {
        ResumeProfile defaultProfile = resumeProfileMapper.selectOne(
                new LambdaQueryWrapper<ResumeProfile>().eq(ResumeProfile::getIsDefault, true).last("LIMIT 1")
        );
        List<ResumeProfile> allProfiles = resumeProfileMapper.selectList(null);

        return AgentContext.builder()
                .defaultResume(defaultProfile != null && defaultProfile.getContent() != null ? defaultProfile.getContent() : "")
                .resumes(allProfiles != null ? allProfiles : new ArrayList<>())
                .activeResumeProfileId(resumeProfileId)
                .build();
    }
}

