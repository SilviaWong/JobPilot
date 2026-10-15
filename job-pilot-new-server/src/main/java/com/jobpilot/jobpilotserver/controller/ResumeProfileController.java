package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.model.entity.ResumeProfile;
import com.jobpilot.jobpilotserver.service.ResumeProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeProfileController {

    private final ResumeProfileService resumeProfileService;

    public ResumeProfileController(ResumeProfileService resumeProfileService) {
        this.resumeProfileService = resumeProfileService;
    }

    @GetMapping({"", "/index"})
    public Result<List<Map<String, Object>>> getProfiles() {
        return Result.success(resumeProfileService.getProfiles());
    }

    @PostMapping({"", "/index"})
    public Result<ResumeProfile> createProfile(@RequestBody Map<String, Object> body) {
        return Result.success(resumeProfileService.createProfile(body));
    }

    @PutMapping("/{id}")
    public Result<ResumeProfile> updateProfile(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return Result.success(resumeProfileService.updateProfile(id, body));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProfile(@PathVariable String id) {
        resumeProfileService.deleteProfile(id);
        return Result.success(null);
    }

    @PostMapping("/{id}/set-default")
    public Result<ResumeProfile> setDefault(@PathVariable String id) {
        return Result.success(resumeProfileService.setDefaultProfile(id));
    }
}

