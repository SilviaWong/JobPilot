package com.jobpilot.jobpilotserver.strategy.sync.pipeline;

import java.util.Map;

/**
 * 全量数据同步阶段接口 (Pipeline / Chain of Responsibility Pattern)
 */
public interface SyncStage {

    /**
     * 当前阶段的执行顺序（升序排序，数值越小越先执行）
     */
    int getOrder();

    /**
     * 执行当前同步阶段的具体逻辑
     *
     * @param payload 客户端上传的全量数据字典
     * @param context 共享同步上下文
     */
    void handle(Map<String, Object> payload, SyncContext context);
}
