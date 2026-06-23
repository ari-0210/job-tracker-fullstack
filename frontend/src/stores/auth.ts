import { defineStore } from "pinia";
import { ref, computed } from "vue";
/**
 * 全局用户无状态安全认证与凭证持久化中心状态仓库 (Pinia Store).
 * <p>承载系统的 JWT 会话状态流转，内置防浏览器 F5 强刷掉线的双向数据桥接同步机制.</p>
 * * @author Ari
 */
export const useAuthStore = defineStore("auth", () => {
  // 核心防御：就地捕获本地外壳存储层缓存，防止页面瞬时刷新导致状态归零崩溃
  const token = ref(localStorage.getItem("token") || "");
  /** 当前成功鉴权登录的系统用户物理元数据载荷 */
  const user = ref(null);

  /** * 提纯衍生的响应式计算属性：判断当前访客是否具备合法的在线认证身份
   * @type {ComputedRef<boolean>} 利用双感叹号 !! 强行执行真假值类型转换
   */
  const isAuthenticated = computed(() => !!token.value);

  /**
   * 接收后端安全通过后下发的全新令牌，并执行本地磁盘异步持久化加冕.
   * @param {string} newToken - 后端 Spring Boot 强认证后签发的 Bearer 访问密文
   */
  function login(newToken: any) {
    token.value = newToken;
    localStorage.setItem("token", newToken);
  }
  /**
   * 全盘状态自清洗注销函数.
   * <p>清除内存级别的 Token 和 User 上下文，并彻底洗净物理磁盘 LocalStorage 的持久化残留，切断会话越权可能。</p>
   */
  function logout() {
    token.value = "";
    user.value = null;
    localStorage.removeItem("token");
  }

  return { token, user, isAuthenticated, login, logout };
});
