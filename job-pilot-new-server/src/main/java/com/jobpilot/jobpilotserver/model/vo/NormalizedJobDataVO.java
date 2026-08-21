package com.jobpilot.jobpilotserver.model.vo;

import java.util.List;
import java.util.Map;

/**
 * 标准化职位数据视图对象 (VO)
 * 用于统一各招聘平台（如 Boss直聘、智联招聘等）爬取解析后的职位详情信息。
 */
public class NormalizedJobDataVO {

    /** 职位原始链接/详情页 URL */
    private String jobUrl;

    /** 职位发布日期 (如 2026-03-01) */
    private String publishDate;

    /** 职位最后更新日期 */
    private String updateDate;

    /** 爬虫抓取/采集时间戳或日期 */
    private String spiderDate;

    /** 企业所属行业 (如 互联网、计算机软件) */
    private String companyIndustry;

    /** 企业融资阶段 (如 不需要融资、已上市、A轮) */
    private String companyStage;

    /** 企业人员规模 (如 100-499人、10000人以上) */
    private String companyScale;

    /** 企业品牌简称 (如 阿里巴巴、腾讯、字节跳动) */
    private String brandName;

    /** 企业工商全称 (如 北京字节跳动网络技术有限公司) */
    private String companyFullName;

    /** 企业在源招聘平台的唯一标识 ID */
    private String companyId;

    /** 招聘者 / HR 姓名 */
    private String hrName;

    /** 招聘者头衔 / 岗位 (如 人事主管、技术总监) */
    private String hrPosition;

    /** 招聘者所属公司名称 (猎头代招场景下可能与用人企业不同) */
    private String hrCompanyName;

    /** 福利待遇列表 (如 五险一金、年终奖、定期体检) */
    private List<String> welfareList;

    /** 岗位所需专业技能标签列表 (如 Java, Spring, MySQL) */
    private List<String> skills;

    /** 职位在源平台的业务唯一 ID */
    private String jobId;

    /** 职位名称 / 岗位标题 (如 Java高级开发工程师) */
    private String jobName;

    /** 薪资待遇范围文本 (如 15-25K·14薪) */
    private String salaryRange;

    /** 职位详细描述与岗位职责要求 (JD) */
    private String jobDesc;

    /** 工作年限 / 经验要求 (如 3-5年、应届生、经验不限) */
    private String experience;

    /** 最低学历要求 (如 本科、硕士、大专) */
    private String degree;

    /** 职位性质 / 类型 (如 全职、兼职、实习) */
    private String positionType;

    /** 职位综合标签列表 */
    private List<String> jobTags;

    /** 工作所在城市 (如 北京、上海、深圳) */
    private String city;

    /** 工作所在行政区 (如 海淀区、朝阳区) */
    private String area;

    /** 工作所在商圈 / 片区 (如 中关村、望京) */
    private String businessDistrict;

    /** 工作具体街道或办公详细地址 */
    private String address;

    /** 是否为猎头 / 中介代理招聘职位 */
    private boolean isHeadhunter;

    /** 猎头代招时背后的真实用人企业名称 */
    private String clientCompanyName;

    /** 数据来源招聘平台 (如 boss, zhilian) */
    private String dataSource;

    /** 平台原始企业结构化扩展数据映射 */
    private Map<String, Object> rawCompany;

    // Getters and Setters
    public String getJobUrl() { return jobUrl; }
    public void setJobUrl(String jobUrl) { this.jobUrl = jobUrl; }
    public String getPublishDate() { return publishDate; }
    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }
    public String getUpdateDate() { return updateDate; }
    public void setUpdateDate(String updateDate) { this.updateDate = updateDate; }
    public String getSpiderDate() { return spiderDate; }
    public void setSpiderDate(String spiderDate) { this.spiderDate = spiderDate; }
    public String getCompanyIndustry() { return companyIndustry; }
    public void setCompanyIndustry(String companyIndustry) { this.companyIndustry = companyIndustry; }
    public String getCompanyStage() { return companyStage; }
    public void setCompanyStage(String companyStage) { this.companyStage = companyStage; }
    public String getCompanyScale() { return companyScale; }
    public void setCompanyScale(String companyScale) { this.companyScale = companyScale; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getCompanyFullName() { return companyFullName; }
    public void setCompanyFullName(String companyFullName) { this.companyFullName = companyFullName; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getHrName() { return hrName; }
    public void setHrName(String hrName) { this.hrName = hrName; }
    public String getHrPosition() { return hrPosition; }
    public void setHrPosition(String hrPosition) { this.hrPosition = hrPosition; }
    public String getHrCompanyName() { return hrCompanyName; }
    public void setHrCompanyName(String hrCompanyName) { this.hrCompanyName = hrCompanyName; }
    public List<String> getWelfareList() { return welfareList; }
    public void setWelfareList(List<String> welfareList) { this.welfareList = welfareList; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }
    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }
    public String getJobDesc() { return jobDesc; }
    public void setJobDesc(String jobDesc) { this.jobDesc = jobDesc; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    public String getPositionType() { return positionType; }
    public void setPositionType(String positionType) { this.positionType = positionType; }
    public List<String> getJobTags() { return jobTags; }
    public void setJobTags(List<String> jobTags) { this.jobTags = jobTags; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getBusinessDistrict() { return businessDistrict; }
    public void setBusinessDistrict(String businessDistrict) { this.businessDistrict = businessDistrict; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public boolean isHeadhunter() { return isHeadhunter; }
    public void setHeadhunter(boolean headhunter) { isHeadhunter = headhunter; }
    public String getClientCompanyName() { return clientCompanyName; }
    public void setClientCompanyName(String clientCompanyName) { this.clientCompanyName = clientCompanyName; }
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    public Map<String, Object> getRawCompany() { return rawCompany; }
    public void setRawCompany(Map<String, Object> rawCompany) { this.rawCompany = rawCompany; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private NormalizedJobDataVO instance = new NormalizedJobDataVO();

        public Builder jobUrl(String jobUrl) { instance.setJobUrl(jobUrl); return this; }
        public Builder publishDate(String publishDate) { instance.setPublishDate(publishDate); return this; }
        public Builder updateDate(String updateDate) { instance.setUpdateDate(updateDate); return this; }
        public Builder spiderDate(String spiderDate) { instance.setSpiderDate(spiderDate); return this; }
        public Builder companyIndustry(String companyIndustry) { instance.setCompanyIndustry(companyIndustry); return this; }
        public Builder companyStage(String companyStage) { instance.setCompanyStage(companyStage); return this; }
        public Builder companyScale(String companyScale) { instance.setCompanyScale(companyScale); return this; }
        public Builder brandName(String brandName) { instance.setBrandName(brandName); return this; }
        public Builder companyFullName(String companyFullName) { instance.setCompanyFullName(companyFullName); return this; }
        public Builder companyId(String companyId) { instance.setCompanyId(companyId); return this; }
        public Builder hrName(String hrName) { instance.setHrName(hrName); return this; }
        public Builder hrPosition(String hrPosition) { instance.setHrPosition(hrPosition); return this; }
        public Builder hrCompanyName(String hrCompanyName) { instance.setHrCompanyName(hrCompanyName); return this; }
        public Builder welfareList(List<String> welfareList) { instance.setWelfareList(welfareList); return this; }
        public Builder skills(List<String> skills) { instance.setSkills(skills); return this; }
        public Builder jobId(String jobId) { instance.setJobId(jobId); return this; }
        public Builder jobName(String jobName) { instance.setJobName(jobName); return this; }
        public Builder salaryRange(String salaryRange) { instance.setSalaryRange(salaryRange); return this; }
        public Builder jobDesc(String jobDesc) { instance.setJobDesc(jobDesc); return this; }
        public Builder experience(String experience) { instance.setExperience(experience); return this; }
        public Builder degree(String degree) { instance.setDegree(degree); return this; }
        public Builder positionType(String positionType) { instance.setPositionType(positionType); return this; }
        public Builder jobTags(List<String> jobTags) { instance.setJobTags(jobTags); return this; }
        public Builder city(String city) { instance.setCity(city); return this; }
        public Builder area(String area) { instance.setArea(area); return this; }
        public Builder businessDistrict(String businessDistrict) { instance.setBusinessDistrict(businessDistrict); return this; }
        public Builder address(String address) { instance.setAddress(address); return this; }
        public Builder isHeadhunter(boolean isHeadhunter) { instance.setHeadhunter(isHeadhunter); return this; }
        public Builder clientCompanyName(String clientCompanyName) { instance.setClientCompanyName(clientCompanyName); return this; }
        public Builder dataSource(String dataSource) { instance.setDataSource(dataSource); return this; }
        public Builder rawCompany(Map<String, Object> rawCompany) { instance.setRawCompany(rawCompany); return this; }

        public NormalizedJobDataVO build() {
            return instance;
        }
    }
}
