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
        </div>

        <div class="form-group">
          <label>Mật khẩu</label>
          <input type="password" v-model="password" />
        </div>

        <div class="forgot">
          <a href="#">Quên mật khẩu?</a>
        </div>

        <button class="btn-login">Login</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import api from "@/services/api";
import { useAuthStore } from "@/stores/auth";

const router = useRouter();
const authStore = useAuthStore();

const email = ref("");
const password = ref("");
const activeTab = ref("login");

const login = async () => {
  try {
    const res = await api.post("/auth/login", {
      email: email.value,
      password: password.value,
    });

    // 🔥 LƯU TOÀN BỘ AUTH VÀO STORE
    authStore.setAuth({
      token: res.data.token,
      email: res.data.email,
      role: res.data.role,
      hoTen: res.data.hoTen,
    });

    // gắn token cho các request sau
    api.defaults.headers.common.Authorization = `Bearer ${res.data.token}`;

    router.push("/");
  } catch (err) {
    console.error(err);
    alert("Sai email hoặc mật khẩu");
  }
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
</style>
