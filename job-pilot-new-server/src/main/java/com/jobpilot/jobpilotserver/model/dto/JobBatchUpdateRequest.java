package com.jobpilot.jobpilotserver.model.dto;

import java.util.List;

/**
 * 职位批量更新请求 DTO
 */
public class JobBatchUpdateRequest {
    private List<String> ids;
    private Boolean isFavorited;
    private Boolean isHidden;
    private List<String> tags;

    public JobBatchUpdateRequest() {}

    public JobBatchUpdateRequest(List<String> ids, Boolean isFavorited, Boolean isHidden, List<String> tags) {
        this.ids = ids;
        this.isFavorited = isFavorited;
        this.isHidden = isHidden;
        this.tags = tags;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public Boolean getIsFavorited() {
        return isFavorited;
    }

    public void setIsFavorited(Boolean isFavorited) {
        this.isFavorited = isFavorited;
    }

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
