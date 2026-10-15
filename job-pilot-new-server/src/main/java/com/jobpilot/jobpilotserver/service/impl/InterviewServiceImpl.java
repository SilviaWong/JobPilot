package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.common.exception.BusinessException;
import com.jobpilot.jobpilotserver.common.result.ResultCode;
import com.jobpilot.jobpilotserver.mapper.*;
import com.jobpilot.jobpilotserver.model.entity.*;
import com.jobpilot.jobpilotserver.service.InterviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 面试管理、日历导出与复盘转题库业务实现类
 */
@Service
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, Interview> implements InterviewService {

    private static final Logger log = LoggerFactory.getLogger(InterviewServiceImpl.class);

    private final InterviewMapper interviewMapper;
    private final JobMapper jobMapper;
    private final JobStatusLogMapper jobStatusLogMapper;
    private final StandardQuestionMapper standardQuestionMapper;
    private final QuestionVariantMapper questionVariantMapper;
    private final ObjectMapper objectMapper;

    public InterviewServiceImpl(InterviewMapper interviewMapper,
                                JobMapper jobMapper,
                                JobStatusLogMapper jobStatusLogMapper,
                                StandardQuestionMapper standardQuestionMapper,
                                QuestionVariantMapper questionVariantMapper,
                                ObjectMapper objectMapper) {
        this.interviewMapper = interviewMapper;
        this.jobMapper = jobMapper;
        this.jobStatusLogMapper = jobStatusLogMapper;
        this.standardQuestionMapper = standardQuestionMapper;
        this.questionVariantMapper = questionVariantMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, Object> getInterviews(String jobId, String status, String search, int page, int pageSize) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);

        // 统计指标
        LambdaQueryWrapper<Interview> baseQuery = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(jobId)) {
            baseQuery.eq(Interview::getJobId, jobId);
        }
        Long total = interviewMapper.selectCount(baseQuery);

        LambdaQueryWrapper<Interview> upcomingQuery = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(jobId)) upcomingQuery.eq(Interview::getJobId, jobId);
        upcomingQuery.ge(Interview::getTime, startOfToday);
        Long upcomingCount = interviewMapper.selectCount(upcomingQuery);

        LambdaQueryWrapper<Interview> todayQuery = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(jobId)) todayQuery.eq(Interview::getJobId, jobId);
        todayQuery.ge(Interview::getTime, startOfToday).le(Interview::getTime, endOfToday);
        Long todayCount = interviewMapper.selectCount(todayQuery);

        LambdaQueryWrapper<Interview> debriefedQuery = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(jobId)) debriefedQuery.eq(Interview::getJobId, jobId);
        debriefedQuery.isNotNull(Interview::getDebrief).ne(Interview::getDebrief, "");
        Long debriefedCount = interviewMapper.selectCount(debriefedQuery);

        // 构建筛选列表查询
        LambdaQueryWrapper<Interview> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(jobId)) {
            queryWrapper.eq(Interview::getJobId, jobId);
        }

        if ("upcoming".equalsIgnoreCase(status)) {
            queryWrapper.ge(Interview::getTime, startOfToday);
        } else if ("today".equalsIgnoreCase(status)) {
            queryWrapper.ge(Interview::getTime, startOfToday).le(Interview::getTime, endOfToday);
        } else if ("past".equalsIgnoreCase(status)) {
            queryWrapper.lt(Interview::getTime, startOfToday);
        }

        // 搜索关键词 (轮次、形式、备注、复盘、或职位名称/公司名称)
        if (StringUtils.hasText(search)) {
            String s = search.trim();
            // 先搜匹配的职位 ID
            List<String> matchedJobIds = jobMapper.selectList(
                    new LambdaQueryWrapper<Job>()
                            .like(Job::getTitle, s)
                            .or().like(Job::getCompanyName, s)
                            .or().like(Job::getCompanyFullName, s)
            ).stream().map(Job::getId).collect(Collectors.toList());

            queryWrapper.and(w -> {
                w.like(Interview::getRound, s)
                 .or().like(Interview::getType, s)
                 .or().like(Interview::getNote, s)
                 .or().like(Interview::getDebrief, s);
                if (!matchedJobIds.isEmpty()) {
                    w.or().in(Interview::getJobId, matchedJobIds);
                }
            });
        }

        // 排序逻辑：待参加按时间正序，历史按时间倒序
        if ("upcoming".equalsIgnoreCase(status) || "today".equalsIgnoreCase(status)) {
            queryWrapper.orderByAsc(Interview::getTime);
        } else {
            queryWrapper.orderByDesc(Interview::getTime);
        }

        Page<Interview> pageParam = new Page<>(page, pageSize);
        Page<Interview> resultPage = interviewMapper.selectPage(pageParam, queryWrapper);

        List<Interview> records = resultPage.getRecords();

        // 关联 Job 职位信息
        if (!records.isEmpty()) {
            List<String> jIds = records.stream().map(Interview::getJobId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
            if (!jIds.isEmpty()) {
                Map<String, Job> jobMap = jobMapper.selectBatchIds(jIds).stream()
                        .collect(Collectors.toMap(Job::getId, j -> j, (a, b) -> a));
                for (Interview record : records) {
                    Job j = jobMap.get(record.getJobId());
                    if (j != null) {
                        record.setJob(j);
                        record.setJobTitle(j.getTitle());
                        record.setCompanyName(j.getCompanyName());
                        record.setStatus(j.getApplyStatus());
                    }
                }
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("data", records);
        res.put("stats", Map.of(
                "total", total,
                "upcoming", upcomingCount,
                "today", todayCount,
                "debriefed", debriefedCount
        ));
        res.put("pagination", Map.of(
                "page", page,
                "pageSize", pageSize,
                "total", resultPage.getTotal()
        ));
        return res;
    }

    @Override
    public List<Interview> getInterviews(String keyword, String status) {
        Map<String, Object> map = getInterviews(null, status, keyword, 1, 100);
        return (List<Interview>) map.get("data");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Interview createInterview(Interview interview) {
        if (!StringUtils.hasText(interview.getId())) {
            interview.setId(UUID.randomUUID().toString());
        }
        if (interview.getTime() == null) {
            interview.setTime(LocalDateTime.now());
        }
        interview.setCreatedAt(LocalDateTime.now());
        interview.setUpdatedAt(LocalDateTime.now());

        interviewMapper.insert(interview);

        // 同步推进关联职位的投递状态至 interview-1 或 interview-2
        if (StringUtils.hasText(interview.getJobId())) {
            Job job = jobMapper.selectById(interview.getJobId());
            if (job != null) {
                Long count = interviewMapper.selectCount(
                        new LambdaQueryWrapper<Interview>().eq(Interview::getJobId, job.getId()));
                String targetStatus = count >= 2 ? "interview-2" : "interview-1";
                String prevStatus = job.getApplyStatus() != null ? job.getApplyStatus() : (job.getStatus() != null ? job.getStatus() : "normal");

                if (!targetStatus.equals(job.getApplyStatus())) {
                    job.setApplyStatus(targetStatus);
                    job.setStatus(targetStatus);
                    job.setStatusUpdatedAt(LocalDateTime.now());
                    job.setUpdatedAt(LocalDateTime.now());
                    jobMapper.updateById(job);

                    JobStatusLog logEntry = new JobStatusLog();
                    logEntry.setJobRecordId(job.getId());
                    logEntry.setFromStatus(prevStatus);
                    logEntry.setToStatus(targetStatus);
                    logEntry.setNote("新增面试安排: " + interview.getRound() + " (" + interview.getType() + ")");
                    logEntry.setCreatedAt(LocalDateTime.now());
                    jobStatusLogMapper.insert(logEntry);
                }
            }
        }

        return interview;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Interview updateInterview(String id, Interview interview) {
        Interview existing = interviewMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "面试记录不存在");
        }

        if (interview.getTime() != null) existing.setTime(interview.getTime());
        if (StringUtils.hasText(interview.getType())) existing.setType(interview.getType());
        if (StringUtils.hasText(interview.getRound())) existing.setRound(interview.getRound());
        if (interview.getNote() != null) existing.setNote(interview.getNote());
        if (interview.getDebrief() != null) existing.setDebrief(interview.getDebrief());

        existing.setUpdatedAt(LocalDateTime.now());
        interviewMapper.updateById(existing);
        return existing;
    }

    @Override
    public boolean saveOrUpdateInterview(Interview interview) {
        if (!StringUtils.hasText(interview.getId()) || interviewMapper.selectById(interview.getId()) == null) {
            createInterview(interview);
        } else {
            updateInterview(interview.getId(), interview);
        }
        return true;
    }

    @Override
    public boolean updateStatus(String id, String status) {
        Interview interview = interviewMapper.selectById(id);
        if (interview != null && StringUtils.hasText(interview.getJobId())) {
            Job job = jobMapper.selectById(interview.getJobId());
            if (job != null) {
                job.setStatus(status);
                job.setApplyStatus(status);
                job.setStatusUpdatedAt(LocalDateTime.now());
                job.setUpdatedAt(LocalDateTime.now());
                jobMapper.updateById(job);
            }
        }
        return true;
    }

    @Override
    public boolean deleteInterview(String id) {
        return interviewMapper.deleteById(id) > 0;
    }

    private String formatIcsDate(LocalDateTime dateTime) {
        if (dateTime == null) dateTime = LocalDateTime.now();
        ZonedDateTime utc = dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC);
        return utc.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
    }

    private String escapeIcsText(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace(";", "\\;")
                  .replace(",", "\\,")
                  .replace("\r", "")
                  .replace("\n", "\\n");
    }

    @Override
    public String generateIcs(String id) {
        Interview interview = interviewMapper.selectById(id);
        if (interview == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "面试记录不存在");
        }

        Job job = jobMapper.selectById(interview.getJobId());
        String company = (job != null && StringUtils.hasText(job.getCompanyFullName()))
                ? job.getCompanyFullName()
                : (job != null && StringUtils.hasText(job.getCompanyName()) ? job.getCompanyName() : "目标企业");
        String title = job != null && StringUtils.hasText(job.getTitle()) ? job.getTitle() : "目标岗位";

        LocalDateTime startTime = interview.getTime() != null ? interview.getTime() : LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        LocalDateTime now = LocalDateTime.now();

        String summary = "【求职面试】" + company + " · " + interview.getRound() + " (" + interview.getType() + ")";
        List<String> descLines = new ArrayList<>();
        descLines.add("岗位：" + title);
        descLines.add("公司：" + company);
        descLines.add("轮次：" + interview.getRound());
        descLines.add("形式：" + interview.getType());
        descLines.add("备注/会议信息：" + (StringUtils.hasText(interview.getNote()) ? interview.getNote() : "无"));
        if (StringUtils.hasText(interview.getDebrief())) {
            descLines.add("复盘要点：" + interview.getDebrief());
        }
        String description = String.join("\n", descLines);
        String location = "现场".equals(interview.getType())
                ? (StringUtils.hasText(interview.getNote()) ? interview.getNote() : (job != null && StringUtils.hasText(job.getLocation()) ? job.getLocation() : ""))
                : (StringUtils.hasText(interview.getNote()) ? interview.getNote() : "线上视频会议");

        List<String> lines = List.of(
                "BEGIN:VCALENDAR",
                "VERSION:2.0",
                "PRODID:-//Job Pilot Server//Interview Schedule//CN",
                "CALSCALE:GREGORIAN",
                "METHOD:PUBLISH",
                "BEGIN:VEVENT",
                "UID:interview-" + interview.getId() + "@jobpilot.local",
                "DTSTAMP:" + formatIcsDate(now),
                "DTSTART:" + formatIcsDate(startTime),
                "DTEND:" + formatIcsDate(endTime),
                "SUMMARY:" + escapeIcsText(summary),
                "DESCRIPTION:" + escapeIcsText(description),
                "LOCATION:" + escapeIcsText(location),
                "STATUS:CONFIRMED",
                "BEGIN:VALARM",
                "TRIGGER:-PT30M",
                "ACTION:DISPLAY",
                "DESCRIPTION:面试开始前 30 分钟提醒",
                "END:VALARM",
                "END:VEVENT",
                "END:VCALENDAR"
        );

        return String.join("\r\n", lines);
    }

    @Override
    public String exportAllIcs() {
        LocalDateTime date7dAgo = LocalDateTime.now().minusDays(7);
        List<Interview> list = interviewMapper.selectList(
                new LambdaQueryWrapper<Interview>()
                        .ge(Interview::getTime, date7dAgo)
                        .orderByAsc(Interview::getTime)
        );

        List<String> lines = new ArrayList<>();
        lines.add("BEGIN:VCALENDAR");
        lines.add("VERSION:2.0");
        lines.add("PRODID:-//Job Pilot Server//All Interviews Schedule//CN");
        lines.add("CALSCALE:GREGORIAN");
        lines.add("METHOD:PUBLISH");

        LocalDateTime now = LocalDateTime.now();

        for (Interview interview : list) {
            Job job = jobMapper.selectById(interview.getJobId());
            String company = (job != null && StringUtils.hasText(job.getCompanyFullName()))
                    ? job.getCompanyFullName()
                    : (job != null && StringUtils.hasText(job.getCompanyName()) ? job.getCompanyName() : "目标企业");
            String title = job != null && StringUtils.hasText(job.getTitle()) ? job.getTitle() : "目标岗位";

            LocalDateTime startTime = interview.getTime() != null ? interview.getTime() : LocalDateTime.now();
            LocalDateTime endTime = startTime.plusHours(1);

            String summary = "【求职面试】" + company + " · " + interview.getRound() + " (" + interview.getType() + ")";
            List<String> descLines = new ArrayList<>();
            descLines.add("岗位：" + title);
            descLines.add("公司：" + company);
            descLines.add("轮次：" + interview.getRound());
            descLines.add("形式：" + interview.getType());
            descLines.add("会议信息/地点：" + (StringUtils.hasText(interview.getNote()) ? interview.getNote() : "无"));
            if (StringUtils.hasText(interview.getDebrief())) {
                descLines.add("复盘：" + interview.getDebrief());
            }

            String location = "现场".equals(interview.getType())
                    ? (StringUtils.hasText(interview.getNote()) ? interview.getNote() : (job != null && StringUtils.hasText(job.getLocation()) ? job.getLocation() : ""))
                    : (StringUtils.hasText(interview.getNote()) ? interview.getNote() : "线上视频会议");

            lines.add("BEGIN:VEVENT");
            lines.add("UID:interview-" + interview.getId() + "@jobpilot.local");
            lines.add("DTSTAMP:" + formatIcsDate(now));
            lines.add("DTSTART:" + formatIcsDate(startTime));
            lines.add("DTEND:" + formatIcsDate(endTime));
            lines.add("SUMMARY:" + escapeIcsText(summary));
            lines.add("DESCRIPTION:" + escapeIcsText(String.join("\n", descLines)));
            lines.add("LOCATION:" + escapeIcsText(location));
            lines.add("STATUS:CONFIRMED");
            lines.add("BEGIN:VALARM");
            lines.add("TRIGGER:-PT30M");
            lines.add("ACTION:DISPLAY");
            lines.add("DESCRIPTION:面试开始前 30 分钟提醒");
            lines.add("END:VALARM");
            lines.add("END:VEVENT");
        }

        lines.add("END:VCALENDAR");
        return String.join("\r\n", lines);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncToQuestions(String id, Map<String, Object> body) {
        Interview interview = interviewMapper.selectById(id);
        if (interview == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "面试记录不存在");
        }

        Job job = jobMapper.selectById(interview.getJobId());
        String company = (job != null && StringUtils.hasText(job.getCompanyFullName()))
                ? job.getCompanyFullName()
                : (job != null && StringUtils.hasText(job.getCompanyName()) ? job.getCompanyName() : "面试真题");
        String round = StringUtils.hasText(interview.getRound()) ? interview.getRound() : "实战面试";

        List<Map<String, Object>> questionsList = new ArrayList<>();
        if (body.get("questions") instanceof List) {
            questionsList = (List<Map<String, Object>>) body.get("questions");
        } else if (body.containsKey("title")) {
            questionsList.add(body);
        }

        if (questionsList.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "请提供至少一道待同步的面试真题");
        }

        int savedCount = 0;
        for (Map<String, Object> q : questionsList) {
            String title = (String) q.get("title");
            if (!StringUtils.hasText(title)) continue;
            title = title.trim();

            String domain = q.get("domain") != null ? (String) q.get("domain") : "面试实战";
            String categoryTags = q.get("categoryTags") != null ? (String) q.get("categoryTags") : (company + "/" + round);
            String corePoints = (String) q.get("corePoints");
            String answerKey = (String) q.get("answerKey");

            StandardQuestion existing = standardQuestionMapper.selectOne(
                    new LambdaQueryWrapper<StandardQuestion>().eq(StandardQuestion::getTitle, title).last("LIMIT 1"));

            if (existing != null) {
                QuestionVariant variant = new QuestionVariant();
                variant.setStandardQuestionId(existing.getId());
                variant.setTitle(company + "（" + round + "）：" + title);
                variant.setCreatedAt(LocalDateTime.now());
                variant.setUpdatedAt(LocalDateTime.now());
                questionVariantMapper.insert(variant);
            } else {
                StandardQuestion created = new StandardQuestion();
                created.setTitle(title);
                created.setDomain(domain);
                created.setCategoryTags(categoryTags);
                created.setCorePoints(corePoints);
                created.setAnswerKey(answerKey);
                try {
                    created.setTags(objectMapper.writeValueAsString(List.of(company, round, "实战真题")));
                } catch (Exception e) {
                    created.setTags("[\"" + company + "\",\"" + round + "\",\"实战真题\"]");
                }
                created.setCreatedAt(LocalDateTime.now());
                created.setUpdatedAt(LocalDateTime.now());
                standardQuestionMapper.insert(created);

                QuestionVariant variant = new QuestionVariant();
                variant.setStandardQuestionId(created.getId());
                variant.setTitle(company + "（" + round + "）：" + title);
                variant.setCreatedAt(LocalDateTime.now());
                variant.setUpdatedAt(LocalDateTime.now());
                questionVariantMapper.insert(variant);
            }
            savedCount++;
        }

        Map<String, Object> res = new HashMap<>();
        res.put("count", savedCount);
        res.put("message", "成功同步 " + savedCount + " 道真题到标准题库");
        return res;
    }
}
