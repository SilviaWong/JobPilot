package com.jobpilot.jobpilotserver.service;

import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardStats();
    Map<String, Object> getDashboardStats(boolean refresh);
    Map<String, Object> getFunnelAnalytics();
    Map<String, Object> getFunnelAnalytics(boolean refresh);
    void invalidateDashboardCache();
}