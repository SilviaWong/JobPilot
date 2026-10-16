package com.jobpilot.jobpilotserver.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jobpilot.jobpilotserver.common.config.SqliteLocalDateTimeTypeHandler;

import java.time.LocalDateTime;

@TableName("QuestionVariant")
@Data
public class QuestionVariant {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String standardQuestionId;
    private Integer serialNo;
    private String title;
    @TableField(typeHandler = SqliteLocalDateTimeTypeHandler.class)
    private LocalDateTime createdAt;
    @TableField(typeHandler = SqliteLocalDateTimeTypeHandler.class)
    private LocalDateTime updatedAt;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStandardQuestionId() {
        return this.standardQuestionId;
    }

    public void setStandardQuestionId(String standardQuestionId) {
        this.standardQuestionId = standardQuestionId;
    }

    public Integer getSerialNo() {
        return this.serialNo;
    }

    public void setSerialNo(Integer serialNo) {
        this.serialNo = serialNo;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
