import { useAuthStore } from "@/stores/auth";
import axios, {
  type AxiosInstance,
  type AxiosResponse,
  type AxiosError,
  type InternalAxiosRequestConfig,
} from "axios";
import router from "@/router";

/**
 * 全局企业级 Axios 网络无状态会话通信客户端实例.
 * <p>内置高抗抖动超时机制（10s）以及统一的前置加冕与后置硬性鉴权熔断拦截器链.</p>
 * * @author Ari
 */
const apiClient: AxiosInstance = axios.create({
  baseURL: "/api",
  timeout: 10000,
});

/**
 * 请求拦截器：无状态身份自动化注入.
 * <p>拦截每一发公网向请求，动态透视 Pinia 状态树。若 Token 健在，强行追加 Bearer 国际标准前缀打入请求头。</p>
 */
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const authStore = useAuthStore();
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  },
);
/**
 * 响应拦截器：全防型安全鉴权中心.
 * <p>双向捕获后端安全防线抛回的 401(未认证) 与 403(越权/禁止访问) 状态码。</p>
 * <p>一旦引爆安全红线，就地就地执行前端无状态数据自清洗(logout)，并强制实施无感路由重定向弹回登录视窗。</p>
 */
//learn;虽然在 Pinia 里设置了 defaults.headers，但如果 Token 过期了（后端返回 401），前端应该自动登出。
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    if (response.data && response.data.code && response.data.code !== 200) {
      // learn; 使用 Naive UI 的独立全局弹窗机制（或者前端原生 window.alert 兜底）
      if (window.$message) {
        window.$message.error(response.data.message || "未知业务矩阵崩溃");
      } else {
        alert(response.data.message);
      }
      return Promise.reject(new Error(response.data.message));
    }
    return response;
  },
  (error: AxiosError) => {
    const authStore = useAuthStore();
    if (error.response) {
      const status = error.response.status;
      const responseData = error.response.data as any;
      const errorMsg =
        responseData?.message || `网络公网会话异常: Status[${status}]`;

      if (status === 401 || status === 403) {
        authStore.logout();
        router.push({ name: "Login" });
        if (window.$message)
          window.$message.warning("安全认证已过期，请重新登录！");
      } else {
        if (window.$message) window.$message.error(errorMsg);
      }
    } else {
      if (window.$message)
        window.$message.error("云端网关无响应，请检查公网链路！");
    }
    return Promise.reject(error);
  },
);
export default apiClient;
