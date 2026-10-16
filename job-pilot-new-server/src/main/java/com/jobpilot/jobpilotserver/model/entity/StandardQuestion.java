package com.jobpilot.jobpilotserver.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jobpilot.jobpilotserver.common.config.SqliteLocalDateTimeTypeHandler;

import java.time.LocalDateTime;

@TableName("StandardQuestion")
@Data
public class StandardQuestion {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String title;
    private String domain;
    private String categoryTags;
    private String corePoints;
    private String answerKey;
    private String aiAnswer;
    private String tags;

    @TableField(exist = false)
    private java.util.List<QuestionVariant> variants;

    @TableField(typeHandler = SqliteLocalDateTimeTypeHandler.class)
    private LocalDateTime createdAt;

    @TableField(typeHandler = SqliteLocalDateTimeTypeHandler.class)
    private LocalDateTime updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getCategoryTags() { return categoryTags; }
    public void setCategoryTags(String categoryTags) { this.categoryTags = categoryTags; }
    public String getCorePoints() { return corePoints; }
    public void setCorePoints(String corePoints) { this.corePoints = corePoints; }
    public String getAnswerKey() { return answerKey; }
    public void setAnswerKey(String answerKey) { this.answerKey = answerKey; }
    public String getAiAnswer() { return aiAnswer; }
    public void setAiAnswer(String aiAnswer) { this.aiAnswer = aiAnswer; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    @com.fasterxml.jackson.annotation.JsonSetter("tags")
    public void setTagsFromAny(Object tagsObj) {
        if (tagsObj instanceof java.util.List) {
            try {
                this.tags = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(tagsObj);
            } catch (Exception e) {
                this.tags = tagsObj.toString();
            }
        } else if (tagsObj != null) {
            this.tags = tagsObj.toString();
        } else {
            this.tags = null;
        }
    }

    public java.util.List<QuestionVariant> getVariants() { return variants; }
    public void setVariants(java.util.List<QuestionVariant> variants) { this.variants = variants; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
