package com.jobpilot.jobpilotserver.strategy.sync.pipeline.stages;

import com.jobpilot.jobpilotserver.mapper.InterviewMapper;
import com.jobpilot.jobpilotserver.model.entity.Interview;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncContext;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncStage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 阶段 4：面试行程与复盘记录同步
 */
@Component
public class InterviewSyncStage implements SyncStage {

    private final InterviewMapper interviewMapper;

    public InterviewSyncStage(InterviewMapper interviewMapper) {
        this.interviewMapper = interviewMapper;
    }

    @Override
    public int getOrder() {
        return 40;
    }

    @Override
    public void handle(Map<String, Object> payload, SyncContext context) {
        if (payload.get("interviews") instanceof List<?> iList) {
            int count = 0;
            for (Object obj : iList) {
                if (obj instanceof Map<?, ?> iMap) {
                    String id = (String) iMap.get("id");
                    String jobId = (String) iMap.get("jobId");
                    if (id != null && jobId != null) {
                        Interview interview = interviewMapper.selectById(id);
                        boolean isNew = (interview == null);
                        if (isNew) {
                            interview = new Interview();
                            interview.setId(id);
                            interview.setCreatedAt(LocalDateTime.now());
                        }
                        interview.setJobId(jobId);
                        interview.setRound((String) iMap.get("round"));
                        interview.setNote((String) iMap.get("note"));
                        interview.setDebrief((String) iMap.get("debrief"));
                        interview.setType((String) iMap.get("type"));
                        interview.setUpdatedAt(LocalDateTime.now());
                        if (isNew) interviewMapper.insert(interview);
                        else interviewMapper.updateById(interview);
                        count++;
                    }
                }
            }
            context.setInterviewsCount(count);
        }
    }
}
