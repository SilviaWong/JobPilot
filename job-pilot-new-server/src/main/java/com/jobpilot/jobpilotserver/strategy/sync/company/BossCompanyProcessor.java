package com.jobpilot.jobpilotserver.strategy.sync.company;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Boss直聘企业处理策略 (对齐 companyProcessors/boss.ts)
 */
@Component
public class BossCompanyProcessor extends AbstractCompanyProcessor {

    public BossCompanyProcessor(CompanyMapper companyMapper,
                                JobMapper jobMapper,
                                ObjectMapper objectMapper) {
        super(companyMapper, jobMapper, objectMapper);
    }

    @Override
    public boolean supports(String platform) {
        if (platform == null) return false;
        String lower = platform.toLowerCase();
        return lower.contains("boss") || lower.contains("直聘");
    }

    @Override
    protected String getStandardizedPlatform(String platform) {
        return "Boss直聘";
    }

    @Override
    protected CompanyFieldsDTO extractFields(Map<?, ?> company, String platform) {
        String cName = getString(company, "公司名称", "brandName", "公司全称", "companyName");
        String cFullName = getString(company, "公司全称", "companyFullName");
        if (cFullName.isEmpty()) {
            cFullName = cName;
        }
        String companyId = getString(company, "公司ID", "encryptBrandId", "companyId");
        Object rawData = company.get("rawData");
        if (rawData == null) {
            rawData = company;
        }

        return CompanyFieldsDTO.builder()
                .companyName(cName)
                .companyFullName(cFullName)
                .companyId(companyId)
                .rawData(rawData)
                .isDetailSource(false)
                .build();
    }
}
