package com.jobpilot.jobpilotserver.common.config;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * SQLite 兼容多格式日期时间解析器 (LocalDateTimeTypeHandler)
 * 解决 SQLite 中 Prisma / Node.js 写入的 ISO-8601 带 'Z' 格式 (如 2026-08-10T14:34:33.121297Z)
 * 以及毫秒时间戳与标准 SQLite 时间字符串的混合兼容问题。
 */
@MappedTypes(LocalDateTime.class)
@MappedJdbcTypes(
        value = {
                JdbcType.TIMESTAMP,
                JdbcType.DATE,
                JdbcType.TIME,
                JdbcType.VARCHAR,
                JdbcType.CHAR,
                JdbcType.OTHER,
                JdbcType.NULL,
                JdbcType.INTEGER,
                JdbcType.BIGINT
        },
        includeNullJdbcType = true
)
public class SqliteLocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {

    private static final DateTimeFormatter STANDARD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.format(STANDARD_FORMATTER));
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object val = rs.getObject(columnName);
        return parse(val);
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object val = rs.getObject(columnIndex);
        return parse(val);
    }

    @Override
    public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object val = cs.getObject(columnIndex);
        return parse(val);
    }

    public static LocalDateTime parse(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof LocalDateTime ldt) {
            return ldt;
        }

        if (obj instanceof java.sql.Timestamp ts) {
            return ts.toLocalDateTime();
        }

        if (obj instanceof java.util.Date d) {
            return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
        }

        if (obj instanceof Number num) {
            long epoch = num.longValue();
            if (epoch < 100000000000L && epoch > -100000000000L) {
                epoch *= 1000;
            }
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault());
        }

        String val = obj.toString().trim();
        if (val.isEmpty()) {
            return null;
        }

        // 1. 纯数字时间戳 (毫秒或秒，如 1790679327141)
        if (val.matches("^-?\\d+$")) {
            try {
                long epoch = Long.parseLong(val);
                if (epoch < 100000000000L && epoch > -100000000000L) {
                    epoch *= 1000;
                }
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault());
            } catch (Exception ignored) {
            }
        }

        // 2. 带 'Z' 或带时区偏移的 ISO-8601 字符串 (如 2026-08-10T14:34:33.121297Z, 2026-10-13T07:41:35.750+08:00)
        if (val.endsWith("Z") || val.endsWith("z") || val.contains("+") || val.matches(".*T.*-\\d{2}:?\\d{2}$")) {
            try {
                return OffsetDateTime.parse(val).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            } catch (Exception ignored) {
            }
            try {
                Instant instant = Instant.parse(val);
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            } catch (Exception ignored) {
            }
        }

        // 3. 标准日期时间格式 (如 2026-10-13 07:41:35 或 2026-10-13T07:41:35 或带小数)
        try {
            String iso = val.replace(" ", "T");
            if (iso.endsWith("Z") || iso.endsWith("z")) {
                iso = iso.substring(0, iso.length() - 1);
            }
            return LocalDateTime.parse(iso);
        } catch (Exception ignored) {
        }

        // 4. 纯日期 (如 2026-08-10)
        try {
            return LocalDate.parse(val).atStartOfDay();
        } catch (Exception ignored) {
        }

        // 5. 最终兜底按 Instant 解析
        try {
            return LocalDateTime.ofInstant(Instant.parse(val), ZoneId.systemDefault());
        } catch (Exception ignored) {
        }

        return null;
    }
}
