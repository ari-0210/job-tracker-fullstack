import { createRouter, createWebHashHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginForm from "../components/LoginForm.vue";
import { useAuthStore } from "@/stores/auth";
/**
 * 全局前端路由映射配置表.
 * <p>采用哈希路由模式(WebHashHistory)，规避 file:// 协议下绝对路径白屏溃败风险。</p>
 * <p>核心路由集成了 Webpack Chunk 按需懒加载优化，大幅削减首屏加载的体积损耗。</p>
 * * @author Ari
 */
const routes = [
  {
    path: "/",
    name: "home",
    component: HomeView,
    meta: { requiresAuth: true }, // 安全审计标志：本路由必须经过安全守卫强认证放行
  },
  {
    path: "/dashboard",
    name: "dashboard",
    // 路由懒加载：只有用户点击时才会异步加载该组件，实现高水准的性能优化
    component: () => import("@/views/Dashboard.vue"),
    meta: {
      requiresAuth: true,
      title: "仪表盘",
    },
  },
  {
    path: "/login",
    name: "Login",
    component: LoginForm,
  },
  {
    path: "/about",
    name: "about",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});
/**
 * 全局前置路由守卫（全系统前端门禁系统）.
 * <p>高抗穿透防线：在动态视图流转前执行强拦截。联动 Pinia 状态树进行双向安全审计：</p>
 * <p>1. 若目标路由携带 requiresAuth 且未登录，强制就地熔断并驱逐至 /login 页面。</p>
 * <p>2. 若用户已处于登录态却企图更改 URL 回头访问 /login，强行修正其路由重定向至主页。</p>
 *
 * @param {RouteLocationNormalized} to - 即将要进入的目标路由上下文对象
 * @returns {RouteLocationRaw | void} 返回重定向目标或直接放行
 */
router.beforeEach((to) => {
  
  const authStore = useAuthStore();
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return { name: "Login" };
  }
  if (to.name === "Login" && authStore.isAuthenticated) {
    return { name: "home" };
  }
});

export default router;
