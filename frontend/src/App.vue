<template>
  <n-config-provider>
    <!-- learn;必须包裹这个，下面的组件才能用 useMessage -->
    <n-message-provider>
      <n-dialog-provider>
        <n-notification-provider>
          <div class="min-h-screen flex flex-col justify-between">
            <div>
              <n-layout-header
                v-if="authStore.isAuthenticated"
                bordered
                class="p-4 flex justify-between items-center"
              >
                <n-menu mode="horizontal" :options="menuOptions" />
                <n-button @click="logout" quaternary type="error"
                  >Logout</n-button
                >
              </n-layout-header>
              <router-view />
            </div>

            <!-- learn;全局合规页脚 Footer -->
            <footer
              class="w-full py-4 text-center text-xs text-gray-500 border-t border-gray-200 mt-8"
            >
              <div class="flex items-center justify-center space-x-4">
                <!-- 工信部 ICP 备案号 -->
                <a
                  href="https://beian.miit.gov.cn/"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="hover:underline hover:text-gray-700 transition-colors"
                >
                  浙ICP备2026050487号
                </a>
              </div>
              <p class="mt-1 text-gray-400">
                © 2026 Deadline Tracker. All rights reserved.
              </p>
            </footer>
          </div>
          <!-- learn;这是一个专门用来挂载全局 API 的“隐形”组件 -->
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
// learn;登出逻辑
const logout = () => {
  authStore.logout();
  router.push({ name: "Login" });
};
</script>
