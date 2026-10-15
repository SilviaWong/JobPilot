package com.jobpilot.jobpilotserver.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentCard {
    /**
     * 卡片类型: job_list, job_detail, greeting, funnel_summary, batch_scan_result, question_list
     */
    private String type;
    private String title;
    private Object data;
}

