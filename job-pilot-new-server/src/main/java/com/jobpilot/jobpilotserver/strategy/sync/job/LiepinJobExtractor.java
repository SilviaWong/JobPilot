package com.jobpilot.jobpilotserver.strategy.sync.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 猎聘职位提取策略 (对齐 jobExtractors/liepin.ts)
 */
@Component
public class LiepinJobExtractor extends AbstractJobExtractor {

    public LiepinJobExtractor(JobMapper jobMapper,
                              CompanyMapper companyMapper,
                              ObjectMapper objectMapper) {
        super(jobMapper, companyMapper, objectMapper);
    }

    @Override
    public boolean supports(String platform) {
        if (platform == null) return false;
        String lower = platform.toLowerCase();
        return lower.contains("liepin") || lower.contains("猎聘");
    }

    @Override
    public JobExtractResultDTO extract(Map<?, ?> job, String platform) {
        Map<?, ?> compInfo = getMap(job, "comp");
        Map<?, ?> jobInfo = getMap(job, "job");
        Map<?, ?> jobDetailJson = getMap(job, "jobDetailJson");
        Map<?, ?> supplementalDomData = getMap(jobDetailJson, "supplementalDomData");

        String jobId = getString(jobInfo, "jobId");
        if (jobId.isEmpty()) jobId = getString(getMap(jobDetailJson, "identifier"), "value");

        String jobTitle = getString(jobInfo, "title");
        if (jobTitle.isEmpty()) jobTitle = getString(jobDetailJson, "title");

        String salary = getString(jobInfo, "salary");
        if (salary.isEmpty()) salary = getString(supplementalDomData, "salary");

        String location = getString(jobInfo, "dqCityName", "dq");
        if (location.isEmpty()) location = getString(getMap(getMap(jobDetailJson, "jobLocation"), "address"), "addressRegion");

        String education = getString(jobInfo, "requireEduLevel");
        if (education.isEmpty()) education = getString(jobDetailJson, "educationRequirements");

        String companyId = getString(compInfo, "compId");
        if (companyId.isEmpty()) {
            String link = getString(compInfo, "link");
            Matcher m = Pattern.compile("/company/(\\d+)").matcher(link);
            if (m.find()) companyId = m.group(1);
        }

        String hrCoName = "";
        Object recruiterInfoObj = supplementalDomData.get("recruiterInfo");
        if (recruiterInfoObj instanceof List<?> rList && !rList.isEmpty()) {
            Object lastItem = rList.get(rList.size() - 1);
            if (lastItem instanceof String s) {
                hrCoName = s.replaceFirst("^·\\s*", "").trim();
            }
        }

        String companyName;
        String jobKind = String.valueOf(jobInfo.get("jobKind"));
        boolean isHeadhunter = "1".equals(jobKind);
        if (isHeadhunter) {
            companyName = getString(compInfo, "fullCompanyName");
            if (companyName.isEmpty()) companyName = hrCoName;
        } else {
            companyName = getString(compInfo, "compName");
        }
        String companyFullName = getString(compInfo, "fullCompanyName");
        if (companyFullName.isEmpty()) companyFullName = hrCoName;

        // 新增展示字段
        String experience = getString(jobInfo, "requireWorkYears");
        if (experience.isEmpty()) experience = getString(jobDetailJson, "experienceRequirements");

        String city = getString(jobInfo, "dqCityName");
        String area = getString(jobInfo, "dq");

        String jobUrl = getString(jobInfo, "link");
        if (jobUrl.isEmpty()) jobUrl = getString(jobDetailJson, "url");

        String jobDesc = getString(jobInfo, "jobDescContent", "jobDesc");
        if (jobDesc.isEmpty()) jobDesc = getString(jobDetailJson, "description");

        List<String> skillsList = extractTagsFromMap(jobInfo, "labels", "skills");
        if (skillsList.isEmpty()) skillsList = extractTagsFromMap(supplementalDomData, "skills");
        List<String> welfareListItems = extractTagsFromMap(supplementalDomData, "welfare");

        String hrName = "";
        String hrPosition = "";
        Object recruiterInfo = supplementalDomData.get("recruiterInfo");
        if (recruiterInfo instanceof List<?> rList2 && rList2.size() >= 2) {
            if (rList2.get(0) instanceof String s) hrName = s.trim();
            if (rList2.get(1) instanceof String s) hrPosition = s.replaceFirst("^·\\s*", "").trim();
        }

        String clientCompany = isHeadhunter ? getString(compInfo, "compName") : "";

        String companyIndustry = getString(compInfo, "industry");
        String companyScale = getString(compInfo, "compScale", "scale");
        String companyStage = getString(compInfo, "compStage", "stage");

        return JobExtractResultDTO.builder()
                .jobId(jobId)
                .title(jobTitle)
                .companyName(companyName)
                .companyFullName(companyFullName)
                .companyId(companyId)
                .salary(salary)
                .location(location)
                .education(education)
                .platform("猎聘")
                .dataSource(getString(job, "dataSource", "猎聘"))
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
                .isHeadhunter(isHeadhunter)
                .clientCompanyName(clientCompany)
                .companyIndustry(companyIndustry)
                .companyScale(companyScale)
                .companyStage(companyStage)
                .build();
    }
}
