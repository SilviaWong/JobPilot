package com.jobpilot.jobpilotserver.strategy.sync.pipeline.stages;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncContext;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncStage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 阶段 8：历史爬虫职位智能过期检测（不在本次活跃列表中的 active 职位自动置为 expired）
 */
@Component
public class ExpiredJobDetectionSyncStage implements SyncStage {

    private final JobMapper jobMapper;

    public ExpiredJobDetectionSyncStage(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    @Override
    public int getOrder() {
        return 80;
    }

    @Override
    public void handle(Map<String, Object> payload, SyncContext context) {
        Set<String> activeJobIds = context.getActiveScrapedJobIds();
        if (!activeJobIds.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            List<Job> allActiveScrapedJobs = jobMapper.selectList(new LambdaQueryWrapper<Job>()
                    .eq(Job::getStatus, "active")
                    .isNotNull(Job::getDataSource));

            for (Job j : allActiveScrapedJobs) {
                if (!activeJobIds.contains(j.getJobId())) {
                    j.setStatus("expired");
                    j.setUpdatedAt(now);
                    jobMapper.updateById(j);
                }
            }
        }
    }
}
