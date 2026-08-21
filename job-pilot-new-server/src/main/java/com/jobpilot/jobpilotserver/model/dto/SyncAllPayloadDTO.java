package com.jobpilot.jobpilotserver.model.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全量同步数据大包 DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncAllPayloadDTO {
    private List<Map<String, Object>> jobs;
    private List<Map<String, Object>> companies;
    private List<Map<String, Object>> jobDetails;
    private Map<String, Object> zhilianCache;
    private List<Map<String, Object>> resumes;

    private Map<String, Object> extra = new HashMap<>();

    @JsonAnySetter
    public void setExtra(String key, Object value) {
        this.extra.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getExtra() {
        return this.extra;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>(extra);
        if (jobs != null) map.put("jobs", jobs);
        if (companies != null) map.put("companies", companies);
        if (jobDetails != null) map.put("jobDetails", jobDetails);
        if (zhilianCache != null) map.put("zhilianCache", zhilianCache);
        if (resumes != null) map.put("resumes", resumes);
        return map;
    }
}
