package com.jobpilot.jobpilotserver.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentStep {
    private int step;
    private String thought;
    private String toolName;
    private Map<String, Object> toolParams;
    private Object observation;
    private String error;
}

