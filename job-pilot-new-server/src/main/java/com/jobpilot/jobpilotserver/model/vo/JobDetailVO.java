package com.jobpilot.jobpilotserver.model.vo;

import com.jobpilot.jobpilotserver.model.entity.AiJobResult;
import com.jobpilot.jobpilotserver.model.entity.Company;
import com.jobpilot.jobpilotserver.model.entity.Job;

public class JobDetailVO extends Job {
    private NormalizedJobDataVO normalizedData;
    private Company company;
    private AiJobResult aiResult;
    private Boolean isBlacklisted;

    public NormalizedJobDataVO getNormalizedData() { return normalizedData; }
    public void setNormalizedData(NormalizedJobDataVO normalizedData) { this.normalizedData = normalizedData; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public AiJobResult getAiResult() { return aiResult; }
    public void setAiResult(AiJobResult aiResult) { this.aiResult = aiResult; }
    public Boolean getIsBlacklisted() { return isBlacklisted; }
    public void setIsBlacklisted(Boolean isBlacklisted) { this.isBlacklisted = isBlacklisted; }
}
