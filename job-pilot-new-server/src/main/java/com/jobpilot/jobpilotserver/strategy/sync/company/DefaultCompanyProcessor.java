package com.jobpilot.jobpilotserver.strategy.sync.company;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 默认通用企业处理策略 (对齐 companyProcessors/default.ts)
 */
@Component
public class DefaultCompanyProcessor extends AbstractCompanyProcessor {

    public DefaultCompanyProcessor(CompanyMapper companyMapper,
                                   JobMapper jobMapper,
                                   ObjectMapper objectMapper) {
        super(companyMapper, jobMapper, objectMapper);
    }

    @Override
    public boolean supports(String platform) {
        // 作为兜底策略，在工厂中未匹配到其他平台时选用
        return "default".equalsIgnoreCase(platform);
    }

    @Override
    public String getStandardizedPlatform(String platform) {
        return (platform == null || platform.isBlank() || "default".equalsIgnoreCase(platform)) ? "未知" : platform;
    }

    @Override
    public CompanyFieldsDTO extractFields(Map<?, ?> company, String platform) {
        String cName = getString(company, "companyName", "公司名称");
        String cFullName = getString(company, "companyFullName", "公司全称");
        if (cFullName.isEmpty()) {
            cFullName = cName;
        }
        String companyId = getString(company, "companyId", "公司ID");
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
