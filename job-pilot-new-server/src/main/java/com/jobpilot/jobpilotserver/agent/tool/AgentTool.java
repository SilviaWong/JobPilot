package com.jobpilot.jobpilotserver.agent.tool;

import com.jobpilot.jobpilotserver.agent.model.AgentContext;

import java.util.Map;

public interface AgentTool {
    String getName();
    String getDescription();
    Map<String, Object> getParameters();
    Object execute(Map<String, Object> params, AgentContext context) throws Exception;
}

