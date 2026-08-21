package com.jobpilot.jobpilotserver.strategy.sync.pipeline;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全量同步管道执行上下文 (SyncContext)
 * 封装各阶段的中间状态、活跃抓取 JobId 集合以及执行结果计数器
 */
public class SyncContext {
    private final long startTime = System.currentTimeMillis();
    private final Set<String> activeScrapedJobIds = ConcurrentHashMap.newKeySet();

    private int blacklistedCount = 0;
    private int questionsCount = 0;
    private boolean aiSettingsUpdated = false;
    private int jobUpdatesCount = 0;
    private int interviewsCount = 0;
    private int companiesCount = 0;
    private int aiResultsCount = 0;

    public void addActiveJobId(String jobId) {
        if (jobId != null && !jobId.isBlank()) {
            activeScrapedJobIds.add(jobId);
        }
    }

    public void addActiveJobIds(Collection<String> jobIds) {
        if (jobIds != null) {
            activeScrapedJobIds.addAll(jobIds);
        }
    }

    public Map<String, Object> buildResponse() {
        long durationMs = System.currentTimeMillis() - startTime;

        Map<String, Object> results = new HashMap<>();
        results.put("blacklisted", blacklistedCount);
        results.put("questions", questionsCount);
        results.put("aiSettingsUpdated", aiSettingsUpdated);
        results.put("jobUpdates", jobUpdatesCount);
        results.put("interviews", interviewsCount);
        results.put("companies", companiesCount);
        results.put("aiResults", aiResultsCount);
        results.put("durationMs", durationMs);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "全量配置同步完成");
        res.put("results", results);
        return res;
    }

    public long getStartTime() {
        return startTime;
    }

    public Set<String> getActiveScrapedJobIds() {
        return activeScrapedJobIds;
    }

    public int getBlacklistedCount() {
        return blacklistedCount;
    }

    public void setBlacklistedCount(int blacklistedCount) {
        this.blacklistedCount = blacklistedCount;
    }

    public int getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(int questionsCount) {
        this.questionsCount = questionsCount;
    }

    public boolean isAiSettingsUpdated() {
        return aiSettingsUpdated;
    }

    public void setAiSettingsUpdated(boolean aiSettingsUpdated) {
        this.aiSettingsUpdated = aiSettingsUpdated;
    }

    public int getJobUpdatesCount() {
        return jobUpdatesCount;
    }

    public void setJobUpdatesCount(int jobUpdatesCount) {
        this.jobUpdatesCount = jobUpdatesCount;
    }

    public int getInterviewsCount() {
        return interviewsCount;
    }

    public void setInterviewsCount(int interviewsCount) {
        this.interviewsCount = interviewsCount;
    }

    public int getCompaniesCount() {
        return companiesCount;
    }

    public void setCompaniesCount(int companiesCount) {
        this.companiesCount = companiesCount;
    }

    public int getAiResultsCount() {
        return aiResultsCount;
    }

    public void setAiResultsCount(int aiResultsCount) {
        this.aiResultsCount = aiResultsCount;
    }
}
