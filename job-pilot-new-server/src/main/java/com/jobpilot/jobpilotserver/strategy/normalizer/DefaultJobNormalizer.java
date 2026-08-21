package com.jobpilot.jobpilotserver.strategy.normalizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.vo.NormalizedJobDataVO;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultJobNormalizer implements JobNormalizerStrategy {

    @Override
    public String getPlatformName() {
        return "default";
    }

    @Override
    public NormalizedJobDataVO normalize(Job job, JsonNode raw, JsonNode raw2, JsonNode companyInfo, JsonNode bossSingleDetail) {
        JsonNode fallback = raw != null ? raw : raw2;

        return NormalizedJobDataVO.builder()
                .jobUrl(getText(fallback, "职位链接", getText(fallback, "jobUrl", "")))
                .publishDate(getText(fallback, "发布时间", ""))
                .updateDate(getText(fallback, "更新时间", ""))
                .spiderDate(getText(fallback, "创建时间", ""))
                .companyIndustry(getText(fallback, "公司行业", "未知"))
                .companyStage(getText(fallback, "融资阶段", "未知"))
                .companyScale(getText(fallback, "公司规模", "未知"))
                .brandName(getText(fallback, "brandName", job.getCompanyName()))
                .companyFullName(getText(fallback, "公司全称", job.getCompanyName()))
                .companyId(getText(fallback, "companyId", ""))
                .hrName(getText(fallback, "HR姓名", "未知"))
                .hrPosition(getText(fallback, "HR职位", "未知"))
                .hrCompanyName(job.getCompanyName())
                .welfareList(extractTags(fallback != null ? fallback.get("公司福利") : null))
                .skills(extractTags(fallback != null ? fallback.get("技能标签") : null))
                .jobId(job.getJobId())
                .jobName(job.getTitle())
                .salaryRange(job.getSalary())
                .jobDesc(getText(fallback, "职位描述", "暂无描述"))
                .experience(job.getEducation() != null ? job.getEducation() : "经验不限")
                .degree("学历不限")
                .positionType("")
                .jobTags(new ArrayList<>())
                .city(job.getLocation() != null ? job.getLocation() : "")
                .area("")
                .businessDistrict("")
                .address(getText(fallback, "详细工作地址", ""))
                .isHeadhunter(false)
                .clientCompanyName("")
                .dataSource("兜底")
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
                if (item.isValueNode()) res.add(item.asText());
            }
        }
        return res;
    }
}