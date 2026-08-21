package com.jobpilot.jobpilotserver.strategy.sync.job;

/**
 * 职位字段提取结果传输对象
 * <p>
 * 除原有基础字段外，新增页面展示所需的清洗字段，
 * 在数据入库时一次性填充，避免查询时反复解析 rawData。
 */
public class JobExtractResultDTO {
    private String jobId;
    private String title;
    private String companyName;
    private String companyFullName;
    private String companyId;
    private String salary;
    private String location;
    private String education;
    private String platform;
    private String dataSource;
    private String rawData;
    private String rawData2;

    // ========== 新增页面展示字段 ==========
    private String experience;
    private String city;
    private String area;
    private String businessDistrict;
    private String jobUrl;
    private String jobDesc;
    /** JSON 数组字符串，如 ["Java","Spring"] */
    private String skills;
    /** JSON 数组字符串 */
    private String welfareList;
    private String hrName;
    private String hrPosition;
    private String publishDate;
    private String updateDate;
    private boolean isHeadhunter;
    private String clientCompanyName;

    // ========== 公司附加字段（用于 Company 表入库） ==========
    private String companyIndustry;
    private String companyScale;
    private String companyStage;
    private String companyDesc;
    /** JSON 数组字符串 */
    private String companyWelfare;

    public JobExtractResultDTO() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final JobExtractResultDTO dto = new JobExtractResultDTO();

        public Builder jobId(String v) { dto.jobId = v; return this; }
        public Builder title(String v) { dto.title = v; return this; }
        public Builder companyName(String v) { dto.companyName = v; return this; }
        public Builder companyFullName(String v) { dto.companyFullName = v; return this; }
        public Builder companyId(String v) { dto.companyId = v; return this; }
        public Builder salary(String v) { dto.salary = v; return this; }
        public Builder location(String v) { dto.location = v; return this; }
        public Builder education(String v) { dto.education = v; return this; }
        public Builder platform(String v) { dto.platform = v; return this; }
        public Builder dataSource(String v) { dto.dataSource = v; return this; }
        public Builder rawData(String v) { dto.rawData = v; return this; }
        public Builder rawData2(String v) { dto.rawData2 = v; return this; }
        public Builder experience(String v) { dto.experience = v; return this; }
        public Builder city(String v) { dto.city = v; return this; }
        public Builder area(String v) { dto.area = v; return this; }
        public Builder businessDistrict(String v) { dto.businessDistrict = v; return this; }
        public Builder jobUrl(String v) { dto.jobUrl = v; return this; }
        public Builder jobDesc(String v) { dto.jobDesc = v; return this; }
        public Builder skills(String v) { dto.skills = v; return this; }
        public Builder welfareList(String v) { dto.welfareList = v; return this; }
        public Builder hrName(String v) { dto.hrName = v; return this; }
        public Builder hrPosition(String v) { dto.hrPosition = v; return this; }
        public Builder publishDate(String v) { dto.publishDate = v; return this; }
        public Builder updateDate(String v) { dto.updateDate = v; return this; }
        public Builder isHeadhunter(boolean v) { dto.isHeadhunter = v; return this; }
        public Builder clientCompanyName(String v) { dto.clientCompanyName = v; return this; }
        public Builder companyIndustry(String v) { dto.companyIndustry = v; return this; }
        public Builder companyScale(String v) { dto.companyScale = v; return this; }
        public Builder companyStage(String v) { dto.companyStage = v; return this; }
        public Builder companyDesc(String v) { dto.companyDesc = v; return this; }
        public Builder companyWelfare(String v) { dto.companyWelfare = v; return this; }

        public JobExtractResultDTO build() { return dto; }
    }

    public boolean isEmpty() {
        return jobId == null || jobId.isBlank();
    }

    // ========== Getters & Setters ==========
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
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    public String getRawData() { return rawData; }
    public void setRawData(String rawData) { this.rawData = rawData; }
    public String getRawData2() { return rawData2; }
    public void setRawData2(String rawData2) { this.rawData2 = rawData2; }
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
    public boolean isHeadhunter() { return isHeadhunter; }
    public void setHeadhunter(boolean isHeadhunter) { this.isHeadhunter = isHeadhunter; }
    public String getClientCompanyName() { return clientCompanyName; }
    public void setClientCompanyName(String clientCompanyName) { this.clientCompanyName = clientCompanyName; }
    public String getCompanyIndustry() { return companyIndustry; }
    public void setCompanyIndustry(String companyIndustry) { this.companyIndustry = companyIndustry; }
    public String getCompanyScale() { return companyScale; }
    public void setCompanyScale(String companyScale) { this.companyScale = companyScale; }
    public String getCompanyStage() { return companyStage; }
    public void setCompanyStage(String companyStage) { this.companyStage = companyStage; }
    public String getCompanyDesc() { return companyDesc; }
    public void setCompanyDesc(String companyDesc) { this.companyDesc = companyDesc; }
    public String getCompanyWelfare() { return companyWelfare; }
    public void setCompanyWelfare(String companyWelfare) { this.companyWelfare = companyWelfare; }
}
