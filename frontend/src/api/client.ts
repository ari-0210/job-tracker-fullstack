import { useAuthStore } from "@/stores/auth";
import axios, {
  type AxiosInstance,
  type AxiosResponse,
  type AxiosError,
  type InternalAxiosRequestConfig,
} from "axios";
import { useRouter } from "vue-router";
const router = useRouter();

const apiClient: AxiosInstance = axios.create({
  baseURL: "/api",
  timeout: 10000,
});

// learn;请求拦截器
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

//learn;虽然在 Pinia 里设置了 defaults.headers，但如果 Token 过期了（后端返回 401），前端应该自动登出。
apiClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error: AxiosError) => {
    if (error.response && error.response.status === 401) {
      const authStore = useAuthStore();
      authStore.logout(); // learn;清除本地过期的 token
      router.push({ name: "Login" });
      console.error("认证过期，已自动退出");
    }
    return Promise.reject(error);
  },
);
export default apiClient;
