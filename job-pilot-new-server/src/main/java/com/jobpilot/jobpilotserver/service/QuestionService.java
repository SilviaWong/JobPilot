package com.jobpilot.jobpilotserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jobpilot.jobpilotserver.model.entity.StandardQuestion;

import java.util.Map;

public interface QuestionService extends IService<StandardQuestion> {
    Map<String, Object> getQuestions(int page, int pageSize, String keyword, String domain);
    StandardQuestion createQuestion(StandardQuestion question);
    StandardQuestion updateQuestion(String id, StandardQuestion question);
    boolean deleteQuestion(String id);
    String generateAiAnswer(String title, String corePoints);
    Map<String, Object> batchAiAnswer();
}