<template>
  <n-layout-header
    v-if="authStore.isAuthenticated"
    bordered
    class="p-4 flex justify-between items-center"
  >
    <n-menu mode="horizontal" :options="menuOptions" />
    <n-button @click.prevent="logout" quaternary type="error">Logout</n-button>
  </n-layout-header>
  <router-view />
</template>

<script setup lang="ts">
import { useRouter, RouterLink } from "vue-router";
import { h } from "vue";
import { useAuthStore } from "./stores/auth";
import type { MenuOption } from "naive-ui";
const authStore = useAuthStore();
const router = useRouter();

const menuOptions: MenuOption[] = [
  {
    label: () =>
      h(RouterLink, { to: { name: "home" } }, { default: () => "DDL Tracker" }),
    key: "home",
  },
  {
    label: () =>
      h(RouterLink, { to: { name: "dashboard" } }, { default: () => "仪表盘" }),
    key: "dashboard",
  },
  {
    label: () =>
      h(RouterLink, { to: { name: "about" } }, { default: () => "About" }),
    key: "about",
  },
];
// learn;登出逻辑
const logout = () => {
  authStore.logout();
  router.push({ name: "Login" });
};
</script>
