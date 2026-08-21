package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.service.ResumeService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping
    public Result<Map<String, Object>> getResume() {
        return Result.success(resumeService.getResume());
    }

    @PostMapping
    public Result<Map<String, Object>> updateResume(@RequestBody Map<String, Object> body) {
        return Result.success(resumeService.updateResume(body));
    }
}
