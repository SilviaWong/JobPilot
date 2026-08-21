package com.jobpilot.jobpilotserver.strategy.normalizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.vo.NormalizedJobDataVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LiepinJobNormalizer implements JobNormalizerStrategy {

    @Override
    public String getPlatformName() {
        return "猎聘";
    }

    @Override
    public NormalizedJobDataVO normalize(Job job, JsonNode raw, JsonNode raw2, JsonNode companyInfo, JsonNode bossSingleDetail) {
        JsonNode fallback = raw2 != null ? raw2 : raw;
        JsonNode comp = fallback != null && fallback.has("comp") ? fallback.get("comp") : null;
        JsonNode jobInfo = fallback != null && fallback.has("job") ? fallback.get("job") : null;
        JsonNode jdJson = fallback != null && fallback.has("jobDetailJson") ? fallback.get("jobDetailJson") : null;

        boolean isHeadhunter = jobInfo != null && jobInfo.has("jobKind") && "1".equals(jobInfo.get("jobKind").asText());
        String brandName = getText(comp, "fullCompanyName", getText(comp, "compName", job.getCompanyName()));

        return NormalizedJobDataVO.builder()
                .jobUrl(getText(jobInfo, "link", getText(jdJson, "url", "")))
                .publishDate(getText(jdJson, "datePosted", ""))
                .updateDate(getText(jdJson, "domUpdateTime", ""))
                .spiderDate(getText(fallback, "创建时间", getText(fallback, "抓取时间", "")))
                .companyIndustry(getText(jdJson, "industry", getText(comp, "compIndustry", "")))
                .companyStage(getText(comp, "compStage", ""))
                .companyScale(getText(comp, "compScale", ""))
                .brandName(brandName)
                .companyFullName(brandName)
                .companyId(getText(comp, "compId", ""))
                .hrName(getText(fallback != null && fallback.has("recruiter") ? fallback.get("recruiter") : null, "recruiterName", ""))
                .hrPosition(getText(fallback != null && fallback.has("recruiter") ? fallback.get("recruiter") : null, "recruiterTitle", ""))
                .hrCompanyName(brandName)
                .welfareList(extractTags(comp != null ? comp.get("compTags") : null))
                .skills(extractTags(jobInfo != null ? jobInfo.get("labels") : null))
                .jobId(getText(jobInfo, "jobId", job.getJobId()))
                .jobName(getText(jobInfo, "title", getText(jdJson, "title", job.getTitle())))
                .salaryRange(getText(jobInfo, "salary", getText(jdJson, "salary", job.getSalary())))
                .jobDesc(getText(jdJson, "description", "暂无描述"))
                .experience(getText(jobInfo, "requireWorkYears", "经验不限"))
                .degree(getText(jobInfo, "requireEduLevel", "学历不限"))
                .positionType(getText(jobInfo, "jobKind", ""))
                .jobTags(extractTags(jobInfo != null ? jobInfo.get("jobTags") : null))
                .city(getText(jobInfo, "dqCityName", ""))
                .area(getText(jobInfo, "dqAreaName", ""))
                .businessDistrict(getText(jobInfo, "businessDistrict", ""))
                .address(getText(jobInfo, "dq", ""))
                .isHeadhunter(isHeadhunter)
                .clientCompanyName(isHeadhunter ? getText(comp, "compName", "") : "")
                .dataSource("猎聘")
                .build();
    }

    private String getText(JsonNode node, String fieldName, String defaultValue) {
        if (node != null && node.has(fieldName) && !node.get(fieldName).isNull()) {
            String val = node.get(fieldName).asText();
            if (!val.trim().isEmpty()) return val.trim();
        }
        return defaultValue;
    }

    private List<String> extractTags(JsonNode node) {
        List<String> res = new ArrayList<>();
        if (node != null && node.isArray()) {
            for (JsonNode item : node) {
                if (item.isObject() && item.has("name")) res.add(item.get("name").asText());
                else if (item.isValueNode()) res.add(item.asText());
            }
        }
        return res;
    }
}