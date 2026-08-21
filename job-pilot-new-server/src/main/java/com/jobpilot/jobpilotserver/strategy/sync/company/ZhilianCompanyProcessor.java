package com.jobpilot.jobpilotserver.strategy.sync.company;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 智联招聘企业处理策略 (对齐 companyProcessors/zhilian.ts)
 * <p>
 * 特色规则：
 * 优先从工商信息 businessInformation.businessInformationData.registeredName 提取官方注册名。
 */
@Component
public class ZhilianCompanyProcessor extends AbstractCompanyProcessor {

    public ZhilianCompanyProcessor(CompanyMapper companyMapper,
                                   JobMapper jobMapper,
                                   ObjectMapper objectMapper) {
        super(companyMapper, jobMapper, objectMapper);
    }

    @Override
    public boolean supports(String platform) {
        if (platform == null) return false;
        String lower = platform.toLowerCase();
        return lower.contains("zhilian") || lower.contains("智联");
    }

    @Override
    public String getStandardizedPlatform(String platform) {
        return "智联";
    }

    @Override
    public CompanyFieldsDTO extractFields(Map<?, ?> company, String platform) {
        Map<?, ?> businessInformation = getMap(company, "businessInformation");
        Map<?, ?> businessInformationData = getMap(businessInformation, "businessInformationData");

        String cName = getString(businessInformationData, "registeredName");
        if (cName.isEmpty()) {
            cName = getString(company, "companyName", "name");
        }

        String cFullName = getString(businessInformationData, "registeredName");
        if (cFullName.isEmpty()) {
            cFullName = getString(company, "companyFullName", "companyName", "name");
        }
        if (cFullName.isEmpty()) {
            cFullName = cName;
        }

        String companyId = getString(company, "companyNumber", "compId", "companyId");

        return CompanyFieldsDTO.builder()
                .companyName(cName)
                .companyFullName(cFullName)
                .companyId(companyId)
                .rawData(company)
                .isDetailSource(false)
                .build();
    }
}
