package com.jobpilot.jobpilotserver.strategy.sync.pipeline.stages;

import com.jobpilot.jobpilotserver.mapper.AiJobResultMapper;
import com.jobpilot.jobpilotserver.model.entity.AiJobResult;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncContext;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncStage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 阶段 9：AI 职位诊断打分结果同步
 */
@Component
public class AiScoreSyncStage implements SyncStage {

    private final AiJobResultMapper aiJobResultMapper;

    public AiScoreSyncStage(AiJobResultMapper aiJobResultMapper) {
        this.aiJobResultMapper = aiJobResultMapper;
    }

    @Override
    public int getOrder() {
        return 90;
    }

    @Override
    public void handle(Map<String, Object> payload, SyncContext context) {
        if (payload.get("ai_job_scores") instanceof Map<?, ?> scoreMap) {
            int count = 0;
            LocalDateTime now = LocalDateTime.now();

            for (Map.Entry<?, ?> entry : scoreMap.entrySet()) {
                String jobId = String.valueOf(entry.getKey());
                if (entry.getValue() instanceof Map<?, ?> sc) {
                    AiJobResult aiRes = aiJobResultMapper.selectById(jobId);
                    boolean isNew = (aiRes == null);
                    if (isNew) {
                        aiRes = new AiJobResult();
                        aiRes.setJobId(jobId);
                        aiRes.setCreatedAt(now);
                    }
                    if (sc.get("score") != null) aiRes.setScore(((Number) sc.get("score")).intValue());
                    aiRes.setMatchLevel((String) sc.get("matchLevel"));
                    aiRes.setResultText((String) sc.get("resultText"));
                    aiRes.setUpdatedAt(now);
                    if (isNew) aiJobResultMapper.insert(aiRes);
                    else aiJobResultMapper.updateById(aiRes);
                    count++;
                }
            }
            context.setAiResultsCount(count);
        }
    }
}
