package com.jobpilot.jobpilotserver.strategy.normalizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.vo.NormalizedJobDataVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Job51Normalizer implements JobNormalizerStrategy {

    @Override
    public String getPlatformName() {
        return "51job";
    }

    @Override
    public NormalizedJobDataVO normalize(Job job, JsonNode raw, JsonNode raw2, JsonNode companyInfo, JsonNode bossSingleDetail) {
        JsonNode fallback = raw;
        JsonNode detail = (raw != null && raw.has("raw_detail_json")) ? raw.get("raw_detail_json") : null;
        JsonNode info = (detail != null && detail.has("detailJobInfo")) ? detail.get("detailJobInfo") : null;
        JsonNode hr = (detail != null && detail.has("jobHrInfo")) ? detail.get("jobHrInfo") : null;

        String jobType = getText(fallback, "jobType", getText(info, "jobType", ""));
        boolean isHeadhunter = "1".equals(jobType) || "2".equals(jobType) || "中介".equals(getText(fallback, "companyTypeString", ""));

        return NormalizedJobDataVO.builder()
                .jobUrl(getText(fallback, "jobHref", ""))
                .publishDate(getText(fallback, "issueDateString", getText(info, "issueDate", "")))
                .updateDate(getText(fallback, "updateDateTime", ""))
                .spiderDate(getText(fallback, "创建时间", getText(fallback, "抓取时间", "")))
                .companyIndustry(getText(fallback, "coIndustryAllText", getText(info, "coIndustryAllText", "")))
                .companyStage(getText(fallback, "companyTypeString", ""))
                .companyScale(getText(fallback, "companySizeString", ""))
                .brandName(getText(fallback, "companyName", getText(info, "coName", job.getCompanyName())))
                .companyFullName(getText(fallback, "companyFullName", getText(info, "companyName", job.getCompanyName())))
                .companyId(getText(fallback, "encCoId", ""))
                .hrName(getText(fallback, "hrName", getText(hr, "hrName", "")))
                .hrPosition(getText(fallback, "hrPosition", getText(hr, "hrPosition", "")))
                .hrCompanyName(getText(fallback, "fullCompanyName", getText(fallback, "companyName", job.getCompanyName())))
                .welfareList(extractTags(fallback != null ? fallback.get("welfare") : null, info != null ? info.get("welfare") : null))
                .skills(extractTags(info != null ? info.get("jobKeywordList") : null, fallback != null ? fallback.get("jobKeywordString") : null))
                .jobId(getText(fallback, "jobId", getText(info, "jobId", job.getJobId())))
                .jobName(getText(fallback, "jobName", getText(info, "jobName", job.getTitle())))
                .salaryRange(getText(fallback, "provideSalaryString", getText(info, "provideSalaryString", job.getSalary())))
                .jobDesc(getText(fallback, "jobDescribe", getText(info, "jobDescribe", "暂无描述")))
                .experience(getText(fallback, "workYearString", getText(info, "workYearString", "经验不限")))
                .degree(getText(fallback, "degreeString", getText(info, "degreeString", "学历不限")))
                .positionType(jobType)
                .jobTags(extractTags(fallback != null ? fallback.get("jobTags") : null))
                .city(getText(fallback, "jobAreaString", ""))
                .area("")
                .businessDistrict("")
                .address(getText(info, "address", getText(info, "companyAddress", "")))
                .isHeadhunter(isHeadhunter)
                .clientCompanyName(isHeadhunter ? getText(fallback, "compName", "") : "")
                .dataSource("51job")
                .build();
    }

    private String getText(JsonNode node, String fieldName, String defaultValue) {
        if (node != null && node.has(fieldName) && !node.get(fieldName).isNull()) {
            String val = node.get(fieldName).asText();
            if (!val.trim().isEmpty()) return val.trim();
        }
        return defaultValue;
    }

    private List<String> extractTags(JsonNode... nodes) {
        List<String> res = new ArrayList<>();
        for (JsonNode node : nodes) {
            if (node == null || node.isNull()) continue;
            if (node.isArray()) {
                for (JsonNode item : node) {
                    if (item.isObject()) {
                        if (item.has("text")) res.add(item.get("text").asText());
                        else if (item.has("label")) res.add(item.get("label").asText());
                    } else if (item.isValueNode()) {
                        res.add(item.asText());
                    }
                }
            } else if (node.isTextual()) {
                for (String s : node.asText().split(",")) {
                    if (!s.trim().isEmpty()) res.add(s.trim());
                }
            }
            if (!res.isEmpty()) break;
        }
        return res;
    }
}