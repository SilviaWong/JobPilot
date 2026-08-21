package com.jobpilot.jobpilotserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jobpilot.jobpilotserver.model.entity.Resume;

import java.util.Map;

public interface ResumeService extends IService<Resume> {
    Map<String, Object> getResume();
    Map<String, Object> updateResume(Map<String, Object> body);
    String getResumeText();
}
