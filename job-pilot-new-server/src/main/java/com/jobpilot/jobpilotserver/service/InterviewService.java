package com.jobpilot.jobpilotserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jobpilot.jobpilotserver.model.entity.Interview;

import java.util.List;
import java.util.Map;

public interface InterviewService extends IService<Interview> {
    Map<String, Object> getInterviews(String jobId, String status, String search, int page, int pageSize);
    List<Interview> getInterviews(String keyword, String status);
    Interview createInterview(Interview interview);
    Interview updateInterview(String id, Interview interview);
    boolean saveOrUpdateInterview(Interview interview);
    boolean updateStatus(String id, String status);
    boolean deleteInterview(String id);
    String generateIcs(String id);
    String exportAllIcs();
    Map<String, Object> syncToQuestions(String id, Map<String, Object> body);
}
