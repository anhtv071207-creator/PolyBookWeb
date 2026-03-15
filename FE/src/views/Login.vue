<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="breadcrumb">
        <router-link to="/">Trang chủ</router-link>
        <span class="divider">></span>
        <span class="current">Đăng nhập</span>
      </div>

      <div class="tabs">
        <button class="tab active">Đăng nhập</button>
        <router-link to="/register" class="tab tab-link">Đăng ký</router-link>
      </div>

      <form
        v-if="activeTab === 'login'"
        @submit.prevent="login"
        class="login-form"
      >
        <div class="form-group">
          <label>Email</label>
          <input
            type="email"
            v-model="email"
            placeholder="example@gmail.com"
            :class="{ 'input-error': errors.email }"
          />
          <p v-if="errors.email" class="error-text">{{ errors.email }}</p>
        </div>

        <div class="form-group">
          <label>Mật khẩu</label>
          <input
            type="password"
            v-model="password"
            placeholder="Nhập mật khẩu"
            :class="{ 'input-error': errors.password }"
          />
          <p v-if="errors.password" class="error-text">{{ errors.password }}</p>
        </div>

        <div class="forgot-link">
          <a href="#">Quên mật khẩu?</a>
        </div>

        <p v-if="serverError" class="server-error">
          {{ serverError }}
        </p>

        <button class="btn-submit" type="submit">Đăng nhập</button>
      </form>
    </div>
  </div>

  <Transition name="fade">
    <div v-if="showLockPopup" class="overlay">
      <div class="lock-popup">
        <div class="lock-icon">🔒</div>
        <h3>Tài khoản đã bị khóa</h3>
        <p>Vui lòng liên hệ quản trị viên để được hỗ trợ mở khóa.</p>
        <button @click="showLockPopup = false">Đóng</button>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import api from "@/services/api";
import { useAuthStore } from "@/stores/auth";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();

const errors = ref({});
const serverError = ref("");
const showLockPopup = ref(false);

const router = useRouter();
const authStore = useAuthStore();

const email = ref("");
const password = ref("");
const activeTab = ref("login");

const validate = () => {
  errors.value = {};
  serverError.value = "";

  if (!email.value) {
    errors.value.email = "Email không được để trống";
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    errors.value.email = "Email không hợp lệ";
  }

  if (!password.value) {
    errors.value.password = "Mật khẩu không được để trống";
  }

  return Object.keys(errors.value).length === 0;
};

const login = async () => {
  if (!validate()) return;

  try {
    const res = await api.post("/auth/login", {
      email: email.value,
      password: password.value,
    });

    authStore.setAuth({
      token: res.data.token,
      email: res.data.email,
      role: res.data.role,
      hoTen: res.data.hoTen,
    });
    api.defaults.headers.common.Authorization = `Bearer ${res.data.token}`;

    router.push("/");
  } catch (err) {
    const code = err.response?.data?.code;

    if (code === "ACCOUNT_LOCKED") {
      showLockPopup.value = true;
      return;
    }

    serverError.value =
      err.response?.data?.message ||
      "Đăng nhập thất bại. Vui lòng kiểm tra lại.";
  }
};
</script>

<style scoped>
/* Toàn bộ nền và căn giữa */
.login-wrapper {
  min-height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f5;
  padding: 20px;
  box-sizing: border-box;
}

/* Card trắng chứa form */
.login-card {
  width: 100%;
  max-width: 450px;
  background: #ffffff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
}

.breadcrumb {
  font-size: 13px;
  margin-bottom: 25px;
  color: #8c8c8c;
}
.breadcrumb a {
  text-decoration: none;
  color: #8c8c8c;
}
.breadcrumb .divider {
  margin: 0 8px;
}
.breadcrumb .current {
  color: #1890ff;
}

/* Tabs */
.tabs {
  display: flex;
  border-bottom: 2px solid #f0f0f0;
  margin-bottom: 30px;
}
.tab {
  flex: 1;
  padding: 12px 0;
  border: none;
  background: none;
  font-size: 16px;
  font-weight: 600;
  color: #595959;
  cursor: pointer;
  text-align: center;
  transition: all 0.3s;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
}
.tab-link {
  text-decoration: none;
}
.tab.active {
  color: #1890ff;
  border-bottom-color: #1890ff;
}

/* Form Groups */
.form-group {
  margin-bottom: 20px;
  text-align: left;
}
.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #262626;
  font-size: 14px;
}
.form-group input {
  width: 100%;
  height: 44px;
  padding: 0 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 15px;
  transition: all 0.3s;
  box-sizing: border-box;
}
.form-group input:focus {
  border-color: #40a9ff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.15);
}

.forgot-link {
  text-align: right;
  margin-bottom: 24px;
}
.forgot-link a {
  color: #1890ff;
  text-decoration: none;
  font-size: 14px;
}

.btn-submit {
  width: 100%;
  height: 46px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
}
.btn-submit:hover {
  background: #40a9ff;
}

/* Error styles */
.input-error {
  border-color: #ff4d4f !important;
  background-color: #fff1f0;
}
.error-text {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 5px;
}
.server-error {
  background: #fff1f0;
  border: 1px solid #ffa39e;
  color: #cf1322;
  padding: 10px;
  border-radius: 6px;
  margin-bottom: 20px;
  font-size: 14px;
}

/* Overlay & Popup */
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}
.lock-popup {
  background: #1f1f1f;
  color: #fff;
  width: 90%;
  max-width: 400px;
  padding: 30px;
  border-radius: 14px;
  text-align: center;
}
.lock-icon {
  font-size: 40px;
  margin-bottom: 10px;
}
.lock-popup h3 {
  margin: 10px 0;
}
.lock-popup p {
  color: #ccc;
  font-size: 14px;
  margin-bottom: 20px;
}
.lock-popup button {
  padding: 10px 25px;
  background: #ff4d4f;
  border: none;
  border-radius: 6px;
  color: white;
  font-weight: 600;
  cursor: pointer;
}

/* Animations */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ===== DARK MODE LOGIN ===== */

.dark .login-wrapper {
  background: #0f172a;
}

.dark .login-card {
  background: #1e293b;
  color: #e2e8f0;
  box-shadow: 0 10px 30px rgba(0,0,0,0.6);
}

.dark .breadcrumb {
  color: #94a3b8;
}

.dark .breadcrumb a {
  color: #94a3b8;
}

.dark .breadcrumb .current {
  color: #60a5fa;
}

.dark .tabs {
  border-bottom-color: #334155;
}

.dark .tab {
  color: #cbd5f5;
}

.dark .tab.active {
  color: #60a5fa;
  border-bottom-color: #60a5fa;
}

.dark .form-group label {
  color: #e2e8f0;
}

.dark .form-group input {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .form-group input:focus {
  border-color: #60a5fa;
  box-shadow: 0 0 0 2px rgba(96,165,250,0.25);
}

.dark .forgot-link a {
  color: #60a5fa;
}

.dark .btn-submit {
  background: #2563eb;
}

.dark .btn-submit:hover {
  background: #3b82f6;
}

.dark .input-error {
  background: #3b0d0d;
  border-color: #ef4444 !important;
}

.dark .server-error {
  background: #3b0d0d;
  border-color: #ef4444;
  color: #fecaca;
}

/* Popup vẫn giữ nền tối nhưng chỉnh cho đồng bộ */
.dark .lock-popup {
  background: #1e293b;
  color: #f1f5f9;
}

.dark .lock-popup p {
  color: #cbd5f5;
}
</style>
