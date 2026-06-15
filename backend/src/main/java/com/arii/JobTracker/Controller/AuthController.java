package com.arii.JobTracker.Controller;

import com.arii.JobTracker.DTO.LoginRequest;
import com.arii.JobTracker.DTO.LoginResponse;
import com.arii.JobTracker.DTO.UserCreateRequestDto;
import com.arii.JobTracker.Security.JwtUtil;
import com.arii.JobTracker.Service.UserService;
import com.arii.JobTracker.common.Result;
import com.arii.JobTracker.common.ResultCode;
import com.arii.JobTracker.pojo.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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


@Tag(name = "00.认证管理", description = "用户注册、登录及令牌发放")
@RestController
    @RequestMapping("/api/auth")
    public class AuthController {

        @Autowired
        private AuthenticationManager authenticationManager; // learn:用于实际的认证处理

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private UserService userService; // learn;注入新的 UserService

    @Operation(summary = "用户注册", description = "创建新账号并持久化到数据库")
        @PostMapping("/register")
    public Result<String> registerUser(@RequestBody UserCreateRequestDto userCreateRequest) {

                User registeredUser = userService.createUser(userCreateRequest);
        // learn;注册成功，可以返回成功信息，新创建的用户信息,去掉try- catch
        return Result.success("User registered successfully! Username: " + registeredUser.getUsername());

        }

    @Operation(summary = "用户登录", description = "验证账号密码并返回 JWT 访问令牌")
        @PostMapping("/login")
    public Result<LoginResponse> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
            Authentication authentication;
            try {
                // learn;使用 AuthenticationManager 进行用户认证（ UserDetailsServiceImpl 和 PasswordEncoder）
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
                );
            } catch (BadCredentialsException e) {
                // learn;用户名或密码错误
                return Result.failed(ResultCode.UNAUTHORIZED.getCode(), "Incorrect username or password");
            }

        // learn:加载 UserDetails ，获取生成 JWT 所需的信息 (通常是用户名)
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // learn;生成 JWT
            final String jwt = jwtUtil.generateToken(userDetails);// 或者 jwtUtil.generateToken(userDetails)

        // learn;返回 JWT 给前端
        return Result.success(new LoginResponse(jwt, userDetails.getUsername()));
        }
    }

