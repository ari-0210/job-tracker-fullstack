<template>
  <nav v-if="authStore.isAuthenticated">
    <router-link to="/">DDL Tracker</router-link> |
    <router-link to="/about">About</router-link>
    <a href="#" @click.prevent="logout" style="margin-left: 15px">Logout</a>
  </nav>
  <router-view />
</template>

<script setup>
import { useRouter } from "vue-router";
import { useAuthStore } from "./stores/auth";

const authStore = useAuthStore();
const router = useRouter();

// learn;登出逻辑
const logout = () => {
  authStore.logout();
  router.push({ name: "Login" });
};
</script>
