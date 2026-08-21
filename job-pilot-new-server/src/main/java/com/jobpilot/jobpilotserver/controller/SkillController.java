package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.model.entity.AiSkill;
import com.jobpilot.jobpilotserver.service.AiSkillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    public SkillController(AiSkillService skillService) {
        this.skillService = skillService;
    }

    private final AiSkillService skillService;

    @GetMapping("/list")
    public Result<List<AiSkill>> getSkills() {
        return Result.success(skillService.getSkills());
    }

    @PostMapping
    public Result<AiSkill> createSkill(@RequestBody AiSkill skill) {
        return Result.success(skillService.createSkill(skill));
    }

    @PutMapping("/{id}")
    public Result<AiSkill> updateSkill(@PathVariable String id, @RequestBody AiSkill skill) {
        return Result.success(skillService.updateSkill(id, skill));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSkill(@PathVariable String id) {
        boolean ok = skillService.deleteSkill(id);
        if (ok) return Result.success(null);
        return Result.error("删除失败");
    }

    @PostMapping("/execute")
    public Result<String> executeSkill(@RequestBody Map<String, String> body) {
        String jobId = body.get("jobId");
        String skillId = body.get("skillId");
        String res = skillService.executeSkill(jobId, skillId);
        return Result.success(res);
    }
}