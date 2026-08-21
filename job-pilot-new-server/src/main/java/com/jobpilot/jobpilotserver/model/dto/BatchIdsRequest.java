package com.jobpilot.jobpilotserver.model.dto;

import java.util.List;

/**
 * 批量主键 ID 请求 DTO
 */
public class BatchIdsRequest {
    private List<String> ids;

    public BatchIdsRequest() {}

    public BatchIdsRequest(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
