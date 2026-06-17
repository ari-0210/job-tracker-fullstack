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


/**
 * 用户认证与权限令牌控制层.
 * <p>提供全系统的安全入口，负责新用户注册安全审查、账号密码强认证及安全 JWT 令牌的签发与分发.</p>
 *
 * @author Ari
 */
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

    /**
     * 开放式新用户注册接口.
     * <p>接收前端传输的账户凭证，经由底层业务层防重校验后完成账号的安全持久化.</p>
     *
     * @param userCreateRequest 包含用户名、明文密码及初始角色列表的数据传输对象
     * @return 包含成功注册提示信息的 Result 外壳
     */
    @Operation(summary = "用户注册", description = "创建新账号并持久化到数据库")
        @PostMapping("/register")
    public Result<String> registerUser(@RequestBody UserCreateRequestDto userCreateRequest) {

                User registeredUser = userService.createUser(userCreateRequest);
        // learn;注册成功，可以返回成功信息，新创建的用户信息,去掉try- catch
        return Result.success("User registered successfully! Username: " + registeredUser.getUsername());

        }

    /**
     * 用户登录认证并签发安全令牌接口.
     * <p>本接口深度对接 Spring Security 认证防线。若密码比对失败，将由本方法就地截胡并转换为标准的 401 业务响应.</p>
     *
     * @param loginRequest 包含用户名与明文密码的登录请求载荷
     * @return 包含高强度 JWT 访问令牌及用户基本信息的登录结果载荷
     * @throws Exception 当发生不可预知的系统级认证崩溃时抛出
     */
    @Operation(summary = "用户登录", description = "验证账号密码并返回 JWT 访问令牌")
        @PostMapping("/login")
    public Result<LoginResponse> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
            Authentication authentication;
            try {
                // 调度 Spring Security 门卫执行底层密码哈希比对与账户状态审查
                // 使用 AuthenticationManager 进行用户认证（ UserDetailsServiceImpl 和 PasswordEncoder）,
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
                );
            } catch (BadCredentialsException e) {
                // 捕获安全防御链抛出的凭证错误异常，就地收拢为标准的 401 统一业务响应外壳
                return Result.failed(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
            }

        // learn:加载 UserDetails ，获取生成 JWT 所需的信息 (通常是用户名)
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // learn;生成 JWT
            final String jwt = jwtUtil.generateToken(userDetails);// 或者 jwtUtil.generateToken(userDetails)

        // learn;返回 JWT 给前端
        return Result.success(new LoginResponse(jwt, userDetails.getUsername()));
        }
    }

