package com.jobpilot.jobpilotserver.model.dto;

/**
 * 职位状态更新请求 DTO
 */
public class JobStatusUpdateRequest {
    private String status;
    private String note;

    public JobStatusUpdateRequest() {}

    public JobStatusUpdateRequest(String status) {
        this.status = status;
    }

    public JobStatusUpdateRequest(String status, String note) {
        this.status = status;
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
