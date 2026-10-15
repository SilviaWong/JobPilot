package com.jobpilot.jobpilotserver.agent.model;

import com.jobpilot.jobpilotserver.model.entity.ResumeProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentContext {
    private String defaultResume;
    private List<ResumeProfile> resumes;
    private String activeResumeProfileId;
}

