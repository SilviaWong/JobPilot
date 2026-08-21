package com.jobpilot.jobpilotserver.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 企业信息表实体
 */
@TableName("Company")
@Data
public class Company {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /** 企业简称 */
    private String companyName;
    /** 企业工商全称 */
    private String companyFullName;
    /** 企业在源平台的唯一 ID */
    private String companyId;
    /** 来源平台 */
    private String sourcePlatform;
    /** 所属行业 */
    private String industry;
    /** 人员规模 */
    private String scale;
    /** 融资阶段 */
    private String stage;
    /** 公司介绍 */
    private String companyDesc;
    /** 公司福利 (JSON 数组字符串) */
    private String welfare;
    /** 原始 JSON 数据 */
    private String rawData;
    /** 辅助原始 JSON 数据 */
    private String rawData2;
    /** 是否为代招/猎头机构 */
    private Boolean isAgency;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 更新时间 */
    private LocalDateTime updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyFullName() { return companyFullName; }
    public void setCompanyFullName(String companyFullName) { this.companyFullName = companyFullName; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getSourcePlatform() { return sourcePlatform; }
    public void setSourcePlatform(String sourcePlatform) { this.sourcePlatform = sourcePlatform; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getScale() { return scale; }
    public void setScale(String scale) { this.scale = scale; }
    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }
    public String getCompanyDesc() { return companyDesc; }
    public void setCompanyDesc(String companyDesc) { this.companyDesc = companyDesc; }
    public String getWelfare() { return welfare; }
    public void setWelfare(String welfare) { this.welfare = welfare; }
    public String getRawData() { return rawData; }
    public void setRawData(String rawData) { this.rawData = rawData; }
    public String getRawData2() { return rawData2; }
    public void setRawData2(String rawData2) { this.rawData2 = rawData2; }
    public Boolean getIsAgency() { return isAgency; }
    public void setIsAgency(Boolean isAgency) { this.isAgency = isAgency; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
