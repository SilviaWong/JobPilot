package com.jobpilot.jobpilotserver.model.dto;

/**
 * AI 职位诊断/话术生成/面试题预测请求 DTO
 */
public class JobAiAnalyzeRequest {
    private String jobId;
    private String resumeProfileId;

    public JobAiAnalyzeRequest() {}

    public JobAiAnalyzeRequest(String jobId) {
        this.jobId = jobId;
    }

    public JobAiAnalyzeRequest(String jobId, String resumeProfileId) {
        this.jobId = jobId;
        this.resumeProfileId = resumeProfileId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getResumeProfileId() {
        return resumeProfileId;
    }

    public void setResumeProfileId(String resumeProfileId) {
        this.resumeProfileId = resumeProfileId;
    }
}
