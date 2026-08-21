package com.jobpilot.jobpilotserver.strategy.sync.pipeline.stages;

import com.jobpilot.jobpilotserver.mapper.StandardQuestionMapper;
import com.jobpilot.jobpilotserver.model.entity.StandardQuestion;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncContext;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncStage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 阶段 2：面试题库同步
 */
@Component
public class QuestionSyncStage implements SyncStage {

    private final StandardQuestionMapper standardQuestionMapper;

    public QuestionSyncStage(StandardQuestionMapper standardQuestionMapper) {
        this.standardQuestionMapper = standardQuestionMapper;
    }

    @Override
    public int getOrder() {
        return 20;
    }

    @Override
    public void handle(Map<String, Object> payload, SyncContext context) {
        if (payload.get("interview_questions") instanceof List<?> qList) {
            int count = 0;
            for (Object obj : qList) {
                if (obj instanceof Map<?, ?> qMap) {
                    String id = (String) qMap.get("id");
                    String title = (String) qMap.get("title");
                    if (id != null && title != null) {
                        StandardQuestion q = standardQuestionMapper.selectById(id);
                        boolean isNew = (q == null);
                        if (isNew) {
                            q = new StandardQuestion();
                            q.setId(id);
                            q.setCreatedAt(LocalDateTime.now());
                        }
                        q.setTitle(title);
                        q.setAnswerKey((String) qMap.get("answer"));
                        q.setUpdatedAt(LocalDateTime.now());
                        if (isNew) standardQuestionMapper.insert(q);
                        else standardQuestionMapper.updateById(q);
                        count++;
                    }
                }
            }
            context.setQuestionsCount(count);
        }
    }
}
