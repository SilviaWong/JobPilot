package com.jobpilot.jobpilotserver.strategy.sync.company;

/**
 * 平台企业字段提取结果传输对象
 */
public class CompanyFieldsDTO {
    private String companyName;
    private String companyFullName;
    private String companyId;
    private Object rawData;
    private boolean detailSource;

    public CompanyFieldsDTO() {}

    public CompanyFieldsDTO(String companyName, String companyFullName, String companyId, Object rawData, boolean detailSource) {
        this.companyName = companyName;
        this.companyFullName = companyFullName;
        this.companyId = companyId;
        this.rawData = rawData;
        this.detailSource = detailSource;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String companyName;
        private String companyFullName;
        private String companyId;
        private Object rawData;
        private boolean detailSource;

        public Builder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder companyFullName(String companyFullName) {
            this.companyFullName = companyFullName;
            return this;
        }

        public Builder companyId(String companyId) {
            this.companyId = companyId;
            return this;
        }

        public Builder rawData(Object rawData) {
            this.rawData = rawData;
            return this;
        }

        public Builder isDetailSource(boolean detailSource) {
            this.detailSource = detailSource;
            return this;
        }

        public CompanyFieldsDTO build() {
            return new CompanyFieldsDTO(companyName, companyFullName, companyId, rawData, detailSource);
        }
    }

    public boolean isEmpty() {
        return (companyName == null || companyName.isBlank())
                && (companyFullName == null || companyFullName.isBlank())
                && (companyId == null || companyId.isBlank());
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyFullName() {
        return companyFullName;
    }

    public void setCompanyFullName(String companyFullName) {
        this.companyFullName = companyFullName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Object getRawData() {
        return rawData;
    }

    public void setRawData(Object rawData) {
        this.rawData = rawData;
    }

    public boolean isDetailSource() {
        return detailSource;
    }

    public void setDetailSource(boolean detailSource) {
        this.detailSource = detailSource;
    }
}
