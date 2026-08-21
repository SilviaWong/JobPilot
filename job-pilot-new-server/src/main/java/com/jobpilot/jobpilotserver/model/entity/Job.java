package com.jobpilot.jobpilotserver.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 职位主表实体
 */
@TableName("Job")
@Data
public class Job {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /** 平台原始职位 ID */
    private String jobId;
    /** 职位名称 */
    private String title;
    /** 企业简称 */
    private String companyName;
    /** 企业工商全称 */
    private String companyFullName;
    /** 企业在源平台的唯一 ID */
    private String companyId;
    /** 薪资待遇范围文本 */
    private String salary;
    /** 工作地点（原始文本） */
    private String location;
    /** 学历要求 */
    private String education;
    /** 工作经验要求 */
    private String experience;
    /** 城市 */
    private String city;
    /** 行政区 */
    private String area;
    /** 商圈/片区 */
    private String businessDistrict;
    /** 职位原始链接 */
    private String jobUrl;
    /** 职位描述 (JD) */
    private String jobDesc;
    /** 技能标签列表 (JSON 数组字符串) */
    private String skills;
    /** 福利列表 (JSON 数组字符串) */
    private String welfareList;
    /** HR 姓名 */
    private String hrName;
    /** HR 职位 */
    private String hrPosition;
    /** 首发日期 */
    private String publishDate;
    /** 最后更新日期 */
    private String updateDate;
    /** 是否猎头/代招 */
    private Boolean isHeadhunter;
    /** 猎头代招时实际用人企业名称 */
    private String clientCompanyName;
    /** 来源平台 */
    private String platform;
    /** 职位状态 */
    private String status;
    /** 原始 JSON 数据 */
    private String rawData;
    /** 辅助原始 JSON 数据 */
    private String rawData2;
    /** 数据来源标识 */
    private String dataSource;
    /** 是否收藏 */
    private Boolean isFavorited;
    /** 是否隐藏 */
    private Boolean isHidden;
    /** 自定义标签 */
    private String tags;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 更新时间 */
    private LocalDateTime updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyFullName() { return companyFullName; }
    public void setCompanyFullName(String companyFullName) { this.companyFullName = companyFullName; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getBusinessDistrict() { return businessDistrict; }
    public void setBusinessDistrict(String businessDistrict) { this.businessDistrict = businessDistrict; }
    public String getJobUrl() { return jobUrl; }
    public void setJobUrl(String jobUrl) { this.jobUrl = jobUrl; }
    public String getJobDesc() { return jobDesc; }
    public void setJobDesc(String jobDesc) { this.jobDesc = jobDesc; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getWelfareList() { return welfareList; }
    public void setWelfareList(String welfareList) { this.welfareList = welfareList; }
    public String getHrName() { return hrName; }
    public void setHrName(String hrName) { this.hrName = hrName; }
    public String getHrPosition() { return hrPosition; }
    public void setHrPosition(String hrPosition) { this.hrPosition = hrPosition; }
    public String getPublishDate() { return publishDate; }
    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }
    public String getUpdateDate() { return updateDate; }
    public void setUpdateDate(String updateDate) { this.updateDate = updateDate; }
    public Boolean getIsHeadhunter() { return isHeadhunter; }
    public void setIsHeadhunter(Boolean isHeadhunter) { this.isHeadhunter = isHeadhunter; }
    public String getClientCompanyName() { return clientCompanyName; }
    public void setClientCompanyName(String clientCompanyName) { this.clientCompanyName = clientCompanyName; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRawData() { return rawData; }
    public void setRawData(String rawData) { this.rawData = rawData; }
    public String getRawData2() { return rawData2; }
    public void setRawData2(String rawData2) { this.rawData2 = rawData2; }
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    public Boolean getIsFavorited() { return isFavorited; }
    public void setIsFavorited(Boolean isFavorited) { this.isFavorited = isFavorited; }
    public Boolean getIsHidden() { return isHidden; }
    public void setIsHidden(Boolean isHidden) { this.isHidden = isHidden; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
