package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.jobpilot.jobpilotserver.mapper.InterviewMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.mapper.JobStatusLogMapper;
import com.jobpilot.jobpilotserver.model.entity.Interview;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.entity.JobStatusLog;
import com.jobpilot.jobpilotserver.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据大盘统计业务实现类
 * <p>
 * 负责聚合统计全库职位的薪资分布、经验要求分布、核心技能标签词频、平台岗位来源、城市分布、经验薪资矩阵、
 * HR活跃度画像及招聘时序趋势等，并提供求职漏斗转化率、阶段流转周期、滞留超时监控等多时间窗口的分析能力。
 * 全面采用列投影、结构化数值直查与 Caffeine 高性能本地缓存。
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

    private final JobMapper jobMapper;
    private final InterviewMapper interviewMapper;
    private final JobStatusLogMapper jobStatusLogMapper;
    private final ObjectMapper objectMapper;
    private final Cache<String, Map<String, Object>> dashboardStatsCache;
    private final Cache<String, Map<String, Object>> dashboardFunnelCache;

    @Autowired
    public DashboardServiceImpl(JobMapper jobMapper,
                                InterviewMapper interviewMapper,
                                JobStatusLogMapper jobStatusLogMapper,
                                ObjectMapper objectMapper,
                                @Autowired(required = false) @Qualifier("dashboardStatsCache") Cache<String, Map<String, Object>> dashboardStatsCache,
                                @Autowired(required = false) @Qualifier("dashboardFunnelCache") Cache<String, Map<String, Object>> dashboardFunnelCache) {
        this.jobMapper = jobMapper;
        this.interviewMapper = interviewMapper;
        this.jobStatusLogMapper = jobStatusLogMapper;
        this.objectMapper = objectMapper;
        this.dashboardStatsCache = dashboardStatsCache;
        this.dashboardFunnelCache = dashboardFunnelCache;
    }

    public DashboardServiceImpl(JobMapper jobMapper,
                                InterviewMapper interviewMapper,
                                JobStatusLogMapper jobStatusLogMapper,
                                ObjectMapper objectMapper) {
        this(jobMapper, interviewMapper, jobStatusLogMapper, objectMapper, null, null);
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        return getDashboardStats(false);
    }

    @Override
    public Map<String, Object> getDashboardStats(boolean refresh) {
        if (!refresh && dashboardStatsCache != null) {
            Map<String, Object> cached = dashboardStatsCache.getIfPresent("stats");
            if (cached != null) {
                return cached;
            }
        }

        long start = System.currentTimeMillis();
        Map<String, Object> stats = buildDashboardStats();
        log.info("Dashboard stats built in {}ms, total jobs: {}", (System.currentTimeMillis() - start), stats.get("totalJobs"));

        if (dashboardStatsCache != null) {
            dashboardStatsCache.put("stats", stats);
        }
        return stats;
    }

    @Override
    public Map<String, Object> getFunnelAnalytics() {
        return getFunnelAnalytics("all", false);
    }

    @Override
    public Map<String, Object> getFunnelAnalytics(boolean refresh) {
        return getFunnelAnalytics("all", refresh);
    }

    @Override
    public Map<String, Object> getFunnelAnalytics(String timeRange, boolean refresh) {
        String range = (timeRange != null && !timeRange.trim().isEmpty()) ? timeRange.trim().toLowerCase() : "all";
        String cacheKey = "funnel_" + range;
        if (!refresh && dashboardFunnelCache != null) {
            Map<String, Object> cached = dashboardFunnelCache.getIfPresent(cacheKey);
            if (cached != null) {
                return cached;
            }
        }

        long start = System.currentTimeMillis();
        Map<String, Object> funnel = buildFunnelAnalytics(range);
        log.info("Funnel analytics ({}) built in {}ms", range, (System.currentTimeMillis() - start));

        if (dashboardFunnelCache != null) {
            dashboardFunnelCache.put(cacheKey, funnel);
        }
        return funnel;
    }

    @Override
    public void invalidateDashboardCache() {
        if (dashboardStatsCache != null) {
            dashboardStatsCache.invalidateAll();
        }
        if (dashboardFunnelCache != null) {
            dashboardFunnelCache.invalidateAll();
        }
        log.info("Dashboard and funnel caches invalidated.");
    }

    /**
     * 核心实现：轻量列投影查询 + 结构化快速聚合计算数据大盘（含城市排行与经验薪资矩阵）
     */
    private Map<String, Object> buildDashboardStats() {
        // 1. 轻量化列投影：剔除 rawData, rawData2, jobDesc 等大文本字段，极速传输
        QueryWrapper<Job> wrapper = new QueryWrapper<>();
        wrapper.select("title", "salaryAvg", "expMinYears", "expMaxYears", "experience",
                "skills", "tags", "platform", "firstSeen", "lastSeen", "createdAt", "hrActiveLevel", "city");
        List<Job> jobs = jobMapper.selectList(wrapper);
        if (jobs == null) jobs = Collections.emptyList();

        Map<String, Integer> salaryDist = new LinkedHashMap<>();
        salaryDist.put("10k以下", 0);
        salaryDist.put("10-20k", 0);
        salaryDist.put("20-30k", 0);
        salaryDist.put("30k以上", 0);
        salaryDist.put("面议", 0);

        Map<String, Integer> expDist = new LinkedHashMap<>();
        Map<String, Integer> platformDist = new LinkedHashMap<>();
        Map<String, Integer> cityRawMap = new HashMap<>();
        Map<String, Integer> skillsDist = new HashMap<>();

        Map<String, Integer> hrActiveDist = new LinkedHashMap<>();
        hrActiveDist.put("active", 0);
        hrActiveDist.put("moderate", 0);
        hrActiveDist.put("zombie", 0);
        hrActiveDist.put("stale", 0);
        hrActiveDist.put("unknown", 0);

        // 经验-薪资交叉分析容器
        Map<String, List<Double>> expSalaryMap = new LinkedHashMap<>();
        expSalaryMap.put("应届/实习", new ArrayList<>());
        expSalaryMap.put("1-3年", new ArrayList<>());
        expSalaryMap.put("3-5年", new ArrayList<>());
        expSalaryMap.put("5-10年", new ArrayList<>());
        expSalaryMap.put("10年以上", new ArrayList<>());

        long now = System.currentTimeMillis();
        long ms7d = 7L * 24 * 60 * 60 * 1000L;
        long ms30d = 30L * 24 * 60 * 60 * 1000L;
        long ms60d = 60L * 24 * 60 * 60 * 1000L;

        int recent7d = 0;
        int recent30d = 0;
        int stale60d = 0;

        Map<String, Integer> monthlyMap = new TreeMap<>();
        Map<String, WeeklyTrendBucket> weeklyMap = new LinkedHashMap<>();
        Map<String, Integer> dailyMap = new LinkedHashMap<>();

        // 初始化最近 30 天的日期槽位，确保前端折线图走势连续平滑 (MM-dd)
        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();
        for (int i = 29; i >= 0; i--) {
            String dayStr = today.minusDays(i).format(dayFmt);
            dailyMap.put(dayStr, 0);
        }

        // 核心技术关键字词云补充列表
        String[] techKeywords = new String[]{
                "Java", "Spring", "SpringBoot", "SpringCloud", "Vue", "React", "Python", "Go", "Golang",
                "MySQL", "Redis", "微服务", "Kafka", "Docker", "K8s", "Kubernetes", "MyBatis",
                "前端", "后端", "全栈", "架构师", "大模型", "AI", "Node.js", "TypeScript", "分布式"
        };

        for (Job job : jobs) {
            // 1. 平台分布统计
            String plat = (job.getPlatform() != null && !job.getPlatform().trim().isEmpty())
                    ? job.getPlatform().trim() : "未知";
            platformDist.put(plat, platformDist.getOrDefault(plat, 0) + 1);

            // 2. 城市分布统计
            String rawCity = job.getCity();
            if (rawCity != null && !rawCity.trim().isEmpty()) {
                String cleanCity = rawCity.trim();
                cityRawMap.put(cleanCity, cityRawMap.getOrDefault(cleanCity, 0) + 1);
            }

            // 3. 时间计算（优先 firstSeen，降级使用 createdAt）
            LocalDateTime seenDt = job.getFirstSeen() != null ? job.getFirstSeen() : job.getCreatedAt();
            long seenTime = seenDt != null ? seenDt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : now;
            long diff = now - seenTime;

            if (diff <= ms7d) recent7d++;
            if (diff <= ms30d) recent30d++;
            if (diff >= ms60d) stale60d++;

            // 4. HR 活跃度统计（结合最后抓取时间衰减模型）
            String hrLevel = (job.getHrActiveLevel() != null && !job.getHrActiveLevel().trim().isEmpty())
                    ? job.getHrActiveLevel().trim().toLowerCase() : "unknown";
            LocalDateTime syncDt = job.getLastSeen() != null ? job.getLastSeen()
                    : (job.getFirstSeen() != null ? job.getFirstSeen() : job.getCreatedAt());
            long syncTime = syncDt != null ? syncDt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : now;
            long syncDiff = now - syncTime;

            if ("zombie".equals(hrLevel)) {
                hrActiveDist.put("zombie", hrActiveDist.get("zombie") + 1);
            } else if ("active".equals(hrLevel)) {
                if (syncDiff <= ms7d) {
                    hrActiveDist.put("active", hrActiveDist.get("active") + 1);
                } else if (syncDiff <= ms30d) {
                    hrActiveDist.put("moderate", hrActiveDist.get("moderate") + 1);
                } else {
                    hrActiveDist.put("stale", hrActiveDist.get("stale") + 1);
                }
            } else if ("moderate".equals(hrLevel)) {
                if (syncDiff <= ms30d) {
                    hrActiveDist.put("moderate", hrActiveDist.get("moderate") + 1);
                } else {
                    hrActiveDist.put("stale", hrActiveDist.get("stale") + 1);
                }
            } else {
                hrActiveDist.put("unknown", hrActiveDist.get("unknown") + 1);
            }

            // 5. 时序招聘趋势归档
            if (seenDt != null) {
                LocalDate seenDate = seenDt.toLocalDate();

                // 5.1 月度趋势
                String monthKey = seenDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                monthlyMap.put(monthKey, monthlyMap.getOrDefault(monthKey, 0) + 1);

                // 5.2 周度趋势 (计算当周周一与周日)
                LocalDate mon = seenDate.with(DayOfWeek.MONDAY);
                LocalDate sun = mon.plusDays(6);
                String weekKey = mon.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String weekLabel = mon.format(DateTimeFormatter.ofPattern("MM/dd")) + "~" + sun.format(DateTimeFormatter.ofPattern("MM/dd"));
                long monTimestamp = mon.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

                WeeklyTrendBucket bucket = weeklyMap.computeIfAbsent(weekKey, k -> new WeeklyTrendBucket(weekLabel, monTimestamp));
                bucket.count++;

                // 5.3 近 30 天每日走势
                if (diff <= ms30d) {
                    String dayKey = seenDate.format(dayFmt);
                    if (dailyMap.containsKey(dayKey)) {
                        dailyMap.put(dayKey, dailyMap.get(dayKey) + 1);
                    }
                }
            }

            // 6. 薪资区间分布解析 (基于结构化数值 salaryAvg，无需正则与降级)
            Double salaryAvg = job.getSalaryAvg();
            if (salaryAvg != null && salaryAvg > 0) {
                if (salaryAvg < 10.0) salaryDist.put("10k以下", salaryDist.get("10k以下") + 1);
                else if (salaryAvg <= 20.0) salaryDist.put("10-20k", salaryDist.get("10-20k") + 1);
                else if (salaryAvg <= 30.0) salaryDist.put("20-30k", salaryDist.get("20-30k") + 1);
                else salaryDist.put("30k以上", salaryDist.get("30k以上") + 1);
            } else {
                salaryDist.put("面议", salaryDist.get("面议") + 1);
            }

            // 7. 经验要求分布解析 (基于结构化工作年限 expMinYears)
            String exp = "其他/不限";
            Integer minYears = job.getExpMinYears();
            Integer maxYears = job.getExpMaxYears();
            if (minYears != null) {
                if (minYears == 0 && (maxYears == null || maxYears == 0)) {
                    exp = "应届/实习";
                } else if (minYears >= 10) {
                    exp = "10年以上";
                } else if (minYears >= 5) {
                    exp = "5-10年";
                } else if (minYears >= 3) {
                    exp = "3-5年";
                } else if (minYears >= 1) {
                    exp = "1-3年";
                }
            } else if (job.getExperience() != null) {
                String eStr = job.getExperience();
                if (eStr.contains("应届") || eStr.contains("实习") || eStr.contains("在校")) exp = "应届/实习";
                else if (eStr.contains("3-5") || eStr.contains("3年") || eStr.contains("4年")) exp = "3-5年";
                else if (eStr.contains("1-3") || eStr.contains("1年") || eStr.contains("2年")) exp = "1-3年";
                else if (eStr.contains("5-10") || eStr.contains("5年") || eStr.contains("6年") || eStr.contains("7年") || eStr.contains("8年")) exp = "5-10年";
                else if (eStr.contains("10")) exp = "10年以上";
            }
            expDist.put(exp, expDist.getOrDefault(exp, 0) + 1);

            // 7.1 经验-薪资交叉分析累加
            if (salaryAvg != null && salaryAvg > 0 && expSalaryMap.containsKey(exp)) {
                expSalaryMap.get(exp).add(salaryAvg);
            }

            // 8. 技能关键词提取 (直接利用结构化 skills 数组)
            if (job.getSkills() != null && !job.getSkills().trim().isEmpty()) {
                try {
                    List<String> parsedSkills = objectMapper.readValue(job.getSkills(), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
                    if (parsedSkills != null) {
                        for (String tag : parsedSkills) {
                            if (tag != null && !tag.trim().isEmpty()) {
                                String clean = tag.trim();
                                skillsDist.put(clean, skillsDist.getOrDefault(clean, 0) + 1);
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }

            // 补充自定义标签
            if (job.getTags() != null && !job.getTags().trim().isEmpty()) {
                try {
                    List<String> parsedTags = objectMapper.readValue(job.getTags(), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
                    if (parsedTags != null) {
                        for (String t : parsedTags) {
                            if (t != null && !t.trim().isEmpty() && !"学历不符".equals(t.trim()) && !"经验不符".equals(t.trim())) {
                                String clean = t.trim();
                                skillsDist.put(clean, skillsDist.getOrDefault(clean, 0) + 1);
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }

            // 从职位标题提取核心高频技术关键词作为词云补充
            if (job.getTitle() != null) {
                String lowerTitle = job.getTitle().toLowerCase();
                for (String kw : techKeywords) {
                    if (lowerTitle.contains(kw.toLowerCase())) {
                        skillsDist.put(kw, skillsDist.getOrDefault(kw, 0) + 1);
                    }
                }
            }
        }

        // 9. 格式化城市前 10 排行榜与其他
        List<Map.Entry<String, Integer>> sortedCities = cityRawMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toList());

        Map<String, Integer> cityDist = new LinkedHashMap<>();
        int topN = Math.min(10, sortedCities.size());
        int otherCount = 0;
        for (int i = 0; i < topN; i++) {
            cityDist.put(sortedCities.get(i).getKey(), sortedCities.get(i).getValue());
        }
        for (int i = topN; i < sortedCities.size(); i++) {
            otherCount += sortedCities.get(i).getValue();
        }
        if (otherCount > 0) {
            cityDist.put("其他城市", otherCount);
        }

        // 10. 格式化经验-平均薪资矩阵
        List<Map<String, Object>> expSalaryMatrix = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : expSalaryMap.entrySet()) {
            String expKey = entry.getKey();
            List<Double> list = entry.getValue();
            double avg = 0.0;
            if (!list.isEmpty()) {
                double sum = 0.0;
                for (Double d : list) sum += d;
                avg = Math.round((sum / list.size()) * 10.0) / 10.0;
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("experience", expKey);
            item.put("avgSalary", avg);
            item.put("count", list.size());
            expSalaryMatrix.add(item);
        }

        // 11. 格式化趋势数据
        List<Map<String, Object>> monthlyTrend = new ArrayList<>();
        for (Map.Entry<String, Integer> e : monthlyMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("label", e.getKey());
            item.put("count", e.getValue());
            monthlyTrend.add(item);
        }

        List<WeeklyTrendBucket> sortedWeeks = new ArrayList<>(weeklyMap.values());
        sortedWeeks.sort(Comparator.comparingLong(w -> w.timestamp));
        if (sortedWeeks.size() > 12) {
            sortedWeeks = sortedWeeks.subList(sortedWeeks.size() - 12, sortedWeeks.size());
        }
        List<Map<String, Object>> weeklyTrend = new ArrayList<>();
        for (WeeklyTrendBucket w : sortedWeeks) {
            Map<String, Object> item = new HashMap<>();
            item.put("label", w.label);
            item.put("count", w.count);
            weeklyTrend.add(item);
        }

        List<Map<String, Object>> dailyTrend = new ArrayList<>();
        for (Map.Entry<String, Integer> e : dailyMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("label", e.getKey());
            item.put("count", e.getValue());
            dailyTrend.add(item);
        }

        Map<String, Object> trends = new LinkedHashMap<>();
        trends.put("monthly", monthlyTrend);
        trends.put("weekly", weeklyTrend);
        trends.put("daily", dailyTrend);

        // 12. 宏观核心 KPI 计算
        Map<String, Object> kpi = new LinkedHashMap<>();
        kpi.put("totalJobs", jobs.size());
        kpi.put("recent7d", recent7d);
        kpi.put("recent30d", recent30d);
        kpi.put("stale60d", stale60d);
        String newJobRatio = jobs.size() > 0 ? String.format(Locale.ROOT, "%.1f", ((double) recent7d / jobs.size()) * 100) : "0";
        String zombieRatio = jobs.size() > 0 ? String.format(Locale.ROOT, "%.1f", ((double) hrActiveDist.get("zombie") / jobs.size()) * 100) : "0";
        kpi.put("newJobRatio", newJobRatio);
        kpi.put("zombieRatio", zombieRatio);

        // 13. 组装并返回标准数据结构
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("salary", salaryDist);
        data.put("experience", expDist);
        data.put("skills", skillsDist);
        data.put("platform", platformDist);
        data.put("city", cityDist);
        data.put("expSalaryMatrix", expSalaryMatrix);
        data.put("totalJobs", jobs.size());
        data.put("hrActive", hrActiveDist);
        data.put("kpi", kpi);
        data.put("trends", trends);

        return data;
    }

    private static class WeeklyTrendBucket {
        String label;
        long timestamp;
        int count = 0;
        WeeklyTrendBucket(String label, long timestamp) {
            this.label = label;
            this.timestamp = timestamp;
        }
    }

    /**
     * 求职转化漏斗业务实现：支持多时间窗口筛选 + 轻量列投影 + 阶段耗时与滞留超时监控
     *
     * @param timeRange 时间范围：all (全部), 30d (近30天), 90d (近90天), 180d (近半年)
     */
    private Map<String, Object> buildFunnelAnalytics(String timeRange) {
        long now = System.currentTimeMillis();
        final long DAY_MS = 24 * 60 * 60 * 1000L;

        // 1. 获取所有相关的 Job (列投影优化：剔除 rawData / rawData2 等大字段)
        QueryWrapper<Job> candidateWrapper = new QueryWrapper<>();
        candidateWrapper.select("id", "jobId", "title", "companyName", "salary", "platform",
                        "applyStatus", "isFavorited", "appliedAt", "statusUpdatedAt", "createdAt")
                .and(w -> w.isNotNull("applyStatus").ne("applyStatus", "none").or().eq("isFavorited", true));

        int days = 0;
        if ("30d".equalsIgnoreCase(timeRange)) days = 30;
        else if ("90d".equalsIgnoreCase(timeRange)) days = 90;
        else if ("180d".equalsIgnoreCase(timeRange)) days = 180;

        if (days > 0) {
            LocalDateTime cutoffDt = LocalDate.now().minusDays(days).atStartOfDay();
            candidateWrapper.and(w -> w.ge("appliedAt", cutoffDt)
                    .or().ge("statusUpdatedAt", cutoffDt)
                    .or().ge("createdAt", cutoffDt));
        }

        List<Job> candidateJobs = jobMapper.selectList(candidateWrapper);
        if (candidateJobs == null) candidateJobs = new ArrayList<>();

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

        // 如果有些职位状态为 none 且未收藏，但有 interview 或 statusLog，且在时间范围内，也加入统计池
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
            QueryWrapper<Job> addWrapper = new QueryWrapper<>();
            addWrapper.select("id", "jobId", "title", "companyName", "salary", "platform",
                            "applyStatus", "isFavorited", "appliedAt", "statusUpdatedAt", "createdAt")
                    .in("id", missingJobIds);
            if (days > 0) {
                LocalDateTime cutoffDt = LocalDate.now().minusDays(days).atStartOfDay();
                addWrapper.and(w -> w.ge("appliedAt", cutoffDt)
                        .or().ge("statusUpdatedAt", cutoffDt)
                        .or().ge("createdAt", cutoffDt));
            }
            List<Job> additionalJobs = jobMapper.selectList(addWrapper);
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
        result.put("timeRange", timeRange);

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