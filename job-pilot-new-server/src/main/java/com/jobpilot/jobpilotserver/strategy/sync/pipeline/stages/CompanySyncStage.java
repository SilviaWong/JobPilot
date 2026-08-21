package com.jobpilot.jobpilotserver.strategy.sync.pipeline.stages;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.model.entity.Company;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncContext;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncStage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 阶段 5：全量同步中的企业基本信息入库 (boss_companies_scraped)
 */
@Component
public class CompanySyncStage implements SyncStage {

    private final CompanyMapper companyMapper;
    private final ObjectMapper objectMapper;

    public CompanySyncStage(CompanyMapper companyMapper,
                            ObjectMapper objectMapper) {
        this.companyMapper = companyMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public int getOrder() {
        return 50;
    }

    @Override
    public void handle(Map<String, Object> payload, SyncContext context) {
        if (payload.get("boss_companies_scraped") instanceof List<?> bComps) {
            int count = 0;
            LocalDateTime now = LocalDateTime.now();

            for (Object o : bComps) {
                if (o instanceof Map<?, ?> m) {
                    String companyName = (String) m.get("公司全称");
                    String companyId = (String) m.get("公司ID");
                    if (companyName == null || companyName.isBlank()) {
                        continue;
                    }

                    Company existing = companyMapper.selectOne(new LambdaQueryWrapper<Company>()
                            .eq(Company::getCompanyName, companyName));

                    String rawData = null;
                    try { rawData = objectMapper.writeValueAsString(m); } catch (Exception ignored) {}

                    if (existing != null) {
                        existing.setCompanyId(companyId);
                        existing.setRawData(rawData);
                        existing.setSourcePlatform("Boss直聘");
                        existing.setUpdatedAt(now);
                        companyMapper.updateById(existing);
                    } else {
                        Company c = new Company();
                        c.setId(UUID.randomUUID().toString());
                        c.setCompanyName(companyName);
                        c.setCompanyId(companyId);
                        c.setSourcePlatform("Boss直聘");
                        c.setRawData(rawData);
                        c.setCreatedAt(now);
                        c.setUpdatedAt(now);
                        companyMapper.insert(c);
                    }
                    count++;
                }
            }
            context.setCompaniesCount(count);
        }
    }
}
