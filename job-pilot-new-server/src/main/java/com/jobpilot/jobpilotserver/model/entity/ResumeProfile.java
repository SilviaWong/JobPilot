package com.jobpilot.jobpilotserver.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 结构化多版本简历画像实体
 */
@TableName("resume_profiles")
@Data
public class ResumeProfile {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /** 简历画像名称，如：Java后端专家 (微服务/高并发) */
    private String title;
    /** 目标岗位名称，如：Java开发工程师 */
    private String targetRole;
    /** 技能标签 (JSON 数组字符串) */
    private String tags;
    /** 简历全文 (Markdown 或纯文本) */
    private String content;
    /** 是否为系统默认使用的简历 */
    private Boolean isDefault;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 更新时间 */
    private LocalDateTime updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

