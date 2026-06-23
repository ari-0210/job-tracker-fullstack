import apiClient from "./client";
import type { AuthCredentials, AuthResponse } from "@/types/auth";
import type { Result } from "@/types/job";

/**
 * 调度后端开户防线，持久化新账户凭证.
 * @author Ari
 * @param {AuthCredentials} credentials - 包含注册所需的用户名、明文密码
 * @returns {Promise<Result<any>>} 包含后端成功响应负载的 Axios Promise
 */
export const registerUser = (credentials: AuthCredentials) => {
  return apiClient.post<Result<any>>("/auth/register", credentials);
};

/**
 * 触发 Spring Security 强认证防线并获取有状态/无状态安全访问凭证.
 * @param {AuthResponse} credentials - 登录凭证载荷 (用户名与明文密码)
 * @returns {Promise<Result<AuthResponse>>} 承载高强度 JWT 访问令牌及用户身份上下文的 Axios Promise
 */
export const loginUser = (credentials: AuthResponse) => {
  return apiClient.post<Result<any>>("/auth/login", credentials);
};
