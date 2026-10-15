package com.jobpilot.jobpilotserver.common.result;

public enum ResultCode {
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_SERVER_ERROR(500, "系统内部异常"),

    // 业务模块错误码 (1000+)
    JOB_NOT_FOUND(1001, "职位不存在"),
    COMPANY_NOT_FOUND(1002, "公司不存在"),
    INTERVIEW_NOT_FOUND(1003, "面试记录不存在"),

    // AI模块错误码 (2000+)
    AI_SERVICE_ERROR(2001, "AI 服务调用失败"),
    AI_CONFIG_MISSING(2002, "AI 配置信息缺失"),

    // 数据同步错误码 (3000+)
    SYNC_PAYLOAD_INVALID(3001, "数据同步载荷格式非法"),
    NORMALIZATION_ERROR(3002, "数据标准化清洗失败"),

    // 通用操作错误码 (5000+)
    OPERATION_FAILED(5001, "操作失败");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
