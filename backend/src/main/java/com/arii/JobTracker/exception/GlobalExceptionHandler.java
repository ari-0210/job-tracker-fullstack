package com.arii.JobTracker.exception;


import com.arii.JobTracker.common.Result;
import com.arii.JobTracker.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     *  专门拦截业务中手动抛出的 RuntimeException (比如：越权、文件不存在)
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("系统触发运行时异常: ", e);
        return Result.failed(ResultCode.BAD_REQUEST.getCode(), e.getMessage());
    }

    /**
     * 专门拦截 @Valid 校验失败导致的异常(比如：标题不能为空)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("数据校验未通过: ", ex);


        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMsg = fieldError != null ? fieldError.getDefaultMessage() : "参数校验未通过";

        return Result.failed(ResultCode.VALIDATE_FAILED.getCode(), errorMsg);
    }

    /**
     * 兜底顶级异常 (防止其他未知崩溃泄漏服务器机密)
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("【致命崩溃】服务器发生未捕获的未知异常: ", e);

        return Result.failed(ResultCode.FAILED);
    }
}
