package com.arii.JobTracker.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginResponse {
    @Schema(description = "JWT 访问令牌，后续请求需放在 Authorization 请求头中（带 Bearer 前缀）",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqYWNrIn0...")
    private String token;
    @Schema(description = "用户名")
    private String username;

    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
}
