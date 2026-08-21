package com.jobpilot.jobpilotserver.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.vo.NormalizedJobDataVO;
import com.jobpilot.jobpilotserver.service.DashboardService;
import com.jobpilot.jobpilotserver.strategy.normalizer.JobNormalizerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据大盘统计业务实现类
 * <p>
 * 负责聚合统计全库职位的薪资分布、经验要求分布、核心技能标签词频统计等宏观数据指标，
 * 为前端仪表盘的可视化图表提供多维度数据支撑。
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private final JobMapper jobMapper;
    private final JobNormalizerFactory jobNormalizerFactory;
    private final ObjectMapper objectMapper;

    public DashboardServiceImpl(JobMapper jobMapper, JobNormalizerFactory jobNormalizerFactory, ObjectMapper objectMapper) {
        this.jobMapper = jobMapper;
        this.jobNormalizerFactory = jobNormalizerFactory;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取数据大盘综合统计指标
     * <p>
     * 统计维度包括：
     * 1. 薪资区间分布（10k以下、10-20k、20-30k、30k以上、面议）；
     * 2. 经验年限分布（1-3年、3-5年、5-10年、10年以上、其他/不限）；
     * 3. 热门技能标签频次统计；
     * 4. 岗位总数。
     *
     * @return 包含 salary、experience、skills、totalJobs 的统计结果 Map
     */
    @Override
    public Map<String, Object> getDashboardStats() {
        // 查询数据库中所有职位记录
        List<Job> jobs = jobMapper.selectList(null);

        // 1. 初始化薪资分布统计桶（使用 LinkedHashMap 保持区间展示顺序）
        Map<String, Integer> salaryDist = new LinkedHashMap<>();
        salaryDist.put("10k以下", 0);
        salaryDist.put("10-20k", 0);
        salaryDist.put("20-30k", 0);
        salaryDist.put("30k以上", 0);
        salaryDist.put("面议", 0);

        // 2. 初始化工作经验分布与技能标签分布映射
        Map<String, Integer> expDist = new LinkedHashMap<>();
        Map<String, Integer> skillsDist = new LinkedHashMap<>();

        // 3. 遍历所有职位，优先读实体清洗字段，兜底降级读归一化 VO 进行聚合统计
        for (Job job : jobs) {
            String salary = job.getSalary() != null ? job.getSalary() : "";
            String expStr = job.getExperience();
            String skillsJson = job.getSkills();

            // 若实体字段尚未回填，平滑降级调用 Normalizer 提取
            NormalizedJobDataVO normalized = null;
            if (salary.isEmpty() || expStr == null || skillsJson == null) {
                JsonNode rawNode = parseJson(job.getRawData());
                JsonNode rawNode2 = parseJson(job.getRawData2());
                normalized = jobNormalizerFactory.normalize(job, rawNode, rawNode2, null, null);
                if (salary.isEmpty() && normalized.getSalaryRange() != null) salary = normalized.getSalaryRange();
                if (expStr == null && normalized.getExperience() != null) expStr = normalized.getExperience();
            }

            // ==================== 薪资区间归类计算 ====================
            int maxK = -1;

            // 正则匹配包含 k/K 的薪资描述（例如 "15-25K" 取最高 25k）
            Matcher kMatcher = Pattern.compile("(\\d+)\\s*[kK]").matcher(salary);
            while (kMatcher.find()) {
                maxK = Math.max(maxK, Integer.parseInt(kMatcher.group(1)));
            }
            // 若未匹配到 k，尝试匹配 "万" 单位（例如 "1.5-2.5万" -> 25k）
            if (maxK < 0 && salary.contains("万")) {
                Matcher wanMatcher = Pattern.compile("(\\d+(\\.\\d+)?)\\s*万").matcher(salary);
                while (wanMatcher.find()) {
                    maxK = Math.max(maxK, (int) (Double.parseDouble(wanMatcher.group(1)) * 10));
                }
            } else if (maxK < 0 && salary.contains("千")) { // 尝试匹配 "千" 单位
                Matcher qianMatcher = Pattern.compile("(\\d+(\\.\\d+)?)\\s*千").matcher(salary);
                while (qianMatcher.find()) {
                    maxK = Math.max(maxK, (int) Double.parseDouble(qianMatcher.group(1)));
                }
            }

            // 根据换算后的最高月薪 (k) 划分区间
            if (maxK > 0) {
                if (maxK < 10) salaryDist.put("10k以下", salaryDist.get("10k以下") + 1);
                else if (maxK <= 20) salaryDist.put("10-20k", salaryDist.get("10-20k") + 1);
                else if (maxK <= 30) salaryDist.put("20-30k", salaryDist.get("20-30k") + 1);
                else salaryDist.put("30k以上", salaryDist.get("30k以上") + 1);
            } else {
                salaryDist.put("面议", salaryDist.get("面议") + 1);
            }

            // ==================== 经验要求归类计算 ====================
            String exp = expStr != null ? expStr : "未知";
            if (exp.contains("3-5") || exp.contains("3年") || exp.contains("4年")) exp = "3-5年";
            else if (exp.contains("1-3") || exp.contains("1年") || exp.contains("2年")) exp = "1-3年";
            else if (exp.contains("5-10") || exp.contains("5年") || exp.contains("6年") || exp.contains("7年") || exp.contains("8年")) exp = "5-10年";
            else if (exp.contains("10")) exp = "10年以上";
            else exp = "其他/不限";

            expDist.put(exp, expDist.getOrDefault(exp, 0) + 1);

            // ==================== 技能标签频次统计 ====================
            List<String> tagsList = null;
            if (skillsJson != null && !skillsJson.isEmpty()) {
                try {
                    tagsList = objectMapper.readValue(skillsJson, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
                } catch (Exception ignored) {}
            }
            if (tagsList == null && normalized != null) {
                tagsList = normalized.getJobTags();
            }
            if (tagsList != null) {
                for (String t : tagsList) {
                    if (t != null && !t.trim().isEmpty()) {
                        String tag = t.trim();
                        skillsDist.put(tag, skillsDist.getOrDefault(tag, 0) + 1);
                    }
                }
            }
        }

        // 4. 组装返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("salary", salaryDist);
        data.put("experience", expDist);
        data.put("skills", skillsDist);
        data.put("totalJobs", jobs.size());

        return data;
    }

    /**
     * 辅助解析 JSON 字符串为 JsonNode
     *
     * @param jsonStr 原始 JSON 字符串
     * @return JsonNode 对象，若解析失败或为空则返回 null
     */
    private JsonNode parseJson(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) return null;
        try {
            return objectMapper.readTree(jsonStr);
        } catch (Exception e) {
            return null;
        }
    }
}