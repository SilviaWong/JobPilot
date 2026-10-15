package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.InterviewMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.mapper.JobStatusLogMapper;
import com.jobpilot.jobpilotserver.model.entity.Interview;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.entity.JobStatusLog;
import com.jobpilot.jobpilotserver.model.vo.NormalizedJobDataVO;
import com.jobpilot.jobpilotserver.service.DashboardService;
import com.jobpilot.jobpilotserver.strategy.normalizer.JobNormalizerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 数据大盘统计业务实现类
 * <p>
 * 负责聚合统计全库职位的薪资分布、经验要求分布、核心技能标签词频统计等宏观数据指标，
 * 为前端仪表盘的可视化图表提供多维度数据支撑。
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private final JobMapper jobMapper;
    private final InterviewMapper interviewMapper;
    private final JobStatusLogMapper jobStatusLogMapper;
    private final JobNormalizerFactory jobNormalizerFactory;
    private final ObjectMapper objectMapper;

    public DashboardServiceImpl(JobMapper jobMapper,
                                InterviewMapper interviewMapper,
                                JobStatusLogMapper jobStatusLogMapper,
                                JobNormalizerFactory jobNormalizerFactory,
                                ObjectMapper objectMapper) {
        this.jobMapper = jobMapper;
        this.interviewMapper = interviewMapper;
        this.jobStatusLogMapper = jobStatusLogMapper;
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

    @Override
    public Map<String, Object> getFunnelAnalytics() {
        long now = System.currentTimeMillis();
        final long DAY_MS = 24 * 60 * 60 * 1000L;

        // 1. 获取所有相关的 Job
        List<Job> candidateJobs = jobMapper.selectList(new QueryWrapper<Job>()
                .isNotNull("applyStatus")
                .ne("applyStatus", "none")
                .or()
                .eq("isFavorited", true));

        // 2. 获取所有的 Interviews 和 JobStatusLogs，并以 jobId / jobRecordId 聚合
        List<Interview> allInterviews = interviewMapper.selectList(new QueryWrapper<Interview>().orderByAsc("time"));
        List<JobStatusLog> allLogs = jobStatusLogMapper.selectList(new QueryWrapper<JobStatusLog>().orderByAsc("createdAt"));

        Map<String, List<Interview>> interviewMap = new HashMap<>();
        for (Interview interview : allInterviews) {
            if (interview.getJobId() != null) {
                interviewMap.computeIfAbsent(interview.getJobId(), k -> new ArrayList<>()).add(interview);
            }
        }

        Map<String, List<JobStatusLog>> logMap = new HashMap<>();
        for (JobStatusLog log : allLogs) {
            if (log.getJobRecordId() != null) {
                logMap.computeIfAbsent(log.getJobRecordId(), k -> new ArrayList<>()).add(log);
            }
        }

        // 如果有些职位状态为 none 且未收藏，但有 interview 或 statusLog，也加入统计池
        Set<String> trackedJobIds = candidateJobs.stream().map(Job::getId).collect(Collectors.toSet());
        List<String> missingJobIds = new ArrayList<>();
        for (String jId : interviewMap.keySet()) {
            if (!trackedJobIds.contains(jId)) missingJobIds.add(jId);
        }
        for (String jId : logMap.keySet()) {
            if (!trackedJobIds.contains(jId) && !missingJobIds.contains(jId)) missingJobIds.add(jId);
        }

        List<Job> jobs = new ArrayList<>(candidateJobs);
        if (!missingJobIds.isEmpty()) {
            List<Job> additionalJobs = jobMapper.selectBatchIds(missingJobIds);
            if (additionalJobs != null) {
                jobs.addAll(additionalJobs);
            }
        }

        // 3. 统计计数器
        int cumToApply = 0;
        int cumApplied = 0;
        int cumInterview1 = 0;
        int cumInterview2 = 0;
        int cumOffer = 0;
        int cumRejected = 0;

        Map<String, Integer> currentDist = new LinkedHashMap<>();
        currentDist.put("to-apply", 0);
        currentDist.put("applied", 0);
        currentDist.put("interview-1", 0);
        currentDist.put("interview-2", 0);
        currentDist.put("offer", 0);
        currentDist.put("rejected", 0);

        List<Double> durationsAppliedToI1 = new ArrayList<>();
        List<Double> durationsI1ToI2 = new ArrayList<>();
        List<Double> durationsI2ToOffer = new ArrayList<>();
        List<Double> durationsFullCycle = new ArrayList<>();

        Map<String, Integer> rejectionBreakdown = new LinkedHashMap<>();
        rejectionBreakdown.put("简历初筛未通过", 0);
        rejectionBreakdown.put("技术一面未通过", 0);
        rejectionBreakdown.put("复试/终面未通过", 0);
        rejectionBreakdown.put("意向阶段放弃", 0);

        List<Map<String, Object>> stagnantJobs = new ArrayList<>();

        // 初始化近 12 周时间线 (每周一到周日)
        Map<String, WeeklyBucket> weeklyMap = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate thisMon = today.with(DayOfWeek.MONDAY);
        DateTimeFormatter keyFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("MM/dd");

        for (int w = 11; w >= 0; w--) {
            LocalDate mon = thisMon.minusWeeks(w);
            LocalDate sun = mon.plusDays(6);
            String key = mon.format(keyFmt);
            String label = mon.format(labelFmt) + "~" + sun.format(labelFmt);
            weeklyMap.put(key, new WeeklyBucket(label));
        }

        for (Job job : jobs) {
            String status = job.getApplyStatus() != null ? job.getApplyStatus() : "none";
            if ("none".equals(status) && Boolean.TRUE.equals(job.getIsFavorited())) {
                status = "to-apply";
            }
            if (currentDist.containsKey(status)) {
                currentDist.put(status, currentDist.get(status) + 1);
            }

            List<JobStatusLog> logs = logMap.getOrDefault(job.getId(), Collections.emptyList());
            List<Interview> interviews = interviewMap.getOrDefault(job.getId(), Collections.emptyList());

            Set<String> passed = new HashSet<>();
            passed.add(status);
            for (JobStatusLog l : logs) {
                if (l.getToStatus() != null) passed.add(l.getToStatus());
                if (l.getFromStatus() != null && !"normal".equals(l.getFromStatus()) && !"none".equals(l.getFromStatus())) {
                    passed.add(l.getFromStatus());
                }
            }
            if (!interviews.isEmpty()) {
                passed.add("interview-1");
                if (interviews.size() >= 2) {
                    passed.add("interview-2");
                }
            }

            if (passed.contains("to-apply") || Boolean.TRUE.equals(job.getIsFavorited()) || passed.contains("applied") || passed.contains("interview-1")) {
                cumToApply++;
            }
            if (passed.contains("applied") || job.getAppliedAt() != null || passed.contains("interview-1")) {
                cumApplied++;
            }
            if (passed.contains("interview-1") || passed.contains("interview-2") || !interviews.isEmpty()) {
                cumInterview1++;
            }
            if (passed.contains("interview-2") || passed.contains("offer") || interviews.size() >= 2) {
                cumInterview2++;
            }
            if (passed.contains("offer")) {
                cumOffer++;
            }
            if (passed.contains("rejected")) {
                cumRejected++;
            }

            Long tApp = job.getAppliedAt() != null ? job.getAppliedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null;
            Long tI1 = null;
            Long tI2 = null;
            Long tOff = null;

            for (JobStatusLog l : logs) {
                long lt = l.getCreatedAt() != null ? l.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0L;
                if ("applied".equals(l.getToStatus()) && (tApp == null || lt < tApp)) tApp = lt;
                if ("interview-1".equals(l.getToStatus()) && (tI1 == null || lt < tI1)) tI1 = lt;
                if ("interview-2".equals(l.getToStatus()) && (tI2 == null || lt < tI2)) tI2 = lt;
                if ("offer".equals(l.getToStatus()) && (tOff == null || lt < tOff)) tOff = lt;
            }

            if (tI1 == null && !interviews.isEmpty() && interviews.get(0).getTime() != null) {
                tI1 = interviews.get(0).getTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }
            if (tI2 == null && interviews.size() > 1 && interviews.get(1).getTime() != null) {
                tI2 = interviews.get(1).getTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }

            if (tApp != null && tI1 != null && tI1 >= tApp) {
                double d = Math.round(((double) (tI1 - tApp) / DAY_MS) * 10.0) / 10.0;
                if (d >= 0 && d <= 60) durationsAppliedToI1.add(d);
            }
            if (tI1 != null && tI2 != null && tI2 >= tI1) {
                double d = Math.round(((double) (tI2 - tI1) / DAY_MS) * 10.0) / 10.0;
                if (d >= 0 && d <= 60) durationsI1ToI2.add(d);
            }
            if (tI2 != null && tOff != null && tOff >= tI2) {
                double d = Math.round(((double) (tOff - tI2) / DAY_MS) * 10.0) / 10.0;
                if (d >= 0 && d <= 60) durationsI2ToOffer.add(d);
            }
            if (tApp != null && tOff != null && tOff >= tApp) {
                double d = Math.round(((double) (tOff - tApp) / DAY_MS) * 10.0) / 10.0;
                if (d >= 0 && d <= 90) durationsFullCycle.add(d);
            }

            if ("rejected".equals(status)) {
                JobStatusLog rejectLog = null;
                for (int i = logs.size() - 1; i >= 0; i--) {
                    if ("rejected".equals(logs.get(i).getToStatus())) {
                        rejectLog = logs.get(i);
                        break;
                    }
                }
                String prev = rejectLog != null && rejectLog.getFromStatus() != null ? rejectLog.getFromStatus() : "";
                if ("applied".equals(prev) || (prev.isEmpty() && !passed.contains("interview-1") && passed.contains("applied"))) {
                    rejectionBreakdown.put("简历初筛未通过", rejectionBreakdown.get("简历初筛未通过") + 1);
                } else if ("interview-1".equals(prev) || (prev.isEmpty() && passed.contains("interview-1") && !passed.contains("interview-2"))) {
                    rejectionBreakdown.put("技术一面未通过", rejectionBreakdown.get("技术一面未通过") + 1);
                } else if ("interview-2".equals(prev) || (prev.isEmpty() && passed.contains("interview-2"))) {
                    rejectionBreakdown.put("复试/终面未通过", rejectionBreakdown.get("复试/终面未通过") + 1);
                } else {
                    rejectionBreakdown.put("意向阶段放弃", rejectionBreakdown.get("意向阶段放弃") + 1);
                }
            }

            long lastTime = 0L;
            if (job.getStatusUpdatedAt() != null) {
                lastTime = job.getStatusUpdatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } else if (job.getAppliedAt() != null) {
                lastTime = job.getAppliedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } else if (job.getCreatedAt() != null) {
                lastTime = job.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }

            long waitingDays = lastTime > 0 ? (now - lastTime) / DAY_MS : 0L;
            if ("applied".equals(status) && waitingDays >= 7) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", job.getId());
                item.put("jobId", job.getJobId());
                item.put("title", job.getTitle());
                item.put("companyName", job.getCompanyName());
                item.put("salary", job.getSalary());
                item.put("platform", job.getPlatform());
                item.put("applyStatus", "applied");
                item.put("daysWaiting", waitingDays);
                item.put("riskLevel", waitingDays >= 14 ? "danger" : "warning");
                item.put("reason", waitingDays >= 14 ? "已投递 " + waitingDays + " 天无进展（疑似停招/HR未活跃）" : "已投递 " + waitingDays + " 天未响应，建议适时跟进");
                stagnantJobs.add(item);
            } else if (("interview-1".equals(status) || "interview-2".equals(status)) && waitingDays >= 5) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", job.getId());
                item.put("jobId", job.getJobId());
                item.put("title", job.getTitle());
                item.put("companyName", job.getCompanyName());
                item.put("salary", job.getSalary());
                item.put("platform", job.getPlatform());
                item.put("applyStatus", status);
                item.put("daysWaiting", waitingDays);
                item.put("riskLevel", waitingDays >= 10 ? "danger" : "warning");
                item.put("reason", "面试流程已停留 " + waitingDays + " 天未更新，建议主动向HR询问反馈");
                stagnantJobs.add(item);
            }

            for (JobStatusLog l : logs) {
                if (l.getCreatedAt() == null) continue;
                LocalDate ld = l.getCreatedAt().toLocalDate();
                LocalDate mon = ld.with(DayOfWeek.MONDAY);
                String k = mon.format(keyFmt);
                WeeklyBucket bucket = weeklyMap.get(k);
                if (bucket != null) {
                    if ("applied".equals(l.getToStatus())) bucket.applied++;
                    if ("interview-1".equals(l.getToStatus()) || "interview-2".equals(l.getToStatus())) bucket.interview++;
                    if ("offer".equals(l.getToStatus())) bucket.offer++;
                }
            }
        }

        Map<String, Object> sAppToI1 = calcAvg(durationsAppliedToI1);
        Map<String, Object> sI1ToI2 = calcAvg(durationsI1ToI2);
        Map<String, Object> sI2ToOff = calcAvg(durationsI2ToOffer);
        Map<String, Object> sFull = calcAvg(durationsFullCycle);

        double interviewRate = cumApplied > 0 ? Math.round(((double) cumInterview1 / cumApplied) * 1000.0) / 10.0 : 0.0;
        double interview2Rate = cumInterview1 > 0 ? Math.round(((double) cumInterview2 / cumInterview1) * 1000.0) / 10.0 : 0.0;
        double overallOfferRate = cumApplied > 0 ? Math.round(((double) cumOffer / cumApplied) * 1000.0) / 10.0 : 0.0;

        List<Map<String, Object>> funnelSteps = new ArrayList<>();
        funnelSteps.add(createFunnelStep("to-apply", "意向储备", cumToApply, currentDist.get("to-apply")));
        funnelSteps.add(createFunnelStep("applied", "已发起投递", cumApplied, currentDist.get("applied")));
        funnelSteps.add(createFunnelStep("interview-1", "初试/一面", cumInterview1, currentDist.get("interview-1")));
        funnelSteps.add(createFunnelStep("interview-2", "复试/二面", cumInterview2, currentDist.get("interview-2")));
        funnelSteps.add(createFunnelStep("offer", "斩获 Offer", cumOffer, currentDist.get("offer")));

        stagnantJobs.sort((a, b) -> Long.compare(((Number) b.get("daysWaiting")).longValue(), ((Number) a.get("daysWaiting")).longValue()));
        if (stagnantJobs.size() > 15) {
            stagnantJobs = new ArrayList<>(stagnantJobs.subList(0, 15));
        }

        Map<String, Object> kpi = new LinkedHashMap<>();
        kpi.put("totalTracked", jobs.size());
        kpi.put("totalApplied", cumApplied);
        kpi.put("totalInterviews", cumInterview1);
        kpi.put("totalOffers", cumOffer);
        kpi.put("totalRejected", cumRejected);
        kpi.put("interviewRate", interviewRate);
        kpi.put("interview2Rate", interview2Rate);
        kpi.put("overallOfferRate", overallOfferRate);
        kpi.put("avgDaysToInterview", sAppToI1.get("avg"));
        kpi.put("avgCycleDays", sFull.get("avg"));

        List<Map<String, Object>> durationsList = new ArrayList<>();
        durationsList.add(createDurationStep("投递 ➔ 首次约面", (Double) sAppToI1.get("avg"), (Integer) sAppToI1.get("count"), "投递后至一面响应周期"));
        durationsList.add(createDurationStep("一面 ➔ 二面推进", (Double) sI1ToI2.get("avg"), (Integer) sI1ToI2.get("count"), "初试至复试流转周期"));
        durationsList.add(createDurationStep("二面 ➔ 终面Offer", (Double) sI2ToOff.get("avg"), (Integer) sI2ToOff.get("count"), "终试至正式发录用周期"));
        durationsList.add(createDurationStep("全流程求职周期", (Double) sFull.get("avg"), (Integer) sFull.get("count"), "从发起投递至最终落地"));

        List<Map<String, Object>> rejectionList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : rejectionBreakdown.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            rejectionList.add(item);
        }

        List<Map<String, Object>> weeklyTimeline = new ArrayList<>();
        for (WeeklyBucket b : weeklyMap.values()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("label", b.label);
            item.put("applied", b.applied);
            item.put("interview", b.interview);
            item.put("offer", b.offer);
            weeklyTimeline.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("kpi", kpi);
        result.put("funnelSteps", funnelSteps);
        result.put("currentDist", currentDist);
        result.put("durations", durationsList);
        result.put("rejectionBreakdown", rejectionList);
        result.put("weeklyTimeline", weeklyTimeline);
        result.put("stagnantJobs", stagnantJobs);

        return result;
    }

    private static class WeeklyBucket {
        String label;
        int applied = 0;
        int interview = 0;
        int offer = 0;
        WeeklyBucket(String label) { this.label = label; }
    }

    private Map<String, Object> createFunnelStep(String key, String name, int count, int current) {
        Map<String, Object> step = new LinkedHashMap<>();
        step.put("key", key);
        step.put("name", name);
        step.put("count", count);
        step.put("current", current);
        return step;
    }

    private Map<String, Object> createDurationStep(String stage, Double avgDays, Integer count, String tip) {
        Map<String, Object> step = new LinkedHashMap<>();
        step.put("stage", stage);
        step.put("avgDays", avgDays != null ? avgDays : 0.0);
        step.put("count", count != null ? count : 0);
        step.put("tip", tip);
        return step;
    }

    private Map<String, Object> calcAvg(List<Double> list) {
        Map<String, Object> res = new HashMap<>();
        if (list == null || list.isEmpty()) {
            res.put("avg", 0.0);
            res.put("count", 0);
            return res;
        }
        double sum = 0.0;
        for (double d : list) sum += d;
        double avg = Math.round((sum / list.size()) * 10.0) / 10.0;
        res.put("avg", avg);
        res.put("count", list.size());
        return res;
    }
}