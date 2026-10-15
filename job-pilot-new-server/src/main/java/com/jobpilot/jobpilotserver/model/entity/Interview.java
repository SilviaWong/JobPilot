package com.jobpilot.jobpilotserver.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("Interview")
@Data
public class Interview {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String jobId;
    private LocalDateTime time;
    private String type;
    private String round;
    private String note;
    private String debrief;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联的职位信息（非持久化字段）
    @TableField(exist = false)
    private Job job;
    @TableField(exist = false)
    private String jobTitle;
    @TableField(exist = false)
    private String companyName;
    @TableField(exist = false)
    private String status;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
    public LocalDateTime getInterviewTime() { return time; }
    public void setInterviewTime(LocalDateTime interviewTime) { this.time = interviewTime; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRound() { return round; }
    public void setRound(String round) { this.round = round; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getDebrief() { return debrief; }
    public void setDebrief(String debrief) { this.debrief = debrief; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public String getJobTitle() { return jobTitle != null ? jobTitle : (job != null ? job.getTitle() : null); }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getCompanyName() { return companyName != null ? companyName : (job != null ? job.getCompanyName() : null); }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getStatus() { return status != null ? status : (job != null ? job.getApplyStatus() : null); }
    public void setStatus(String status) { this.status = status; }
}
