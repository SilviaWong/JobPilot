package com.jobpilot.jobpilotserver.model.dto;

import java.util.List;

/**
 * 公司别名合并请求 DTO
 */
public class CompanyMergeRequest {
    private String targetName;
    private List<String> sourceNames;

    public CompanyMergeRequest() {}

    public CompanyMergeRequest(String targetName, List<String> sourceNames) {
        this.targetName = targetName;
        this.sourceNames = sourceNames;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public List<String> getSourceNames() {
        return sourceNames;
    }

    public void setSourceNames(List<String> sourceNames) {
        this.sourceNames = sourceNames;
    }
}
