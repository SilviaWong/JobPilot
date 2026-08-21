package com.jobpilot.jobpilotserver.model.dto;

import java.util.List;

/**
 * 职位属性（收藏、隐藏、标签）更新请求 DTO
 */
public class JobAttributesUpdateRequest {
    private Boolean isFavorited;
    private Boolean isHidden;
    private List<String> tags;

    public JobAttributesUpdateRequest() {}

    public JobAttributesUpdateRequest(Boolean isFavorited, Boolean isHidden, List<String> tags) {
        this.isFavorited = isFavorited;
        this.isHidden = isHidden;
        this.tags = tags;
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
