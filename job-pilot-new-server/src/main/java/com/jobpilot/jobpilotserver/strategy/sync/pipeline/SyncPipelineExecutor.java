package com.jobpilot.jobpilotserver.strategy.sync.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 全量同步管道执行器 (Pipeline Executor)
 */
@Component
public class SyncPipelineExecutor {

    private static final Logger log = LoggerFactory.getLogger(SyncPipelineExecutor.class);

    private final List<SyncStage> stages;

    public SyncPipelineExecutor(List<SyncStage> stages) {
        // 按 getOrder() 升序严格排序
        this.stages = stages.stream()
                .sorted(Comparator.comparingInt(SyncStage::getOrder))
                .toList();
        log.info("Initialized SyncPipelineExecutor with {} stages.", this.stages.size());
    }

    /**
     * 按顺序依次调度各个同步阶段
     *
     * @param payload 客户端上传的全量数据
     * @return 最终组装的响应字典
     */
    public Map<String, Object> execute(Map<String, Object> payload) {
        log.info("Starting sync pipeline execution with {} payload keys...", payload != null ? payload.size() : 0);
        SyncContext context = new SyncContext();

        for (SyncStage stage : stages) {
            try {
                stage.handle(payload, context);
            } catch (Exception e) {
                log.error("Sync stage failed at [{}]:", stage.getClass().getSimpleName(), e);
                throw e;
            }
        }

        Map<String, Object> response = context.buildResponse();
        log.info("Sync pipeline execution completed successfully.");
        return response;
    }
}
