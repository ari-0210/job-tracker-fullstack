<template>
  <n-config-provider>
    <!-- 必须包裹这个，下面的组件才能用 useMessage -->
    <n-message-provider>
      <n-dialog-provider>
        <n-notification-provider>
          <n-layout-header
            v-if="authStore.isAuthenticated"
            bordered
            class="p-4 flex justify-between items-center"
          >
            <n-menu mode="horizontal" :options="menuOptions" />
            <n-button @click="logout" quaternary type="error">Logout</n-button>
          </n-layout-header>
          <router-view />
          <!-- 这是一个专门用来挂载全局 API 的“隐形”组件 -->
          <ProviderContext />
        </n-notification-provider>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { useRouter, RouterLink } from "vue-router";
import { h } from "vue";
import { useAuthStore } from "./stores/auth";
import type { MenuOption } from "naive-ui";
import ProviderContext from "@/components/ProviderContext.vue";
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

const logout = () => {
  authStore.logout();
  router.push({ name: "Login" });
};
</script>
