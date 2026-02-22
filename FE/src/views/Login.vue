<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="breadcrumb">Trang chủ &gt; Đăng nhập</div>

      <div class="tabs">
        <button class="tab active">Login</button>

        <router-link to="/register" class="tab tab-link">
          Register
        </router-link>
      </div>

      <form v-if="activeTab === 'login'" @submit.prevent="login">
        <div class="form-group">
          <label>Email</label>
          <input type="email" v-model="email" />
          <p v-if="errors.email" class="error-text">{{ errors.email }}</p>
        </div>

        <div class="form-group">
          <label>Mật khẩu</label>
          <input type="password" v-model="password" />
          <p v-if="errors.password" class="error-text">
            {{ errors.password }}
          </p>
        </div>

        <div class="forgot">
          <a href="#">Quên mật khẩu?</a>
        </div>
        <p v-if="serverError" class="server-error">
          {{ serverError }}
        </p>
        <button class="btn-login">Login</button>
      </form>
    </div>
  </div>
  <div v-if="showLockPopup" class="overlay">
    <div class="lock-popup">
      <div class="lock-icon">🔒</div>
      <h3>Tài khoản đã bị khóa</h3>
      <p>Vui lòng liên hệ quản trị viên để được hỗ trợ.</p>
      <button @click="showLockPopup = false">Đóng</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import api from "@/services/api";
import { useAuthStore } from "@/stores/auth";
const errors = ref({});
const serverError = ref("");
const showLockPopup = ref(false);

const router = useRouter();
const authStore = useAuthStore();

const email = ref("");
const password = ref("");
const activeTab = ref("login");

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

    serverError.value = err.response?.data?.message || "Đăng nhập thất bại";
  }
};

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
</script>

<style scoped>
.login-wrapper {
  width: 100vw;
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 100%;
  max-width: 520px;
  background: #fff;
  padding: 24px;
  border-radius: 6px;
  box-shadow: 0 0 0 1px #eee;
}

.breadcrumb {
  font-size: 14px;
  color: #333;
  margin-bottom: 16px;
}

.tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.tab {
  flex: 1;
  height: 44px;
  border: 1px solid #1890ff;
  background: #fff;
  color: #1890ff;
  border-radius: 6px;
  font-size: 18px;
  font-weight: 600;
}

.tab.active {
  background: #1890ff;
  color: #fff;
}

.form-group {
  margin-bottom: 16px;
}

.form-group input {
  width: 100%;
  height: 36px;
  padding: 0 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.forgot {
  text-align: right;
  margin-bottom: 16px;
}

.btn-login {
  width: 100%;
  height: 40px;
  background: #1890ff;
  color: #fff;
  border: none;
  border-radius: 4px;
}
.input-error {
  border-color: #ff4d4f !important;
  background: #fff1f0;
}

.error-text {
  color: #ff4d4f;
  font-size: 13px;
  margin-top: 4px;
}

.server-error {
  background: #fff1f0;
  border: 1px solid #ff4d4f;
  color: #ff4d4f;
  padding: 10px;
  border-radius: 6px;
  font-size: 14px;
  margin-bottom: 16px;
}

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
  width: 400px;
  padding: 30px;
  border-radius: 14px;
  text-align: center;
  animation: fadeInScale 0.25s ease;
}

.lock-popup h3 {
  margin-top: 10px;
  margin-bottom: 10px;
}

.lock-popup p {
  font-size: 14px;
  color: #ccc;
  margin-bottom: 20px;
}

.lock-popup button {
  padding: 10px 20px;
  background: #ff4d4f;
  border: none;
  border-radius: 6px;
  color: #fff;
  cursor: pointer;
}

.lock-popup button:hover {
  background: #d9363e;
}

.lock-icon {
  font-size: 40px;
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.85);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
