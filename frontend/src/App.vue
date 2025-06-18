<template>
  <nav v-if="isAuthenticated">
    <router-link to="/">Job Tracker</router-link> |
    <router-link to="/about">About</router-link>
    <a href="#" @click.prevent="logout" style="margin-left: 15px">Logout</a>
  </nav>
  <router-view />
</template>

<script setup>
import { useRouter } from "vue-router";
import { isAuthenticated, clearToken } from "@/api/auth.js";

const router = useRouter();

// 登出逻辑
const logout = () => {
  clearToken(); // 清除 token 和状态
  router.push({ name: "Login" }); // 重定向到登录页
};
</script>
