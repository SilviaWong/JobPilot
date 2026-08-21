package com.jobpilot.jobpilotserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jobpilot.jobpilotserver.model.entity.Interview;

import java.util.List;

public interface InterviewService extends IService<Interview> {
    List<Interview> getInterviews(String keyword, String status);
    boolean saveOrUpdateInterview(Interview interview);
    boolean updateStatus(String id, String status);
    boolean deleteInterview(String id);
}
