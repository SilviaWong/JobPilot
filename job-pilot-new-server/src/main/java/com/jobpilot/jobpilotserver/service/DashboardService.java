package com.jobpilot.jobpilotserver.service;

import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardStats();
    Map<String, Object> getFunnelAnalytics();
}