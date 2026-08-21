package com.jobpilot.jobpilotserver.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 跨平台薪资折算与归一化解析器 (统一换算为 K/月)
 */
public class SalaryParser {

    private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*[-~—到至]\\s*(\\d+(?:\\.\\d+)?)");
    private static final Pattern SINGLE_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");
    private static final Pattern MONTHS_PATTERN = Pattern.compile("·?\\s*(\\d+)\\s*薪");

    public static class SalaryRange {
        public double minK; // 以 K/月 为单位
        public double maxK;

        public SalaryRange(double minK, double maxK) {
            this.minK = Math.round(minK * 100.0) / 100.0;
            this.maxK = Math.round(maxK * 100.0) / 100.0;
        }

        @Override
        public String toString() {
            if (minK == maxK) {
                return minK + "K";
            }
            return minK + "-" + maxK + "K";
        }
    }

    /**
     * 解析各种复杂薪资字符串为以 K/月 为单位的标准区间
     * 例如:
     * "15-25K" -> 15.0 - 25.0
     * "20-30K·16薪" -> 26.67 - 40.0 (按 16/12 折算月薪)
     * "2-3万" -> 20.0 - 30.0
     * "30-50万/年" -> 25.0 - 41.67
     * "400-500元/天" -> 8.7 - 10.87 (按 21.75个工作日/月折算)
     * "50-80元/小时" -> 8.7 - 13.92 (按 8h/天 * 21.75天折算)
     */
    public static SalaryRange parse(String salaryStr) {
        if (salaryStr == null || salaryStr.isBlank()) {
            return null;
        }

        String str = salaryStr.trim().toLowerCase();
        if (str.contains("面议") || str.contains("保密") || str.contains("不限")) {
            return null;
        }

        // 检查是否有额外多薪标注，如 16薪
        double monthBonusMultiplier = 1.0;
        Matcher monthsMatcher = MONTHS_PATTERN.matcher(str);
        if (monthsMatcher.find()) {
            try {
                int totalMonths = Integer.parseInt(monthsMatcher.group(1));
                if (totalMonths > 12) {
                    monthBonusMultiplier = (double) totalMonths / 12.0;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        boolean isHour = str.contains("时") || str.contains("/h") || str.contains("小时");
        boolean isDay = str.contains("天") || str.contains("/日") || str.contains("元/天");
        boolean isYear = str.contains("年") || str.contains("/年") || str.contains("万/年");

        double baseMultiplier = 1.0;
        if (str.contains("k") || str.contains("千")) {
            baseMultiplier = 1.0;
        } else if (str.contains("万") || str.contains("w")) {
            if (isYear) {
                baseMultiplier = 10.0 / 12.0;
            } else {
                baseMultiplier = 10.0;
            }
        } else if (isHour) {
            // 时薪: 元/小时 -> K/月 (8小时 * 21.75天 / 1000)
            baseMultiplier = (8.0 * 21.75) / 1000.0;
        } else if (isDay) {
            // 日薪: 元/天 -> K/月 (21.75天 / 1000)
            baseMultiplier = 21.75 / 1000.0;
        } else if (isYear) {
            // 元/年 -> K/月
            baseMultiplier = 1.0 / 12.0 / 1000.0;
        }

        double finalMultiplier = baseMultiplier * (isYear ? 1.0 : monthBonusMultiplier);

        Matcher rangeMatcher = RANGE_PATTERN.matcher(str);
        if (rangeMatcher.find()) {
            double low = Double.parseDouble(rangeMatcher.group(1)) * finalMultiplier;
            double high = Double.parseDouble(rangeMatcher.group(2)) * finalMultiplier;
            return new SalaryRange(low, high);
        }

        Matcher singleMatcher = SINGLE_PATTERN.matcher(str);
        if (singleMatcher.find()) {
            double val = Double.parseDouble(singleMatcher.group(1)) * finalMultiplier;
            return new SalaryRange(val, val);
        }

        return null;
    }

    /**
     * 判定目标薪资是否落在筛选区间内
     */
    public static boolean matchesFilter(String salaryStr, String filter) {
        if (filter == null || filter.isBlank() || "all".equalsIgnoreCase(filter)) {
            return true;
        }
        SalaryRange jobRange = parse(salaryStr);
        if (jobRange == null) {
            return false;
        }

        SalaryRange filterRange = parse(filter);
        if (filterRange == null) {
            return true;
        }

        return jobRange.maxK >= filterRange.minK && jobRange.minK <= filterRange.maxK;
    }
}
