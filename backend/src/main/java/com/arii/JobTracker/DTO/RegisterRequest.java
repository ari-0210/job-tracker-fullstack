package com.arii.JobTracker.DTO;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
}