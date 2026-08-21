package com.jobpilot.jobpilotserver.model.vo;

import com.jobpilot.jobpilotserver.model.entity.Company;

public class CompanyVO extends Company {
    private String tagsStr;

    public String getTagsStr() { return tagsStr; }
    public void setTagsStr(String tagsStr) { this.tagsStr = tagsStr; }
}
