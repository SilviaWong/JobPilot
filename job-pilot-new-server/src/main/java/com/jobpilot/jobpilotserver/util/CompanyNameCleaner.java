package com.jobpilot.jobpilotserver.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 企业名称清洗与规范化工具类
 * <p>
 * 对齐前端与爬虫端的数据清洗规则：
 * 1. Unicode 转义字符解码（如 \\u4e2d -> 中）；
 * 2. URL 编码解码（如 %E4%B8%AD%E5%9B%BD -> 中国）；
 * 3. 中文全角括号（‘（’、‘）’）统一替换为英文半角括号（‘(’、‘)’）；
 * 4. 去除字符串内部所有空格及首尾空白字符。
 */
public final class CompanyNameCleaner {

    private CompanyNameCleaner() {
        // 工具类禁止实例化
    }

    /**
     * 规范化清洗企业名称/全称
     *
     * @param str 待清洗的企业名称原始字符串
     * @return 规范化后的企业名称（若输入为空则返回空字符串 ""）
     */
    public static String clean(String str) {
        if (str == null || str.isBlank()) {
            return "";
        }

        String res = str;

        // 1. 处理 Unicode 转义字符（\\uXXXX 或 \\UXXXX）
        if (res.contains("\\u") || res.contains("\\U")) {
            try {
                StringBuilder sb = new StringBuilder();
                int i = 0;
                int len = res.length();
                while (i < len) {
                    if (i + 5 < len && res.charAt(i) == '\\' && (res.charAt(i + 1) == 'u' || res.charAt(i + 1) == 'U')) {
                        try {
                            int code = Integer.parseInt(res.substring(i + 2, i + 6), 16);
                            sb.append((char) code);
                            i += 6;
                            continue;
                        } catch (Exception ignored) {
                            // 解析失败按原字符保留
                        }
                    }
                    sb.append(res.charAt(i));
                    i++;
                }
                res = sb.toString();
            } catch (Exception ignored) {}
        }

        // 2. 处理 URL 编码字符（%XX）
        if (res.contains("%")) {
            try {
                res = URLDecoder.decode(res, StandardCharsets.UTF_8);
            } catch (Exception ignored) {}
        }

        // 3. 中文全角括号转英文半角括号，并去除所有空白符
        return res.replace('（', '(')
                  .replace('）', ')')
                  .replaceAll("\\s+", "")
                  .trim();
    }

    /**
     * 兼容别名方法
     */
    public static String cleanCompanyName(String str) {
        return clean(str);
    }
}
