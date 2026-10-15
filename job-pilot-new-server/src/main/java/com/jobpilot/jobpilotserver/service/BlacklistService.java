package com.jobpilot.jobpilotserver.service;

import java.util.Map;

public interface BlacklistService {
    Map<String, Object> getBlacklist(String keyword, String source, int page, int pageSize);
    Map<String, Object> addBlacklist(Map<String, Object> body);
    boolean removeBlacklist(String companyName);
}

