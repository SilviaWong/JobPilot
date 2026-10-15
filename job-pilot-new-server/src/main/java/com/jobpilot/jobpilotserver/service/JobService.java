package com.jobpilot.jobpilotserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jobpilot.jobpilotserver.model.entity.Job;

import java.util.List;
import java.util.Map;

public interface JobService extends IService<Job> {
    Map<String, Object> getJobs(int page, int pageSize, String status, Boolean filterFavoritesOnly,
                                Boolean filterShowHidden, Boolean filterShowBlacklisted,
                                String platform, String education, String keyword, String aiDiagnosisFilter);

    Map<String, Object> getJobs(int page, int pageSize, String status, Boolean filterFavoritesOnly,
                                Boolean filterShowHidden, Boolean filterShowBlacklisted,
                                String platform, String education, String keyword, String aiDiagnosisFilter,
                                String headhunterFilter, String city, String salaryFilter,
                                String lifecycleFilter, String hrActiveFilter, String sortBy);

    Job updateJobStatus(String id, String status);
    Job updateJobStatus(String id, String status, String note);
    Job updateJobAttributes(String id, Boolean isFavorited, Boolean isHidden, List<String> tags);
    boolean deleteJob(String id);
    int batchUpdate(List<String> ids, Boolean isFavorited, Boolean isHidden, List<String> tags);
    int batchDelete(List<String> ids);
    Map<String, Object> analyzeJob(String jobId);
    Map<String, Object> generateGreeting(String jobId);
    Map<String, Object> predictQuestions(String jobId);
    Map<String, Object> gapAnalysis(String jobId, String resumeProfileId);
}