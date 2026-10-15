package com.jobpilot.jobpilotserver.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    private Boolean success;
    private T data;
    private String message;
    private String error;
    private Long total;
    private Boolean hasMore;
    private Object stats;

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    public Boolean getHasMore() { return hasMore; }
    public void setHasMore(Boolean hasMore) { this.hasMore = hasMore; }
    public Object getStats() { return stats; }
    public void setStats(Object stats) { this.stats = stats; }

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setData(data);
        return r;
    }

    public static <T> Result<T> success(T data, Object stats) {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setData(data);
        r.setStats(stats);
        return r;
    }

    public static <T> Result<T> success(T data, Long total, Boolean hasMore) {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setData(data);
        r.setTotal(total);
        r.setHasMore(hasMore);
        return r;
    }

    public static <T> Result<T> error(String message) {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setMessage(message);
        return r;
    }

    public static <T> Result<T> error(String message, String error) {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setMessage(message);
        r.setError(error);
        return r;
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setMessage(resultCode.getMessage());
        r.setError(String.valueOf(resultCode.getCode()));
        return r;
    }

    public static <T> Result<T> error(ResultCode resultCode, String customMessage) {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setMessage(customMessage);
        r.setError(String.valueOf(resultCode.getCode()));
        return r;
    }
}
