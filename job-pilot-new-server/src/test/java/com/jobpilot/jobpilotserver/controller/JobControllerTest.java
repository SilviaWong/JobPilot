package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.model.dto.*;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.service.JobService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JobControllerTest {

    @Test
    @DisplayName("测试 JobController 接收强类型 DTO 进行批量更新")
    public void testJobControllerBatchUpdate() {
        JobService jobService = (JobService) Proxy.newProxyInstance(
                JobService.class.getClassLoader(),
                new Class<?>[]{JobService.class},
                (proxy, method, args) -> {
                    if ("batchUpdate".equals(method.getName())) {
                        List<String> ids = (List<String>) args[0];
                        return ids != null ? ids.size() : 0;
                    } else if ("updateJobStatus".equals(method.getName())) {
                        Job j = new Job();
                        j.setId((String) args[0]);
                        j.setStatus((String) args[1]);
                        return j;
                    }
                    return null;
                }
        );

        JobController controller = new JobController(jobService);

        // 1. 测试 updateStatus DTO
        JobStatusUpdateRequest statusReq = new JobStatusUpdateRequest();
        statusReq.setStatus("interview");
        Result<Job> statusRes = controller.updateStatus("job-101", statusReq);
        assertTrue(statusRes.getSuccess());
        assertEquals("interview", statusRes.getData().getStatus());

        // 2. 测试 batchUpdate DTO
        JobBatchUpdateRequest batchReq = new JobBatchUpdateRequest();
        batchReq.setIds(List.of("1", "2", "3", "4"));
        batchReq.setIsFavorited(true);
        batchReq.setTags(List.of("重点推荐"));

        Result<Map<String, Object>> batchRes = controller.batchUpdate(batchReq);
        assertTrue(batchRes.getSuccess());
        assertEquals(4, batchRes.getData().get("count"));
    }
}
