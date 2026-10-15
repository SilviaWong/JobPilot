package com.jobpilot.jobpilotserver.util;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 婉拒匹配与黑名单解析引擎工具类
 */
public class BlacklistParserUtil {

    public static final List<String> REJECTION_KEYWORDS = List.of(
            "不合适", "暂不匹配", "暂不考虑", "不符合", "暂不合适", "技能不符",
            "不太匹配", "不搭", "未能通过", "未通过",
            "需要本", "要求本", "必须统招", "学历不符", "要求全日制", "专业不符", "经验不符",
            "感谢关注", "很遗憾", "抱歉", "对不起", "感谢投递", "祝您早日", "祝您找到更合适",
            "招满", "招完", "已招到", "停止招聘", "hc已满", "HC已满", "职位已关闭", "暂无合适空缺", "岗位已停"
    );

    public static final List<Pattern> EXCLUSION_PATTERNS = List.of(
            Pattern.compile("不是"),
            Pattern.compile("不得不"),
            Pattern.compile("如果不"),
            Pattern.compile("不见不散"),
            Pattern.compile("不知"),
            Pattern.compile("不限"),
            Pattern.compile("不妨"),
            Pattern.compile("不耽误"),
            Pattern.compile("不影响"),
            Pattern.compile("不仅"),
            Pattern.compile("不论")
    );

    private static final Pattern COMPOSITE_REJECTION_1 = Pattern.compile("感谢.*但", Pattern.CASE_INSENSITIVE);
    private static final Pattern COMPOSITE_REJECTION_2 = Pattern.compile("遗憾.*无法", Pattern.CASE_INSENSITIVE);
    private static final Pattern COMPOSITE_REJECTION_3 = Pattern.compile("抱歉.*目前", Pattern.CASE_INSENSITIVE);

    public static class RejectionCheckResult {
        public boolean isRejected;
        public String reason;
        public String matchedKeyword;

        public RejectionCheckResult(boolean isRejected, String reason, String matchedKeyword) {
            this.isRejected = isRejected;
            this.reason = reason;
            this.matchedKeyword = matchedKeyword;
        }
    }

    public static RejectionCheckResult isRejectionMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return new RejectionCheckResult(false, null, null);
        }
        String cleanMsg = message.trim();
        String filteredMsg = cleanMsg;
        for (Pattern pat : EXCLUSION_PATTERNS) {
            filteredMsg = pat.matcher(filteredMsg).replaceAll("");
        }

        for (String kw : REJECTION_KEYWORDS) {
            if (filteredMsg.contains(kw)) {
                String sub = cleanMsg.length() > 100 ? cleanMsg.substring(0, 100) : cleanMsg;
                return new RejectionCheckResult(true, "沟通婉拒: " + sub, kw);
            }
        }

        if (COMPOSITE_REJECTION_1.matcher(filteredMsg).find()
                || COMPOSITE_REJECTION_2.matcher(filteredMsg).find()
                || COMPOSITE_REJECTION_3.matcher(filteredMsg).find()) {
            String sub = cleanMsg.length() > 100 ? cleanMsg.substring(0, 100) : cleanMsg;
            return new RejectionCheckResult(true, "沟通婉拒: " + sub, "复合婉拒语式");
        }

        return new RejectionCheckResult(false, null, null);
    }

    public static String cleanCompanyNameForBlacklist(String name) {
        if (name == null) return "";
        return name.replaceAll("[·.]{2,}", "")
                .replaceAll("^[【\\[(（][^】\\])）]+[】\\])）]", "")
                .replaceAll("[\\r\\n\\t]+", " ")
                .trim();
    }

    public static class ParsedRejectionItem {
        public String companyName;
        public String message;
        public String reason;

        public ParsedRejectionItem(String companyName, String message, String reason) {
            this.companyName = companyName;
            this.message = message;
            this.reason = reason;
        }
    }

    public static List<ParsedRejectionItem> parseChatTextLines(String text) {
        if (text == null || text.trim().isEmpty()) return Collections.emptyList();
        List<ParsedRejectionItem> results = new ArrayList<>();
        String[] lines = text.split("\\n");
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) continue;

            String comp = "";
            String msg = "";
            int splitIdx = -1;
            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                if (ch == ':' || ch == '：' || ch == '\t' || ch == '-' || ch == '—') {
                    splitIdx = i;
                    break;
                }
            }

            if (splitIdx > 0) {
                comp = line.substring(0, splitIdx).trim();
                msg = line.substring(splitIdx + 1).trim();
            } else {
                continue;
            }

            String cleanComp = cleanCompanyNameForBlacklist(comp);
            if (cleanComp.length() >= 2 && cleanComp.length() <= 40) {
                RejectionCheckResult rej = isRejectionMessage(msg);
                if (rej.isRejected) {
                    String sub = msg.length() > 60 ? msg.substring(0, 60) : msg;
                    results.add(new ParsedRejectionItem(cleanComp, msg, rej.reason != null ? rej.reason : "沟通婉拒: " + sub));
                }
            }
        }
        return results;
    }
}

