package com.jobpilot.jobpilotserver.strategy.normalizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.vo.NormalizedJobDataVO;

public interface JobNormalizerStrategy {
    String getPlatformName();
    NormalizedJobDataVO normalize(Job job, JsonNode rawData, JsonNode rawData2, JsonNode companyRawData, JsonNode bossSingleDetail);
}