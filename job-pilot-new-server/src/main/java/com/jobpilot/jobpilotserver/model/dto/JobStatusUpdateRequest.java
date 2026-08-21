package com.jobpilot.jobpilotserver.model.dto;

/**
 * 职位状态更新请求 DTO
 */
public class JobStatusUpdateRequest {
    private String status;

    public JobStatusUpdateRequest() {}

    public JobStatusUpdateRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
