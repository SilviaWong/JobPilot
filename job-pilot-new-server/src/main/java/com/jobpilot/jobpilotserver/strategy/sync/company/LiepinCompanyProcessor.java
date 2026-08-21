package com.jobpilot.jobpilotserver.strategy.sync.company;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.model.entity.Company;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 猎聘企业处理策略 (对齐 companyProcessors/liepin.ts)
 * <p>
 * 特色规则：
 * 1. 识别数据来源（详情主页 vs 检索列表）；
 * 2. 普通列表数据若已有 rawData2 则跳过不覆盖，详情主页数据强制更新。
 */
@Component
public class LiepinCompanyProcessor extends AbstractCompanyProcessor {

    public LiepinCompanyProcessor(CompanyMapper companyMapper,
                                  JobMapper jobMapper,
                                  ObjectMapper objectMapper) {
        super(companyMapper, jobMapper, objectMapper);
    }

    @Override
    public boolean supports(String platform) {
        if (platform == null) return false;
        String lower = platform.toLowerCase();
        return lower.contains("liepin") || lower.contains("猎聘");
    }

    @Override
    protected String getStandardizedPlatform(String platform) {
        return "猎聘";
    }

    @Override
    protected CompanyFieldsDTO extractFields(Map<?, ?> company, String platform) {
        String cName = getString(company, "compName", "公司名称", "companyName", "公司简称");
        String cFullName = getString(company, "compFullName", "公司全称", "companyFullName");
        if (cFullName.isEmpty()) {
            cFullName = cName;
        }
        String companyId = getString(company, "compId", "公司ID", "companyId");

        String dataSource = getString(company, "dataSource", "数据来源");
        boolean isDetailSource = "liepin_company_details".equals(dataSource)
                || "liepin_company_page".equals(dataSource)
                || company.containsKey("businessInfo")
                || company.containsKey("products");

        return CompanyFieldsDTO.builder()
                .companyName(cName)
                .companyFullName(cFullName)
                .companyId(companyId)
                .rawData(company)
                .isDetailSource(isDetailSource)
                .build();
    }

    @Override
    protected Company findExistingCompany(String companyId, String finalCompanyName, String cFullName, String cName, String standardizedPlatform) {
        Company existing = null;
        if (!companyId.isEmpty()) {
            existing = companyMapper.selectOne(new LambdaQueryWrapper<Company>()
                    .eq(Company::getCompanyId, companyId)
                    .eq(Company::getSourcePlatform, standardizedPlatform));
        }
        if (existing == null && (!cFullName.isEmpty() || !cName.isEmpty())) {
            final String finalFullNameParam = cFullName;
            final String finalNameParam = cName;
            existing = companyMapper.selectOne(new LambdaQueryWrapper<Company>()
                    .eq(Company::getSourcePlatform, standardizedPlatform)
                    .and(w -> {
                        if (!finalFullNameParam.isEmpty() && !finalNameParam.isEmpty()) {
                            w.eq(Company::getCompanyFullName, finalFullNameParam).or().eq(Company::getCompanyName, finalNameParam);
                        } else if (!finalFullNameParam.isEmpty()) {
                            w.eq(Company::getCompanyFullName, finalFullNameParam);
                        } else {
                            w.eq(Company::getCompanyName, finalNameParam);
                        }
                    }));
        }
        return existing;
    }

    @Override
    protected boolean shouldUpdate(Company existingCompany, CompanyFieldsDTO fields) {
        if (!fields.isDetailSource() && existingCompany.getRawData2() != null && !existingCompany.getRawData2().isBlank()) {
            log.debug("Skipping Liepin company list update as rawData2 already exists for: {}", existingCompany.getCompanyName());
            return false;
        }
        return true;
    }
}
