package com.jobpilot.jobpilotserver.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentRunResult {
    private boolean success;
    private String answer;
    @Builder.Default
    private List<AgentStep> steps = new ArrayList<>();
    @Builder.Default
    private List<AgentCard> cards = new ArrayList<>();
    private String error;
}

