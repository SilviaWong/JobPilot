package com.jobpilot.jobpilotserver.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    @TableField(exist = false)
    private String jobUrl;
    /** 职位描述 (JD) */
    @TableField(exist = false)
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
    @TableField("platformPublishTime")
    private String publishDate;
    /** 最后更新日期 */
    @TableField("platformUpdateTime")
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
    /** 首见时间 */
    private LocalDateTime firstSeen;
    /** 最后抓取时间 */
    private LocalDateTime lastSeen;
    /** 职位描述哈希 */
    private String descHash;
    /** HR 活跃状态描述 */
    private String hrActiveStatus;
    /** HR 活跃等级 (active, moderate, zombie, unknown) */
    private String hrActiveLevel;
    /** 投递/看板状态 (none, to-apply, applied, interview-1, interview-2, offer, rejected) */
    private String applyStatus;
    /** 发起投递时间 */
    private LocalDateTime appliedAt;
    /** 看板状态最后变更时间 */
    private LocalDateTime statusUpdatedAt;
    /** 最低薪资(K/月) */
    private Double salaryMin;
    /** 最高薪资(K/月) */
    private Double salaryMax;
    /** 平均薪资(K/月) */
    private Double salaryAvg;
    /** 年薪月数 */
    private Integer salaryMonths;
    /** 国家标准城市编码 */
    private String cityCode;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 更新时间 */
    private LocalDateTime updatedAt;

    public LocalDateTime getFirstSeen() { return firstSeen; }
    public void setFirstSeen(LocalDateTime firstSeen) { this.firstSeen = firstSeen; }
    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
    public String getDescHash() { return descHash; }
    public void setDescHash(String descHash) { this.descHash = descHash; }
    public String getHrActiveStatus() { return hrActiveStatus; }
    public void setHrActiveStatus(String hrActiveStatus) { this.hrActiveStatus = hrActiveStatus; }
    public String getHrActiveLevel() { return hrActiveLevel; }
    public void setHrActiveLevel(String hrActiveLevel) { this.hrActiveLevel = hrActiveLevel; }
    public String getApplyStatus() { return applyStatus; }
    public void setApplyStatus(String applyStatus) { this.applyStatus = applyStatus; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    public LocalDateTime getStatusUpdatedAt() { return statusUpdatedAt; }
    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) { this.statusUpdatedAt = statusUpdatedAt; }
    public Double getSalaryMin() { return salaryMin; }
    public void setSalaryMin(Double salaryMin) { this.salaryMin = salaryMin; }
    public Double getSalaryMax() { return salaryMax; }
    public void setSalaryMax(Double salaryMax) { this.salaryMax = salaryMax; }
    public Double getSalaryAvg() { return salaryAvg; }
    public void setSalaryAvg(Double salaryAvg) { this.salaryAvg = salaryAvg; }
    public Integer getSalaryMonths() { return salaryMonths; }
    public void setSalaryMonths(Integer salaryMonths) { this.salaryMonths = salaryMonths; }
    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }

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
