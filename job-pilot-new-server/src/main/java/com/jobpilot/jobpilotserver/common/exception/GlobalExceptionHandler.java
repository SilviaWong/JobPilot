package com.jobpilot.jobpilotserver.common.exception;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.common.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("Business exception occurred: code={}, message={}", e.getResultCode(), e.getMessage());
        return Result.error(e.getResultCode(), e.getMessage());
    }

    /**
     * 方法参数校验异常 (RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Validation error: {}", errorMsg);
        return Result.error(ResultCode.PARAM_ERROR, errorMsg);
    }

    /**
     * 表单绑定参数校验异常 (RequestParam / ModelAttribute)
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Bind error: {}", errorMsg);
        return Result.error(ResultCode.PARAM_ERROR, errorMsg);
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        return Result.error(ResultCode.PARAM_ERROR, e.getMessage());
    }

    /**
     * 静态资源或路径不存在异常 (Spring 6.1+ / Spring Boot 3)
     */
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public Result<Void> handleNoResourceFoundException(org.springframework.web.servlet.resource.NoResourceFoundException e) {
        log.warn("Resource or path not found: {}", e.getResourcePath());
        return Result.error(ResultCode.NOT_FOUND, "请求的接口或资源不存在: " + e.getResourcePath());
    }

    /**
     * 未知兜底异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("Unhandled system exception caught:", e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "系统繁忙，请稍后重试: " + e.getMessage());
    }
}
