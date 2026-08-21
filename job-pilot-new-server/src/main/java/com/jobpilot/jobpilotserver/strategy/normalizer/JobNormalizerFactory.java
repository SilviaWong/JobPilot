package com.jobpilot.jobpilotserver.strategy.normalizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.vo.NormalizedJobDataVO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JobNormalizerFactory {
    private final Map<String, JobNormalizerStrategy> strategyMap = new ConcurrentHashMap<>();

    public JobNormalizerFactory(List<JobNormalizerStrategy> strategies) {
        for (JobNormalizerStrategy strategy : strategies) {
            strategyMap.put(strategy.getPlatformName(), strategy);
        }
    }

    public NormalizedJobDataVO normalize(Job job, JsonNode rawData, JsonNode rawData2, JsonNode companyRawData, JsonNode bossSingleDetail) {
        String platform = job.getPlatform() != null ? job.getPlatform() : "default";
        JobNormalizerStrategy strategy = strategyMap.getOrDefault(platform, strategyMap.get("default"));
        return strategy.normalize(job, rawData, rawData2, companyRawData, bossSingleDetail);
    }
}