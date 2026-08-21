package com.jobpilot.jobpilotserver.strategy.sync.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Boss直聘职位提取策略 (对齐 jobExtractors/boss.ts)
 */
@Component
public class BossJobExtractor extends AbstractJobExtractor {

    public BossJobExtractor(JobMapper jobMapper,
                            CompanyMapper companyMapper,
                            ObjectMapper objectMapper) {
        super(jobMapper, companyMapper, objectMapper);
    }

    @Override
    public boolean supports(String platform) {
        if (platform == null) return false;
        String lower = platform.toLowerCase();
        return lower.contains("boss") || lower.contains("直聘");
    }

    @Override
    public JobExtractResultDTO extract(Map<?, ?> job, String platform) {
        Map<?, ?> jobDetail = getMap(job, "jobDetail");
        Map<?, ?> zpData = getMap(jobDetail, "zpData");
        Map<?, ?> jobInfo = getMap(zpData, "jobInfo");
        Map<?, ?> bossInfo = getMap(zpData, "bossInfo");
        Map<?, ?> brandComInfo = getMap(zpData, "brandComInfo");

        String jobId = getString(job, "jobId", "encryptJobId", "职位ID");
        if (jobId.isEmpty()) jobId = getString(jobInfo, "encryptId");

        String jobTitle = getString(job, "jobName", "职位名称");
        if (jobTitle.isEmpty()) jobTitle = getString(jobInfo, "jobName");

        String salary = getString(job, "salaryDesc", "薪资");
        if (salary.isEmpty()) salary = getString(jobInfo, "salaryDesc");

        String location = getString(job, "cityName", "工作地点");
        if (location.isEmpty()) location = getString(jobInfo, "locationName");

        String education = getString(job, "jobDegree", "学历要求");
        if (education.isEmpty()) education = getString(jobInfo, "degreeName");

        String companyName = "";
        String companyFullName = "";
        String companyId = "";

        Object proxyJobObj = job.get("proxyJob");
        int proxyJob = proxyJobObj instanceof Number ? ((Number) proxyJobObj).intValue() : 0;
        boolean isHeadhunter = proxyJob == 1;

        if (proxyJob == 0) {
            companyName = getString(job, "brandName");
            if (companyName.isEmpty()) companyName = getString(bossInfo, "brandName");
            if (companyName.isEmpty()) companyName = getString(brandComInfo, "brandName", "customerBrandName");
            companyFullName = companyName;
            companyId = getString(job, "encryptBrandId");
            if (companyId.isEmpty()) companyId = getString(brandComInfo, "encryptBrandId");
        } else {
            companyName = getString(bossInfo, "brandName");
            companyFullName = companyName;
        }

        // 新增展示字段提取
        String experience = getString(job, "jobExperience", "工作经验");
        if (experience.isEmpty()) experience = getString(jobInfo, "experienceName");

        String city = getString(job, "cityName");
        if (city.isEmpty()) city = getString(jobInfo, "locationName");

        String area = getString(job, "areaDistrict");
        String businessDistrict = getString(job, "businessDistrict");

        String encryptJobId = getString(job, "encryptJobId");
        if (encryptJobId.isEmpty()) encryptJobId = getString(jobInfo, "encryptId");
        if (encryptJobId.isEmpty()) encryptJobId = jobId;
        String jobUrl = (encryptJobId != null && !encryptJobId.isEmpty())
                ? "https://www.zhipin.com/job_detail/" + encryptJobId + ".html" : "";

        String jobDesc = getString(jobInfo, "postDescription");

        String hrName = getString(job, "bossName");
        if (hrName.isEmpty()) hrName = getString(bossInfo, "name");
        String hrPosition = getString(job, "bossTitle");
        if (hrPosition.isEmpty()) hrPosition = getString(bossInfo, "title");

        List<String> skillsList = extractTagsFromMap(job, "skills");
        if (skillsList.isEmpty()) skillsList = extractTagsFromMap(jobInfo, "showSkills");
        List<String> welfareListItems = extractTagsFromMap(job, "welfareList");
        if (welfareListItems.isEmpty()) welfareListItems = extractTagsFromMap(brandComInfo, "labels");

        String clientCompany = isHeadhunter
                ? getString(job, "brandName", getString(brandComInfo, "customerBrandName", "")) : "";

        // 公司附加字段
        String companyIndustry = getString(job, "brandIndustry");
        if (companyIndustry.isEmpty()) companyIndustry = getString(brandComInfo, "industryName");
        String companyStage = getString(job, "brandStageName");
        if (companyStage.isEmpty()) companyStage = getString(brandComInfo, "customerBrandStageName", "stageName");
        String companyScale = getString(job, "brandScaleName");
        if (companyScale.isEmpty()) companyScale = getString(brandComInfo, "scaleName");

        return JobExtractResultDTO.builder()
                .jobId(jobId)
                .title(jobTitle)
                .companyName(companyName)
                .companyFullName(companyFullName)
                .companyId(companyId)
                .salary(salary)
                .location(location)
                .education(education)
                .platform("Boss直聘")
                .dataSource(getString(job, "dataSource", "Boss直聘"))
                .rawData(toJson(job))
                .rawData2(null)
                // 新增字段
                .experience(experience)
                .city(city)
                .area(area)
                .businessDistrict(businessDistrict)
                .jobUrl(jobUrl)
                .jobDesc(jobDesc)
                .skills(toJsonArray(skillsList))
                .welfareList(toJsonArray(welfareListItems))
                .hrName(hrName)
                .hrPosition(hrPosition)
                .publishDate("")
                .updateDate("")
                .isHeadhunter(isHeadhunter)
                .clientCompanyName(clientCompany)
                .companyIndustry(companyIndustry)
                .companyScale(companyScale)
                .companyStage(companyStage)
                .build();
    }
}
