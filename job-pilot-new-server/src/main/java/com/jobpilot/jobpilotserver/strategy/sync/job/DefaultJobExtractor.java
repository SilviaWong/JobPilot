package com.jobpilot.jobpilotserver.strategy.sync.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 默认通用职位提取策略 (兜底策略)
 */
@Component
public class DefaultJobExtractor extends AbstractJobExtractor {

    public DefaultJobExtractor(JobMapper jobMapper,
                               CompanyMapper companyMapper,
                               ObjectMapper objectMapper) {
        super(jobMapper, companyMapper, objectMapper);
    }

    @Override
    public boolean supports(String platform) {
        return "default".equalsIgnoreCase(platform);
    }

    @Override
    public JobExtractResultDTO extract(Map<?, ?> job, String platform) {
        String jobId = getString(job, "jobId", "id", "encryptJobId", "number");
        String title = getString(job, "title", "jobName", "jobTitle", "name");
        String companyName = getString(job, "companyName", "brandName", "compName");
        String companyFullName = getString(job, "companyFullName", "fullCompanyName");
        String companyId = getString(job, "companyId", "encryptBrandId", "encCoId", "compId");
        String salary = getString(job, "salary", "salaryDesc", "provideSalaryString");
        String location = getString(job, "location", "cityName", "city", "jobArea");
        String education = getString(job, "education", "jobDegree", "degreeString");

        // 新增展示字段（尽可能从通用键名中提取）
        String experience = getString(job, "experience", "jobExperience", "workingExp", "workYearString");
        String city = getString(job, "city", "cityName");
        String area = getString(job, "area", "areaDistrict");
        String jobUrl = getString(job, "jobUrl", "url", "link", "jobHref");
        String jobDesc = getString(job, "jobDesc", "description", "postDescription");
        String hrName = getString(job, "hrName", "bossName", "recruiterName");
        String hrPosition = getString(job, "hrPosition", "bossTitle", "recruiterTitle");

        List<String> skillsList = extractTagsFromMap(job, "skills", "jobTags", "tags");
        List<String> welfareListItems = extractTagsFromMap(job, "welfareList", "welfare");

        String companyIndustry = getString(job, "industry", "brandIndustry", "industryName");
        String companyScale = getString(job, "companyScale", "brandScaleName", "scaleName");
        String companyStage = getString(job, "companyStage", "brandStageName", "stageName");

        return JobExtractResultDTO.builder()
                .jobId(jobId)
                .title(title)
                .companyName(companyName)
                .companyFullName(companyFullName)
                .companyId(companyId)
                .salary(salary)
                .location(location)
                .education(education)
                .platform(platform != null && !platform.isBlank() ? platform : "未知")
                .dataSource(getString(job, "dataSource", platform))
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
