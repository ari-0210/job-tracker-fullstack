import { createRouter, createWebHashHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginForm from '../components/LoginForm.vue';
import { isAuthenticated } from '@/api/auth'; 

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { requiresAuth: true } // 需要认证
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginForm,
  },
  {
    path: '/about',
    name: 'about',
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue'),
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 添加全局前置导航守卫
router.beforeEach((to, from, next) => {
  const needsAuth = to.meta.requiresAuth;

  // 如果目标路由需要认证，但用户未登录
  if (needsAuth && !isAuthenticated.value) {
    // 将用户重定向到登录页
    next({ name: 'Login' });
  } 
  // 如果用户已登录，但尝试访问登录页
  else if (to.name === 'Login' && isAuthenticated.value) {
    // 将用户重定向到主页
    next({ name: 'home' });
  } 
  // 其他情况，正常放行
  else {
    next();
  }
});


export default router
