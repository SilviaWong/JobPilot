package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.service.DataBackfillService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 历史数据清洗与回填控制器
 */
@RestController
@RequestMapping("/api/admin/backfill")
public class DataBackfillController {

    private final DataBackfillService dataBackfillService;

    public DataBackfillController(DataBackfillService dataBackfillService) {
        this.dataBackfillService = dataBackfillService;
    }

    /**
     * 触发历史数据物理字段全量回填
     *
     * @return 执行统计结果
     */
    @PostMapping
    public Result<Map<String, Object>> runBackfill() {
        Map<String, Object> stats = dataBackfillService.backfillAll();
        return Result.success(stats);
    }
}
