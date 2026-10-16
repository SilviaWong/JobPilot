package com.jobpilot.jobpilotserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jobpilot.jobpilotserver.model.entity.BlacklistedCompany;
import com.jobpilot.jobpilotserver.model.entity.Company;

import java.util.List;
import java.util.Map;

public interface CompanyService extends IService<Company> {
    Map<String, Object> getCompanies(int page, int pageSize, String query);
    Map<String, Object> getCompanies(int page, int pageSize, String query, String status, String agencyFilter, String platformFilter, String cityFilter);
    List<Company> getAllCompanies();
    List<BlacklistedCompany> getBlacklist();
    boolean addBlacklist(String companyName, String reason);
    boolean removeBlacklist(String companyName);
    boolean mergeCompanies(String targetName, List<String> sourceNames);
    boolean putAgency(String companyName, boolean isAgency);
    void invalidatePanoramaCache();
}
