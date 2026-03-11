import { useAuthStore } from "@/stores/auth";
import axios from "axios";
import { useRouter } from "vue-router";
const router = useRouter();
const apiClient = axios.create({
  baseURL: "/api",
});

//learn;虽然在 Pinia 里设置了 defaults.headers，但如果 Token 过期了（后端返回 401），前端应该自动登出。
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      const authStore = useAuthStore();
      authStore.logout(); // learn;清除本地过期的 token
      router.push({ name: "Login" });
    }
    return Promise.reject(error);
  },
);
export default apiClient;
