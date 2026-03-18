import { defineStore } from "pinia";
import { ref, computed } from "vue";

export const useAuthStore = defineStore("auth", () => {
  const token = ref(localStorage.getItem("token") || "");
  const user = ref(null);

  const isAuthenticated = computed(() => !!token.value);

  function login(newToken: any) {
    token.value = newToken;
    localStorage.setItem("token", newToken);
  }

  function logout() {
    token.value = "";
    user.value = null;
    localStorage.removeItem("token");
  }

  return { token, user, isAuthenticated, login, logout };
});
