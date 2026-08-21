package com.jobpilot.jobpilotserver.strategy.sync.company;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 前程无忧 51job 企业处理策略 (对齐 companyProcessors/51job.ts)
 */
@Component
public class Job51CompanyProcessor extends AbstractCompanyProcessor {

    public Job51CompanyProcessor(CompanyMapper companyMapper,
                                 JobMapper jobMapper,
                                 ObjectMapper objectMapper) {
        super(companyMapper, jobMapper, objectMapper);
    }

    @Override
    public boolean supports(String platform) {
        if (platform == null) return false;
        String lower = platform.toLowerCase();
        return lower.contains("51job") || lower.contains("前程");
    }

    @Override
    protected String getStandardizedPlatform(String platform) {
        return "前程无忧";
    }

    @Override
    protected CompanyFieldsDTO extractFields(Map<?, ?> company, String platform) {
        Map<?, ?> coInfo = getMap(company, "coinfo");
        if (coInfo.isEmpty()) coInfo = company;

        Map<?, ?> license = getMap(company, "license");

        String cName = getString(coInfo, "coname");
        if (cName.isEmpty()) cName = getString(license, "businessName");
        if (cName.isEmpty()) cName = getString(company, "companyName");

        String cFullName = getString(coInfo, "coname");
        if (cFullName.isEmpty()) cFullName = getString(license, "businessName");
        if (cFullName.isEmpty()) cFullName = getString(company, "companyFullName");
        if (cFullName.isEmpty()) cFullName = cName;

        String companyId = getString(coInfo, "encryCompanyId");
        if (companyId.isEmpty()) companyId = getString(company, "companyId", "compId");

        return CompanyFieldsDTO.builder()
                .companyName(cName)
                .companyFullName(cFullName)
                .companyId(companyId)
                .rawData(coInfo)
                .isDetailSource(false)
                .build();
    }
}
