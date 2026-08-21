package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.service.JobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    private final JobService jobService;

    @GetMapping
    public Result<List<Object>> getJobs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "normal") String status,
            @RequestParam(required = false) Boolean filterFavoritesOnly,
            @RequestParam(required = false) Boolean filterShowHidden,
            @RequestParam(required = false) Boolean filterShowBlacklisted,
            @RequestParam(defaultValue = "all") String platform,
            @RequestParam(defaultValue = "all") String education,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "all") String aiDiagnosisFilter) {

        Map<String, Object> res = jobService.getJobs(page, pageSize, status, filterFavoritesOnly,
                filterShowHidden, filterShowBlacklisted, platform, education, keyword, aiDiagnosisFilter);

        List<Object> data = (List<Object>) res.get("data");
        Long total = (Long) res.get("total");
        Boolean hasMore = (Boolean) res.get("hasMore");

        return Result.success(data, total, hasMore);
    }

    @PutMapping("/{id}/status")
    public Result<Job> updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        Job updated = jobService.updateJobStatus(id, status);
        return Result.success(updated);
    }

    @PutMapping("/{id}")
    public Result<Job> updateAttributes(@PathVariable String id, @RequestBody Map<String, Object> body) {
        Boolean isFavorited = body.containsKey("isFavorited") ? (Boolean) body.get("isFavorited") : null;
        Boolean isHidden = body.containsKey("isHidden") ? (Boolean) body.get("isHidden") : null;
        List<String> tags = body.containsKey("tags") ? (List<String>) body.get("tags") : null;

        Job updated = jobService.updateJobAttributes(id, isFavorited, isHidden, tags);
        return Result.success(updated);
    }

    @PutMapping("/batch")
    public Result<Map<String, Object>> batchUpdate(@RequestBody Map<String, Object> body) {
        List<String> ids = (List<String>) body.get("ids");
        Boolean isFavorited = (Boolean) body.get("isFavorited");
        Boolean isHidden = (Boolean) body.get("isHidden");
        List<String> tags = (List<String>) body.get("tags");
        int count = jobService.batchUpdate(ids, isFavorited, isHidden, tags);
        return Result.success(Map.of("count", count));
    }

    @DeleteMapping("/batch")
    public Result<Map<String, Object>> batchDelete(@RequestBody Map<String, Object> body) {
        List<String> ids = (List<String>) body.get("ids");
        int count = jobService.batchDelete(ids);
        return Result.success(Map.of("count", count));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteJob(@PathVariable String id) {
        boolean ok = jobService.deleteJob(id);
        if (ok) {
            return Result.success(null);
        }
        return Result.error("删除失败，职位不存在");
    }

    @PostMapping("/analyze")
    public Result<Object> analyzeJob(@RequestBody Map<String, String> body) {
        String jobId = body.get("jobId");
        Map<String, Object> res = jobService.analyzeJob(jobId);
        return Result.success(res.get("data"));
    }

    @PostMapping("/generate-greeting")
    public Result<Object> generateGreeting(@RequestBody Map<String, String> body) {
        String jobId = body.get("jobId");
        Map<String, Object> res = jobService.generateGreeting(jobId);
        return Result.success(res.get("data"));
    }

    @PostMapping("/predict-questions")
    public Result<Object> predictQuestions(@RequestBody Map<String, String> body) {
        String jobId = body.get("jobId");
        Map<String, Object> res = jobService.predictQuestions(jobId);
        return Result.success(res.get("data"));
    }
}