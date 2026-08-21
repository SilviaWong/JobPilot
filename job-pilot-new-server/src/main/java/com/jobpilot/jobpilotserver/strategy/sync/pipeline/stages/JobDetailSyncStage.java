package com.jobpilot.jobpilotserver.strategy.sync.pipeline.stages;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.JobDetailMapper;
import com.jobpilot.jobpilotserver.model.entity.JobDetail;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncContext;
import com.jobpilot.jobpilotserver.strategy.sync.pipeline.SyncStage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 阶段 7：单个职位深度详情富化数据同步 (boss_single_details -> JobDetail)
 */
@Component
public class JobDetailSyncStage implements SyncStage {

    private final JobDetailMapper jobDetailMapper;
    private final ObjectMapper objectMapper;

    public JobDetailSyncStage(JobDetailMapper jobDetailMapper,
                              ObjectMapper objectMapper) {
        this.jobDetailMapper = jobDetailMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public int getOrder() {
        return 70;
    }

    @Override
    public void handle(Map<String, Object> payload, SyncContext context) {
        if (payload.get("boss_single_details") instanceof Map<?, ?> bsdMap) {
            LocalDateTime now = LocalDateTime.now();

            for (Map.Entry<?, ?> entry : bsdMap.entrySet()) {
                String jobId = String.valueOf(entry.getKey());
                if (entry.getValue() instanceof Map<?, ?> detailObj) {
                    JobDetail detail = jobDetailMapper.selectOne(new LambdaQueryWrapper<JobDetail>()
                            .eq(JobDetail::getJobId, jobId)
                            .eq(JobDetail::getPlatform, "Boss直聘"));
                    boolean isNew = (detail == null);
                    if (isNew) {
                        detail = new JobDetail();
                        detail.setId(UUID.randomUUID().toString());
                        detail.setJobId(jobId);
                        detail.setPlatform("Boss直聘");
                        detail.setCreatedAt(now);
                    }
                    try { detail.setRawData(objectMapper.writeValueAsString(detailObj)); } catch (Exception ignored) {}
                    detail.setUpdatedAt(now);
                    if (isNew) jobDetailMapper.insert(detail);
                    else jobDetailMapper.updateById(detail);
                }
            }
        }
    }
}
