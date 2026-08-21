package com.jobpilot.jobpilotserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jobpilot.jobpilotserver.model.entity.AiSkill;

import java.util.List;

public interface AiSkillService extends IService<AiSkill> {
    List<AiSkill> getSkills();
    AiSkill createSkill(AiSkill skill);
    AiSkill updateSkill(String id, AiSkill skill);
    boolean deleteSkill(String id);
    String executeSkill(String jobId, String skillId);
}