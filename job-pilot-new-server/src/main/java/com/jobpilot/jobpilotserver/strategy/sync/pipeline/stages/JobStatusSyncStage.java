package com.jobpilot.jobpilotserver.strategy.sync.pipeline.stages;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncContext;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncStage;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 阶段 10：职位收藏与自定义业务状态批量同步
 */
@Component
public class JobStatusSyncStage implements SyncStage {

    private final JobMapper jobMapper;

    public JobStatusSyncStage(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public void handle(Map<String, Object> payload, SyncContext context) {
        List<String> favList = payload.get("favorited_jobs") instanceof List<?> fList ? (List<String>) fList : Collections.emptyList();
        Map<String, String> statusMap = payload.get("job_statuses") instanceof Map<?, ?> sm ? (Map<String, String>) sm : Collections.emptyMap();

        // 1. 同步收藏标记
        for (String favId : favList) {
            Job job = jobMapper.selectOne(new LambdaQueryWrapper<Job>().eq(Job::getJobId, favId));
            if (job != null) {
                job.setIsFavorited(true);
                jobMapper.updateById(job);
            }
        }

        // 2. 同步自定义状态
        for (Map.Entry<String, String> entry : statusMap.entrySet()) {
            Job job = jobMapper.selectOne(new LambdaQueryWrapper<Job>().eq(Job::getJobId, entry.getKey()));
            if (job != null) {
                job.setStatus(entry.getValue());
                jobMapper.updateById(job);
            }
        }
    }
}
