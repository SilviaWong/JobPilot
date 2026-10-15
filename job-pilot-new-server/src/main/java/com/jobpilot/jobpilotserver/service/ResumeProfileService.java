package com.jobpilot.jobpilotserver.service;

import com.jobpilot.jobpilotserver.model.entity.ResumeProfile;

import java.util.List;
import java.util.Map;

public interface ResumeProfileService {
    List<Map<String, Object>> getProfiles();
    ResumeProfile createProfile(Map<String, Object> body);
    ResumeProfile updateProfile(String id, Map<String, Object> body);
    void deleteProfile(String id);
    ResumeProfile setDefaultProfile(String id);
    String getDefaultResumeContent();
}

