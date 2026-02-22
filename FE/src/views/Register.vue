<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="breadcrumb">Trang chủ &gt; Đăng ký</div>

      <div class="tabs">
        <router-link to="/login" class="tab tab-link"> Login </router-link>
        <button class="tab active">Register</button>
      </div>

      <form @submit.prevent="register">
        <div class="form-group">
          <label>Họ tên</label>
          <input
            type="text"
            v-model="hoTen"
            :class="{ 'input-error': errors.hoTen }"
          />
          <p v-if="errors.hoTen" class="error-text">{{ errors.hoTen }}</p>
        </div>

        <div class="form-group">
          <label>Email</label>
          <input
            type="email"
            v-model="email"
            :class="{ 'input-error': errors.email }"
          />
          <p v-if="errors.email" class="error-text">{{ errors.email }}</p>
        </div>

        <div class="form-group">
          <label>Số điện thoại</label>
          <input
            type="tel"
            v-model="phone"
            :class="{ 'input-error': errors.phone }"
          />
          <p v-if="errors.phone" class="error-text">{{ errors.phone }}</p>
        </div>

        <div class="form-group">
          <label>Mật khẩu</label>
          <input
            type="password"
            v-model="password"
            :class="{ 'input-error': errors.password }"
          />
          <p v-if="errors.password" class="error-text">{{ errors.password }}</p>
        </div>

        <div class="form-group">
          <label>Nhập lại mật khẩu</label>
          <input
            type="password"
            v-model="confirmPassword"
            :class="{ 'input-error': errors.confirmPassword }"
          />
          <p v-if="errors.confirmPassword" class="error-text">
            {{ errors.confirmPassword }}
          </p>
        </div>

        <p v-if="serverError" class="server-error">
          {{ serverError }}
        </p>

        <button class="btn-login">Register</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import api from "@/services/api";

const router = useRouter();

const hoTen = ref("");
const email = ref("");
const phone = ref("");
const password = ref("");
const confirmPassword = ref("");


const register = async () => {
  if (!validate()) return

  try {
    await api.post('/auth/register', {
      hoTen: hoTen.value,
      email: email.value,
      phone: phone.value,
      password: password.value
    })

    alert('Đăng ký thành công, mời đăng nhập')
    router.push('/login')

  } catch (err) {
    const code = err.response?.data?.code

    if (code === 'EMAIL_EXISTS') {
      errors.value.email = 'Email đã tồn tại'
      return
    }

    serverError.value = err.response?.data?.message || 'Đăng ký thất bại'
  }
}


const errors = ref({})
const serverError = ref('')

const validate = () => {
  errors.value = {}
  serverError.value = ''

  if (!hoTen.value) {
    errors.value.hoTen = 'Họ tên không được để trống'
  }

  if (!email.value) {
    errors.value.email = 'Email không được để trống'
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    errors.value.email = 'Email không hợp lệ'
  }

  if (!phone.value) {
    errors.value.phone = 'Số điện thoại không được để trống'
  }

  if (!password.value) {
    errors.value.password = 'Mật khẩu không được để trống'
  } else if (password.value.length < 6) {
    errors.value.password = 'Mật khẩu phải ít nhất 6 ký tự'
  }

  if (!confirmPassword.value) {
    errors.value.confirmPassword = 'Vui lòng nhập lại mật khẩu'
  } else if (password.value !== confirmPassword.value) {
    errors.value.confirmPassword = 'Mật khẩu không khớp'
  }

  return Object.keys(errors.value).length === 0
}

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
.tab-link {
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
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

</style>
