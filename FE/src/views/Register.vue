<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="breadcrumb">
        Trang chủ &gt; Đăng ký
      </div>

      <div class="tabs">
        <router-link to="/login" class="tab tab-link">
          Login
        </router-link>
        <button class="tab active">
          Register
        </button>
      </div>

      <form @submit.prevent="register">
        <div class="form-group">
          <label>Họ tên</label>
          <input type="text" v-model="hoTen" required />
        </div>

        <div class="form-group">
          <label>Email</label>
          <input type="email" v-model="email" required />
        </div>

        <div class="form-group">
          <label>Số điện thoại</label>
          <input type="tel" v-model="phone" required />
        </div>

        <div class="form-group">
          <label>Mật khẩu</label>
          <input type="password" v-model="password" required />
        </div>

        <div class="form-group">
          <label>Nhập lại mật khẩu</label>
          <input type="password" v-model="confirmPassword" required />
        </div>

        <button class="btn-login">Register</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()

const hoTen = ref('')
const email = ref('')
const phone = ref('')
const password = ref('')
const confirmPassword = ref('')

const register = async () => {
  if (password.value !== confirmPassword.value) {
    alert('Mật khẩu không khớp')
    return
  }

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
    console.error(err)
    alert('Đăng ký thất bại')
  }
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

</style>
