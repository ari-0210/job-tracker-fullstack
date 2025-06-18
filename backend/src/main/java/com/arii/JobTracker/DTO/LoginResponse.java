package com.arii.JobTracker.DTO;

public class LoginResponse {
    private String token;
    private String username;
    // private List<String> roles;

    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
}
