package com.arii.JobTracker.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "统一常规API响应外壳")
public class Result<T> {

    @Schema(description = "业务自定义状态码", example = "200")
    private Integer code;

    @Schema(description = "提示信息", example = "操作成功")
    private String message;

    @Schema(description = "核心干货数据")
    private T data;

    // learn;全局禁用 new 关键字创建，强迫使用下方的静态快捷流式方法
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // learn;成功并携带数据 (常用于查询、创建)
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    // learn;成功但不带数据 (常用于删除、修改)
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    // learn;通用失败 (直接传入具体的枚举)
    public static <T> Result<T> failed(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    // learn;自定义失败提示 (常用于全局异常处理器吐出动态提示)
    public static <T> Result<T> failed(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}