package com.jobpilot.jobpilotserver.strategy.sync.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 前程无忧 51job 职位提取策略 (对齐 jobExtractors/51job.ts)
 */
@Component
public class Job51Extractor extends AbstractJobExtractor {

    public Job51Extractor(JobMapper jobMapper,
                          CompanyMapper companyMapper,
                          ObjectMapper objectMapper) {
        super(jobMapper, companyMapper, objectMapper);
    }

    @Override
    public boolean supports(String platform) {
        if (platform == null) return false;
        String lower = platform.toLowerCase();
        return lower.contains("51job") || lower.contains("前程");
    }

    @Override
    public JobExtractResultDTO extract(Map<?, ?> job, String platform) {
        Map<?, ?> rawDetailJson = getMap(job, "raw_detail_json");
        Map<?, ?> detailJobInfo = getMap(rawDetailJson, "detailJobInfo");
        Map<?, ?> pcDetailJobInfo = getMap(detailJobInfo, "pcdetailJobInfo");
        Map<?, ?> jobAreaLevelDetail = getMap(job, "jobAreaLevelDetail");

        String jobId = getString(job, "jobId");
        if (jobId.isEmpty()) jobId = getString(detailJobInfo, "jobId");

        String jobTitle = getString(job, "jobName");
        if (jobTitle.isEmpty()) jobTitle = getString(detailJobInfo, "jobname");

        String companyId = getString(job, "encCoId");
        if (companyId.isEmpty()) companyId = getString(pcDetailJobInfo, "encryCompanyId");

        String companyName = getString(job, "companyName");
        if (companyName.isEmpty()) companyName = getString(detailJobInfo, "coName");

        String companyFullName = getString(job, "fullCompanyName");
        if (companyFullName.isEmpty()) companyFullName = getString(detailJobInfo, "companyName");

        String salary = getString(job, "provideSalaryString");
        if (salary.isEmpty()) salary = getString(detailJobInfo, "provideSalaryString");

        String location = getString(jobAreaLevelDetail, "cityString");
        if (location.isEmpty()) location = getString(getMap(detailJobInfo, "jobAreaLevelDetail"), "cityString");

        String education = getString(job, "degreeString");
        if (education.isEmpty()) education = getString(detailJobInfo, "degreeString");

        // 新增展示字段
        String experience = getString(job, "workYearString");
        if (experience.isEmpty()) experience = getString(detailJobInfo, "workYearString");

        String city = getString(jobAreaLevelDetail, "cityString");
        if (city.isEmpty()) city = getString(getMap(detailJobInfo, "jobAreaLevelDetail"), "cityString");

        String area = getString(jobAreaLevelDetail, "areaString");
        if (area.isEmpty()) area = getString(getMap(detailJobInfo, "jobAreaLevelDetail"), "areaString");

        String jobUrl = getString(job, "jobHref");
        if (jobUrl.isEmpty()) jobUrl = getString(detailJobInfo, "jobHref");

        String jobDesc = getString(detailJobInfo, "jobDescribe", "jobDescription");
        if (jobDesc.isEmpty()) jobDesc = getString(pcDetailJobInfo, "jobDescribe");

        List<String> skillsList = extractTagsFromMap(detailJobInfo, "jobTags", "tags");
        if (skillsList.isEmpty()) skillsList = extractTagsFromMap(job, "jobTags");
        List<String> welfareListItems = extractTagsFromMap(detailJobInfo, "welfare");
        if (welfareListItems.isEmpty()) welfareListItems = extractTagsFromMap(job, "welfare");

        String hrName = getString(detailJobInfo, "hrName");
        String hrPosition = getString(detailJobInfo, "hrPosition");

        String companyIndustry = getString(detailJobInfo, "industryType1Str", "industryStr");
        if (companyIndustry.isEmpty()) companyIndustry = getString(job, "industryStr");
        String companyScale = getString(detailJobInfo, "companySize");
        if (companyScale.isEmpty()) companyScale = getString(job, "companySize");
        String companyStage = getString(detailJobInfo, "companyNature");
        if (companyStage.isEmpty()) companyStage = getString(job, "companyNature");

        return JobExtractResultDTO.builder()
                .jobId(jobId)
                .title(jobTitle)
                .companyName(companyName)
                .companyFullName(companyFullName)
                .companyId(companyId)
                .salary(salary)
                .location(location)
                .education(education)
                .platform("51job")
                .dataSource(getString(job, "dataSource", "51job"))
                .rawData(toJson(job))
                .rawData2(null)
                .experience(experience)
                .city(city)
                .area(area)
                .jobUrl(jobUrl)
                .jobDesc(jobDesc)
                .skills(toJsonArray(skillsList))
                .welfareList(toJsonArray(welfareListItems))
                .hrName(hrName)
                .hrPosition(hrPosition)
                .companyIndustry(companyIndustry)
                .companyScale(companyScale)
                .companyStage(companyStage)
                .build();
    }
}
