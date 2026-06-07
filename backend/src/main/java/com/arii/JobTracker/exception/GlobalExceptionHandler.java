package com.arii.JobTracker.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * learn;1. 专门拦截业务中手动抛出的 RuntimeException (比如：越权、文件不存在)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        //learn;在后端控制台打印出完整的错误堆栈
        log.error("系统触发运行时异常: ", e);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", e.getMessage()); // learn;此时拿到的是 throw 的自定义一句话提示
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * learn;2. 专门拦截 @Valid 校验失败导致的异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 422); // learn;422 代表参数格式错误

        // learn;抓取具体是哪个字段没过
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMsg = fieldError != null ? fieldError.getDefaultMessage() : "参数校验未通过";

        response.put("message", errorMsg); // learn;比如直接返回："任务标题不能为空"
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    /**
     * learn;3. 兜底顶级异常 (防止其他未知崩溃泄漏服务器机密)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("【致命崩溃】服务器发生未捕获的未知异常: ", e);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "服务器内部崩溃，请联系管理员");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
