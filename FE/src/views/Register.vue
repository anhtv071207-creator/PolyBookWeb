<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="breadcrumb">
        <router-link to="/">Trang chủ</router-link> 
        <span class="divider">></span> 
        <span class="current">Đăng ký</span>
      </div>

      <div class="tabs">
        <router-link to="/login" class="tab tab-link">Đăng nhập</router-link>
        <button class="tab active">Đăng ký</button>
      </div>

      <form @submit.prevent="register" class="register-form">
        <div class="form-group">
          <label>Họ tên</label>
          <input
            type="text"
            placeholder="Nhập họ và tên"
            v-model="hoTen"
            :class="{ 'input-error': errors.hoTen }"
          />
          <p v-if="errors.hoTen" class="error-text">{{ errors.hoTen }}</p>
        </div>

        <div class="form-group">
          <label>Email</label>
          <input
            type="email"
            placeholder="example@gmail.com"
            v-model="email"
            :class="{ 'input-error': errors.email }"
          />
          <p v-if="errors.email" class="error-text">{{ errors.email }}</p>
        </div>

        <div class="form-group">
          <label>Số điện thoại</label>
          <input
            type="tel"
            placeholder="Nhập số điện thoại"
            v-model="phone"
            :class="{ 'input-error': errors.phone }"
          />
          <p v-if="errors.phone" class="error-text">{{ errors.phone }}</p>
        </div>

        <div class="form-group">
          <label>Mật khẩu</label>
          <input
            type="password"
            placeholder="Tối thiểu 6 ký tự"
            v-model="password"
            :class="{ 'input-error': errors.password }"
          />
          <p v-if="errors.password" class="error-text">{{ errors.password }}</p>
        </div>

        <div class="form-group">
          <label>Nhập lại mật khẩu</label>
          <input
            type="password"
            placeholder="Xác nhận lại mật khẩu"
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

        <button class="btn-submit" type="submit">Đăng ký ngay</button>
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
const errors = ref({});
const serverError = ref("");

const validate = () => {
  errors.value = {};
  serverError.value = "";

  if (!hoTen.value) {
    errors.value.hoTen = "Họ tên không được để trống";
  }

  if (!email.value) {
    errors.value.email = "Email không được để trống";
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    errors.value.email = "Email không hợp lệ";
  }

  if (!phone.value) {
    errors.value.phone = "Số điện thoại không được để trống";
  }

  if (!password.value) {
    errors.value.password = "Mật khẩu không được để trống";
  } else if (password.value.length < 6) {
    errors.value.password = "Mật khẩu phải ít nhất 6 ký tự";
  }

  if (!confirmPassword.value) {
    errors.value.confirmPassword = "Vui lòng nhập lại mật khẩu";
  } else if (password.value !== confirmPassword.value) {
    errors.value.confirmPassword = "Mật khẩu không khớp";
  }

  return Object.keys(errors.value).length === 0;
};

const register = async () => {
  if (!validate()) return;

  try {
    await api.post("/auth/register", {
      hoTen: hoTen.value,
      email: email.value,
      phone: phone.value,
      password: password.value,
    });

    alert("Đăng ký thành công, mời bạn đăng nhập");
    router.push("/login");
  } catch (err) {
    const code = err.response?.data?.code;
    if (code === "EMAIL_EXISTS") {
      errors.value.email = "Email này đã được sử dụng";
      return;
    }
    serverError.value = err.response?.data?.message || "Đăng ký thất bại, vui lòng thử lại";
  }
};
</script>

<style scoped>
/* Bao phủ toàn bộ màn hình và căn giữa */
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

/* Thẻ card trắng chứa form */
.login-card {
  width: 100%;
  max-width: 480px;
  background: #ffffff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
}

/* Điều hướng nhỏ ở trên cùng */
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
  font-weight: 500;
}

/* Tabs chuyển Đăng nhập / Đăng ký */
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
  display: block;
}
.tab.active {
  color: #1890ff;
  border-bottom-color: #1890ff;
}

/* Các nhóm nhập liệu */
.form-group {
  margin-bottom: 20px;
  text-align: left;
}
.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #262626;
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

/* Nút bấm đăng ký */
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
  margin-top: 10px;
  transition: background 0.3s;
}
.btn-submit:hover {
  background: #40a9ff;
}

/* Xử lý lỗi */
.input-error {
  border-color: #ff4d4f !important;
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
</style>