package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.model.entity.Interview;
import com.jobpilot.jobpilotserver.service.InterviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping
    public Result<List<Interview>> getInterviews(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) String status) {
        List<Interview> list = interviewService.getInterviews(keyword, status);
        return Result.success(list);
    }

    @PostMapping
    public Result<Boolean> saveOrUpdateInterview(@RequestBody Interview interview) {
        boolean success = interviewService.saveOrUpdateInterview(interview);
        return Result.success(success);
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable String id, @RequestParam String status) {
        boolean success = interviewService.updateStatus(id, status);
        return Result.success(success);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteInterview(@PathVariable String id) {
        boolean success = interviewService.deleteInterview(id);
        return Result.success(success);
    }
}
