package com.arii.JobTracker.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResultCode {
    // --- 成功状态 ---
    SUCCESS(200, "操作成功"),
    CREATED(201, "创建成功"),

    // --- 客户端错误 (4xx) ---
    BAD_REQUEST(400, "参数错误或非法请求"),
    UNAUTHORIZED(401, "尚未登录或Token已失效"),
    FORBIDDEN(403, "权限不足，拒绝访问"),
    VALIDATE_FAILED(422, "参数校验未通过"),

    // --- 服务端错误 (5xx) ---
    FAILED(500, "服务器内部崩溃，请联系管理员");

    private final Integer code;
    private final String message;

   

    public Integer getCode() { return code; }
    public String getMessage() { return message; }
}
