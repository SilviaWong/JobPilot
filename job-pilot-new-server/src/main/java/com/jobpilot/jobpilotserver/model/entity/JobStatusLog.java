package com.jobpilot.jobpilotserver.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 投递进度流转日志实体
 */
@TableName("job_status_logs")
@Data
public class JobStatusLog {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /** 关联职位主键 ID */
    private String jobRecordId;
    /** 流转前状态 */
    private String fromStatus;
    /** 流转后状态 */
    private String toStatus;
    /** 备注说明 */
    private String note;
    /** 创建时间 */
    private LocalDateTime createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getJobRecordId() { return jobRecordId; }
    public void setJobRecordId(String jobRecordId) { this.jobRecordId = jobRecordId; }
    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }
    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

