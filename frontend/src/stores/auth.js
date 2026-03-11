import { defineStore } from "pinia";
import { ref, computed } from "vue";
import apiClient from "@/api/axiosInstance";

export const useAuthStore = defineStore("auth", () => {
  const token = ref(localStorage.getItem("token") || "");
  const user = ref(null);

  const isAuthenticated = computed(() => !!token.value);

  // learn;更新 Axios 请求头的私有方法
  const _updateHeader = (newToken) => {
    if (newToken) {
      apiClient.defaults.headers.common["Authorization"] = `Bearer ${newToken}`;
    } else {
      delete apiClient.defaults.headers.common["Authorization"];
    }
  };
  // learn;初始化时立即设置一次请求头
  _updateHeader(token.value);

  function login(newToken) {
    token.value = newToken;
    localStorage.setItem("token", newToken);
    _updateHeader(newToken);
  }

  function logout() {
    token.value = "";
    user.value = null;
    localStorage.removeItem("token");
    _updateHeader(null);
  }

  return { token, user, isAuthenticated, login, logout };
});
