package com.arii.JobTracker.Controller;

import com.arii.JobTracker.DTO.LoginRequest;
import com.arii.JobTracker.DTO.LoginResponse;
import com.arii.JobTracker.DTO.UserCreateRequestDto;
import com.arii.JobTracker.Security.JwtUtil;
import com.arii.JobTracker.Service.UserService;
import com.arii.JobTracker.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


    @RestController
    @RequestMapping("/api/auth")
    public class AuthController {

        @Autowired
        private AuthenticationManager authenticationManager; // 用于实际的认证处理

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private UserService userService; // 注入新的 UserService

        @PostMapping("/register")
        public ResponseEntity<?> registerUser(@RequestBody UserCreateRequestDto userCreateRequest) {
            try {
                User registeredUser = userService.createUser(userCreateRequest);
                // 注册成功，可以返回成功信息，新创建的用户信息
                return ResponseEntity.ok("User registered successfully! Username: " + registeredUser.getUsername());
            } catch (RuntimeException e) {
                // 例如用户名已存在的错误
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        @PostMapping("/login")
        public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
            Authentication authentication;
            try {
                // 使用 AuthenticationManager 进行用户认证（ UserDetailsServiceImpl 和 PasswordEncoder）
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
                );
            } catch (BadCredentialsException e) {
                // 用户名或密码错误
                return ResponseEntity.status(401).body("Incorrect username or password");
            }

            // 加载 UserDetails ，获取生成 JWT 所需的信息 (通常是用户名)
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 生成 JWT
            final String jwt = jwtUtil.generateToken(userDetails);// 或者 jwtUtil.generateToken(userDetails)

            // 返回 JWT 给前端
            return ResponseEntity.ok(new LoginResponse(jwt, userDetails.getUsername()));
        }
    }

