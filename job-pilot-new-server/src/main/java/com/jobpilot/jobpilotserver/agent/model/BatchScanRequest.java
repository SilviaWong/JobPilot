package com.jobpilot.jobpilotserver.agent.model;

import lombok.Data;

@Data
public class BatchScanRequest {
    private Integer scanCount = 10;
    private Integer minScore = 75;
    private Boolean autoMoveToApply = false;
    private String resumeProfileId;
}

