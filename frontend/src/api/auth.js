import { ref } from 'vue';
import apiClient from '@/api/axiosInstance'; 

const initialToken = localStorage.getItem('authToken');
export const isAuthenticated = ref(!!initialToken);
export const token = ref(initialToken);

// 修改 Axios 请求头
const updateAuthHeader = (newToken) => {
  if (newToken) {
    apiClient.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
  } else {
    delete apiClient.defaults.headers.common['Authorization'];
  }
};

// 登录时调用的函数
export function setToken(newToken) {
  token.value = newToken;
  isAuthenticated.value = true;
  localStorage.setItem('authToken', newToken);
  //直接在这里同步设置请求头，而不是依赖 watchEffect
  updateAuthHeader(newToken);
}

// 登出时调用的函数
export function clearToken() {
  token.value = null;
  isAuthenticated.value = false;
  localStorage.removeItem('authToken');
  // 直接在这里同步清除请求头
  updateAuthHeader(null);
}

export const registerUser = (credentials) => {
  return apiClient.post('/auth/register', credentials);
};

export const loginUser = (credentials) => {

return apiClient.post('/auth/login', credentials); 

}

// 应用加载时，根据 localStorage 中的初始 token 设置一次请求头
updateAuthHeader(initialToken);