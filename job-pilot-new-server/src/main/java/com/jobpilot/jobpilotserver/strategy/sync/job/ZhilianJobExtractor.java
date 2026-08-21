package com.jobpilot.jobpilotserver.strategy.sync.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 智联招聘职位提取策略 (对齐 jobExtractors/zhilian.ts)
 */
@Component
public class ZhilianJobExtractor extends AbstractJobExtractor {

    public ZhilianJobExtractor(JobMapper jobMapper,
                               CompanyMapper companyMapper,
                               ObjectMapper objectMapper) {
        super(jobMapper, companyMapper, objectMapper);
    }

    @Override
    public boolean supports(String platform) {
        if (platform == null) return false;
        String lower = platform.toLowerCase();
        return lower.contains("zhilian") || lower.contains("智联");
    }

    @Override
    public JobExtractResultDTO extract(Map<?, ?> job, String platform) {
        String dataSource = getString(job, "dataSource");

        if ("zhilian_scraped_data_v1".equals(dataSource)) {
            return extractV1(job, dataSource);
        }
        return extractV2(job, dataSource);
    }

    private JobExtractResultDTO extractV1(Map<?, ?> job, String dataSource) {
        Map<?, ?> jobDeliverCache = getMap(job, "jobDeliverCache");
        Map<?, ?> jobDetailData = getMap(jobDeliverCache, "jobDetailData");
        Map<?, ?> position = getMap(jobDetailData, "position");
        Map<?, ?> base = getMap(position, "base");
        Map<?, ?> jobDetail = getMap(job, "jobDetail");
        Map<?, ?> detailedPosition = getMap(jobDetail, "detailedPosition");
        Map<?, ?> compInfo = getMap(jobDetail, "detailedCompany");

        String jobId = getString(jobDeliverCache, "number");
        if (jobId.isEmpty()) jobId = getString(base, "positionNumber");
        if (jobId.isEmpty()) jobId = getString(detailedPosition, "number", "positionNumber");

        String jobTitle = getString(jobDeliverCache, "name");
        if (jobTitle.isEmpty()) jobTitle = getString(base, "positionName");
        if (jobTitle.isEmpty()) jobTitle = getString(detailedPosition, "name", "positionName");

        String salary = getString(jobDeliverCache, "salary60");
        if (salary.isEmpty()) salary = getString(base, "salary");
        if (salary.isEmpty()) salary = getString(detailedPosition, "salary");

        String location = getString(jobDeliverCache, "workCity");
        if (location.isEmpty()) location = getString(jobDetail, "workCity");
        if (location.isEmpty()) location = getString(detailedPosition, "positionWorkCity");

        String education = getString(jobDeliverCache, "education");
        if (education.isEmpty()) education = getString(detailedPosition, "education");

        String companyId = getString(compInfo, "companyNumber");
        if (companyId.isEmpty()) companyId = getString(detailedPosition, "companyNumber");
        if (companyId.isEmpty()) companyId = getString(jobDeliverCache, "companyNumber");

        String companyName = getString(compInfo, "companyName");
        if (companyName.isEmpty()) companyName = getString(detailedPosition, "companyName");
        if (companyName.isEmpty()) companyName = getString(jobDeliverCache, "companyName");
        String companyFullName = companyName;

        // 新增字段
        String experience = getString(jobDeliverCache, "workingExp");
        if (experience.isEmpty()) experience = getString(detailedPosition, "workingExp");

        String city = getString(jobDeliverCache, "workCity");
        if (city.isEmpty()) city = getString(detailedPosition, "positionWorkCity");

        String jobDesc = getString(detailedPosition, "positionDescription", "jobSummary");
        if (jobDesc.isEmpty()) jobDesc = getString(base, "positionDescription");

        String jobUrl = getString(detailedPosition, "positionURL");
        if (jobUrl.isEmpty()) jobUrl = getString(jobDeliverCache, "positionURL");

        List<String> skillsList = extractTagsFromMap(detailedPosition, "skillLabel");
        List<String> welfareListItems = extractTagsFromMap(detailedPosition, "welfare", "welfareLabel");

        String hrName = getString(detailedPosition, "recruiterName");
        String hrPosition = getString(detailedPosition, "recruiterTitle");

        String companyIndustry = getString(compInfo, "industryName", "industry");
        String companyScale = getString(compInfo, "companySizeRange", "companySize");
        String companyStage = getString(compInfo, "companyStage");
        String companyDesc = getString(compInfo, "companyDescription");

        return JobExtractResultDTO.builder()
                .jobId(jobId)
                .title(jobTitle)
                .companyName(companyName)
                .companyFullName(companyFullName)
                .companyId(companyId)
                .salary(salary)
                .location(location)
                .education(education)
                .platform("智联")
                .dataSource(dataSource)
                .rawData("{}")
                .rawData2(toJson(job))
                .experience(experience)
                .city(city)
                .jobUrl(jobUrl)
                .jobDesc(jobDesc)
                .skills(toJsonArray(skillsList))
                .welfareList(toJsonArray(welfareListItems))
                .hrName(hrName)
                .hrPosition(hrPosition)
                .companyIndustry(companyIndustry)
                .companyScale(companyScale)
                .companyStage(companyStage)
                .companyDesc(companyDesc)
                .build();
    }

    private JobExtractResultDTO extractV2(Map<?, ?> job, String dataSource) {
        Map<?, ?> jobDetailData = getMap(job, "jobDetailData");
        Map<?, ?> position = getMap(jobDetailData, "position");
        Map<?, ?> base = getMap(position, "base");
        Map<?, ?> jobDetail = getMap(job, "jobDetail");
        Map<?, ?> detailedPosition = getMap(jobDetail, "detailedPosition");
        Map<?, ?> compInfo = getMap(jobDetail, "detailedCompany");

        String jobId = getString(job, "number");
        if (jobId.isEmpty()) jobId = getString(base, "positionNumber");
        if (jobId.isEmpty()) jobId = getString(detailedPosition, "number", "positionNumber");

        String jobTitle = getString(job, "name", "list_jobName");
        if (jobTitle.isEmpty()) jobTitle = getString(base, "positionName");
        if (jobTitle.isEmpty()) jobTitle = getString(detailedPosition, "name", "positionName");

        String salary = getString(job, "salary60");
        if (salary.isEmpty()) salary = getString(base, "salary");
        if (salary.isEmpty()) salary = getString(detailedPosition, "salary");

        String location = getString(job, "workCity");
        if (location.isEmpty()) location = getString(jobDetail, "workCity");
        if (location.isEmpty()) location = getString(detailedPosition, "positionWorkCity");
        if (location.isEmpty()) location = getString(getMap(job, "jobRootOrgInfo"), "cityName");

        String education = getString(job, "education");
        if (education.isEmpty()) education = getString(base, "education");
        if (education.isEmpty()) education = getString(detailedPosition, "education");

        String companyId = getString(job, "companyNumber", "rootCompanyNumber");
        if (companyId.isEmpty()) companyId = getString(compInfo, "companyNumber");
        if (companyId.isEmpty()) companyId = getString(detailedPosition, "companyNumber");

        String companyName = getString(compInfo, "companyName");
        if (companyName.isEmpty()) companyName = getString(detailedPosition, "companyName");
        if (companyName.isEmpty()) companyName = getString(job, "companyName");
        String companyFullName = companyName;

        // 新增字段
        String experience = getString(job, "workingExp");
        if (experience.isEmpty()) experience = getString(detailedPosition, "workingExp");
        if (experience.isEmpty()) experience = getString(base, "workingExp");

        String city = getString(job, "workCity");
        if (city.isEmpty()) city = getString(detailedPosition, "positionWorkCity");

        String jobDesc = getString(detailedPosition, "positionDescription", "jobSummary");
        if (jobDesc.isEmpty()) jobDesc = getString(base, "positionDescription");

        String jobUrl = getString(detailedPosition, "positionURL");
        if (jobUrl.isEmpty()) jobUrl = getString(job, "positionURL");

        List<String> skillsList = extractTagsFromMap(detailedPosition, "skillLabel");
        if (skillsList.isEmpty()) skillsList = extractTagsFromMap(job, "skillLabel");
        List<String> welfareListItems = extractTagsFromMap(detailedPosition, "welfare", "welfareLabel");
        if (welfareListItems.isEmpty()) welfareListItems = extractTagsFromMap(job, "welfare");

        String hrName = getString(detailedPosition, "recruiterName");
        if (hrName.isEmpty()) hrName = getString(job, "recruiterName");
        String hrPosition = getString(detailedPosition, "recruiterTitle");
        if (hrPosition.isEmpty()) hrPosition = getString(job, "recruiterTitle");

        String companyIndustry = getString(compInfo, "industryName", "industry");
        String companyScale = getString(compInfo, "companySizeRange", "companySize");
        String companyStage = getString(compInfo, "companyStage");
        String companyDesc = getString(compInfo, "companyDescription");

        return JobExtractResultDTO.builder()
                .jobId(jobId)
                .title(jobTitle)
                .companyName(companyName)
                .companyFullName(companyFullName)
                .companyId(companyId)
                .salary(salary)
                .location(location)
                .education(education)
                .platform("智联")
                .dataSource(dataSource.isEmpty() ? "智联" : dataSource)
                .rawData(toJson(job))
                .rawData2(null)
                .experience(experience)
                .city(city)
                .jobUrl(jobUrl)
                .jobDesc(jobDesc)
                .skills(toJsonArray(skillsList))
                .welfareList(toJsonArray(welfareListItems))
                .hrName(hrName)
                .hrPosition(hrPosition)
                .companyIndustry(companyIndustry)
                .companyScale(companyScale)
                .companyStage(companyStage)
                .companyDesc(companyDesc)
                .build();
    }
}
