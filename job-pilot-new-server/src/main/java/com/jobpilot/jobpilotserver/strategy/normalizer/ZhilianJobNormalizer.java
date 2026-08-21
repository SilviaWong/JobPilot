package com.jobpilot.jobpilotserver.strategy.normalizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.vo.NormalizedJobDataVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ZhilianJobNormalizer implements JobNormalizerStrategy {

    @Override
    public String getPlatformName() {
        return "智联";
    }

    @Override
    public NormalizedJobDataVO normalize(Job job, JsonNode raw, JsonNode raw2, JsonNode companyInfo, JsonNode bossSingleDetail) {
        JsonNode fallback = raw;
        JsonNode jobDetailData = (fallback != null && fallback.has("jobDetailData")) ? fallback.get("jobDetailData") : null;
        JsonNode position = (jobDetailData != null && jobDetailData.has("position")) ? jobDetailData.get("position") : null;
        JsonNode base = (position != null && position.has("base")) ? position.get("base") : null;
        JsonNode staff = (jobDetailData != null && jobDetailData.has("staff")) ? jobDetailData.get("staff") : null;

        JsonNode jobDetailInfo = (fallback != null && fallback.has("jobDetail")) ? fallback.get("jobDetail") : null;
        JsonNode detailedCompany = (jobDetailInfo != null && jobDetailInfo.has("detailedCompany")) ? jobDetailInfo.get("detailedCompany") : null;
        JsonNode detailedPosition = (jobDetailInfo != null && jobDetailInfo.has("detailedPosition")) ? jobDetailInfo.get("detailedPosition") : null;

        int jobType = fallback != null && fallback.has("proxyModel") && fallback.get("proxyModel").has("recruitPosition") ? fallback.get("proxyModel").get("recruitPosition").asInt() : 0;
        boolean isHeadhunter = jobType > 0;

        String compName = isHeadhunter ? getText(staff, "companyName", "") : getText(fallback, "companyName", getText(detailedCompany, "companyName", job.getCompanyName()));

        return NormalizedJobDataVO.builder()
                .jobUrl(getText(fallback, "positionUrl", getText(detailedPosition, "positionUrl", "")))
                .publishDate(getText(fallback, "firstPublishTime", ""))
                .updateDate(getText(fallback, "publishTime", ""))
                .spiderDate(getText(fallback, "创建时间", getText(fallback, "抓取时间", "")))
                .companyIndustry(getText(fallback, "industryName", getText(detailedCompany, "industryNameLevel", "")))
                .companyStage(getText(detailedCompany, "financingStageName", ""))
                .companyScale(getText(fallback, "companySize", getText(detailedCompany, "companySize", "")))
                .brandName(compName)
                .companyFullName(compName)
                .companyId(getText(fallback, "companyNumber", getText(detailedCompany, "companyNumber", "")))
                .hrName(getText(staff, "staffName", ""))
                .hrPosition(getText(staff, "hrJob", ""))
                .hrCompanyName(getText(staff, "companyName", compName))
                .welfareList(extractTags(fallback != null ? fallback.get("jobKnowledgeWelfareFeatures") : null))
                .skills(extractTags(fallback != null ? fallback.get("jobSkillTags") : null, detailedPosition != null ? detailedPosition.get("skillLabel") : null))
                .jobId(getText(fallback, "number", getText(base, "positionNumber", job.getJobId())))
                .jobName(getText(fallback, "name", getText(base, "positionName", job.getTitle())))
                .salaryRange(getText(fallback, "salary60", getText(base, "salary", job.getSalary())))
                .jobDesc(getText(detailedPosition, "description", "暂无描述"))
                .experience(getText(base, "positionWorkingExp", getText(fallback, "workingExp", "经验不限")))
                .degree(getText(base, "education", getText(fallback, "education", "学历不限")))
                .positionType(String.valueOf(jobType))
                .jobTags(extractTags(fallback != null ? fallback.get("showSkillTags") : null))
                .city(getText(fallback, "workCity", ""))
                .area(getText(fallback, "cityDistrict", ""))
                .businessDistrict(getText(fallback, "streetName", ""))
                .address(getText(detailedPosition, "workAddress", ""))
                .isHeadhunter(isHeadhunter)
                .clientCompanyName("")
                .dataSource("智联")
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
                        if (item.has("name")) res.add(item.get("name").asText());
                        else if (item.has("value")) res.add(item.get("value").asText());
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