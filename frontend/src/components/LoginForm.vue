<template>
  <div class="login-container">
    <n-card
      title="Login to Job Tracker"
      style="max-width: 400px; margin: auto; margin-top: 100px"
    >
      <n-form @submit.prevent="handleLogin">
        <n-form-item-row label="Username">
          <n-input v-model:value="username" placeholder="Enter your username" />
        </n-form-item-row>
        <n-form-item-row label="Password">
          <n-input
            v-model:value="password"
            type="password"
            show-password-on="mousedown"
            placeholder="Enter your password"
            @keyup.enter="handleLogin"
          />
        </n-form-item-row>
        <n-button
          type="primary"
          block
          attr-type="submit"
          :loading="isLoading"
          @click="handleLogin"
        >
          Login
        </n-button>
        <n-alert
          v-if="errorMessage"
          title="Login Failed"
          type="error"
          style="margin-top: 15px"
        >
          {{ errorMessage }}
        </n-alert>
      </n-form>
      <n-divider> Don't have an account? </n-divider>
      <n-button block @click="openRegisterModal"> Create an Account </n-button>
    </n-card>
    <n-modal v-model:show="showRegisterModal">
      <n-card
        style="width: 600px"
        title="Create New Account"
        :bordered="false"
        size="huge"
        role="dialog"
        aria-modal="true"
      >
        <n-form @submit.prevent="handleRegister">
          <n-form-item-row label="Username">
            <n-input
              v-model:value="newUser.username"
              placeholder="Choose a username"
            />
          </n-form-item-row>
          <n-form-item-row label="Password">
            <n-input
              v-model:value="newUser.password"
              type="password"
              placeholder="Choose a password"
            />
          </n-form-item-row>
          <n-form-item-row label="Confirm Password">
            <n-input
              v-model:value="newUser.confirmPassword"
              type="password"
              placeholder="Confirm your password"
            />
          </n-form-item-row>
          <n-alert
            v-if="registerError"
            title="Registration Failed"
            type="error"
            closable
            @close="registerError = ''"
          >
            {{ registerError }}
          </n-alert>
        </n-form>
        <template #footer>
          <n-button @click="showRegisterModal = false">Cancel</n-button>
          <n-button
            type="primary"
            :loading="isRegistering"
            @click="handleRegister"
            >Register</n-button
          >
        </template>
      </n-card>
    </n-modal>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { loginUser, setToken, registerUser } from "@/api/auth";

const username = ref("");
const password = ref("");
const isLoading = ref(false);
const errorMessage = ref("");

const router = useRouter();

const showRegisterModal = ref(false);
const isRegistering = ref(false);
const registerError = ref("");
const newUser = ref({
  username: "",
  password: "",
  confirmPassword: "",
  roles: "ROLE_USER", // 默认给新用户 "ROLE_USER"
});

const handleRegister = async () => {
  if (!newUser.value.username || !newUser.value.password) {
    registerError.value = "Username and password are required.";
    return;
  }
  if (newUser.value.password !== newUser.value.confirmPassword) {
    registerError.value = "Passwords do not match.";
    return;
  }

  isRegistering.value = true;
  registerError.value = "";

  try {
    const payload = {
      username: newUser.value.username,
      password: newUser.value.password,
      roles: newUser.value.roles,
    };
    await registerUser(payload);

    // 注册成功
    alert("Registration successful! You can now log in.");
    showRegisterModal.value = false;
  } catch (error) {
    console.error("Registration failed:", error);
    if (
      error.response &&
      error.response.data &&
      (error.response.data.error || error.response.data.message)
    ) {
      registerError.value =
        error.response.data.error || error.response.data.message;
    } else {
      registerError.value = "An unknown error occurred.";
    }
  } finally {
    isRegistering.value = false;
  }
};

const handleLogin = async () => {
  if (!username.value || !password.value) {
    errorMessage.value = "Username and password are required.";
    return;
  }

  isLoading.value = true;
  errorMessage.value = ""; // 清除之前的错误信息

  try {
    const response = await loginUser({
      username: username.value,
      password: password.value,
    });

    const token = response.data.token;

    if (token) {
      console.log(
        "Login successful, token received. Setting token and attempting redirect..."
      );
      //  存储 JWT
      setToken(token);

      // 跳转到受保护的页面
      router.push({ name: "home" });
    } else {
      errorMessage.value = "Login successful, but no token received.";
    }
  } catch (error) {
    console.error("Login failed:", error);
    if (error.response && error.response.data) {
      // 假设后端错误响应体是 { message: "错误信息" } 或直接是字符串
      errorMessage.value =
        typeof error.response.data === "string"
          ? error.response.data
          : error.response.data.message ||
            "Invalid credentials or server error.";
    } else if (error.request) {
      errorMessage.value =
        "No response from server. Please check your network.";
    } else {
      errorMessage.value = "Login request failed. Please try again.";
    }
  } finally {
    isLoading.value = false;
  }
};

const openRegisterModal = () => {
  // 重置表单
  registerError.value = "";
  newUser.value = {
    username: "",
    password: "",
    confirmPassword: "",
    roles: "ROLE_USER",
  };
  showRegisterModal.value = true;
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 100px);
}
</style>
