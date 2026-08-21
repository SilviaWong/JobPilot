package com.jobpilot.jobpilotserver.model.dto;

/**
 * 公司黑名单添加请求 DTO
 */
public class CompanyBlacklistRequest {
    private String companyName;
    private String reason;

    public CompanyBlacklistRequest() {}

    public CompanyBlacklistRequest(String companyName, String reason) {
        this.companyName = companyName;
        this.reason = reason;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
