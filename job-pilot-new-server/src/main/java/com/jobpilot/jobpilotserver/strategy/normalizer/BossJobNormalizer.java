package com.jobpilot.jobpilotserver.strategy.normalizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.vo.NormalizedJobDataVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BossJobNormalizer implements JobNormalizerStrategy {

    @Override
    public String getPlatformName() {
        return "Boss直聘";
    }

    @Override
    public NormalizedJobDataVO normalize(Job job, JsonNode raw, JsonNode raw2, JsonNode companyInfo, JsonNode bossSingleDetail) {
        JsonNode bossSingle = bossSingleDetail != null ? bossSingleDetail : (raw != null && raw.has("boss_single_detail") ? raw.get("boss_single_detail") : raw);
        JsonNode jd = (raw != null && raw.has("jobDetail") && raw.get("jobDetail").has("zpData")) ? raw.get("jobDetail").get("zpData") : null;
        JsonNode info = jd != null && jd.has("jobInfo") ? jd.get("jobInfo") : null;
        JsonNode boss = jd != null && jd.has("bossInfo") ? jd.get("bossInfo") : null;
        JsonNode comp = jd != null && jd.has("brandComInfo") ? jd.get("brandComInfo") : null;

        String bossEncryptJobId = getText(raw, "encryptJobId", getText(info, "encryptId", job.getJobId()));
        String jobUrl = bossEncryptJobId != null && !bossEncryptJobId.isEmpty() ? "https://www.zhipin.com/job_detail/" + bossEncryptJobId + ".html" : "";

        int proxyJob = raw != null && raw.has("proxyJob") ? raw.get("proxyJob").asInt() : (info != null && info.has("proxyJob") ? info.get("proxyJob").asInt() : 0);
        boolean isHeadhunter = proxyJob == 1;

        String brandName = isHeadhunter ? getText(boss, "brandName", getText(bossSingle, "HR职位", "")) : getText(raw, "brandName", getText(comp, "brandName", getText(comp, "customerBrandName", getText(bossSingle, "公司名称", ""))));
        String companyFullName = isHeadhunter ? getText(boss, "brandName", getText(bossSingle, "HR职位", "")) : getText(raw, "_fetched_companyFullName", getText(raw, "公司全称", getText(bossSingle, "公司全称", brandName)));

        List<String> welfareList = extractTags(raw != null ? raw.get("welfareList") : null, comp != null ? comp.get("labels") : null, bossSingle != null ? bossSingle.get("公司福利") : null);
        List<String> skills = extractTags(raw != null ? raw.get("skills") : null, info != null ? info.get("showSkills") : null, bossSingle != null ? bossSingle.get("技能标签") : null);
        List<String> jobTags = extractTags(raw != null ? raw.get("jobLabels") : null);

        return NormalizedJobDataVO.builder()
                .jobUrl(jobUrl)
                .publishDate("")
                .updateDate(getText(bossSingle, "页面更新时间", getText(bossSingle, "最后刷新时间", "")))
                .spiderDate(getText(raw, "创建时间", getText(bossSingle, "抓取时间", "")))
                .companyIndustry(getText(raw, "brandIndustry", getText(comp, "industryName", getText(bossSingle, "公司行业", ""))))
                .companyStage(getText(raw, "brandStageName", getText(comp, "customerBrandStageName", getText(comp, "stageName", ""))))
                .companyScale(getText(raw, "brandScaleName", getText(comp, "scaleName", getText(bossSingle, "公司规模", ""))))
                .brandName(brandName)
                .companyFullName(companyFullName)
                .companyId(getText(raw, "encryptBrandId", getText(comp, "encryptBrandId", getText(boss, "brandName", ""))))
                .hrName(getText(raw, "bossName", getText(boss, "name", getText(bossSingle, "HR姓名", ""))))
                .hrPosition(getText(raw, "bossTitle", getText(boss, "title", getText(bossSingle, "HR职位", ""))))
                .hrCompanyName(getText(boss, "brandName", getText(raw, "brandName", "")))
                .welfareList(welfareList)
                .skills(skills)
                .jobId(bossEncryptJobId)
                .jobName(getText(raw, "jobName", getText(info, "jobName", getText(bossSingle, "职位名称", job.getTitle()))))
                .salaryRange(getText(raw, "salaryDesc", getText(info, "salaryDesc", getText(bossSingle, "薪资待遇", job.getSalary()))))
                .jobDesc(getText(info, "postDescription", getText(bossSingle, "职位描述", "暂无描述")))
                .experience(getText(raw, "jobExperience", getText(info, "experienceName", getText(bossSingle, "工作经验", "经验不限"))))
                .degree(getText(raw, "jobDegree", getText(info, "degreeName", getText(bossSingle, "学历要求", "学历不限"))))
                .positionType(String.valueOf(proxyJob))
                .jobTags(jobTags)
                .city(getText(raw, "cityName", getText(info, "locationName", "")))
                .area(getText(raw, "areaDistrict", ""))
                .businessDistrict(getText(raw, "businessDistrict", ""))
                .address(getText(info, "address", getText(bossSingle, "详细完整地址", "")))
                .isHeadhunter(isHeadhunter)
                .clientCompanyName(isHeadhunter ? getText(raw, "brandName", getText(comp, "customerBrandName", "")) : "")
                .dataSource("BOSS直聘")
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