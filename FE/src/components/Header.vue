<script setup>
import { computed } from "vue"
import { useRouter } from "vue-router"
import { useAuthStore } from "@/stores/auth"

const auth = useAuthStore()
const router = useRouter()

const isLoggedIn = computed(() => auth.isLoggedIn)
const role = computed(() => auth.role)
const userName = computed(() => auth.user?.name || "ho_ten")

const isAdmin = computed(() => role.value === "ADMIN")
const isStaff = computed(() => role.value === "STAFF")
const isUser = computed(() => role.value === "USER")

const logout = () => {
  auth.logout()
  router.push("/login")
}
</script>

<template>
  <header class="header bg-white shadow-sm">
    <!-- TOP HEADER -->
    <div class="container d-flex align-items-center py-2">
      <!-- Logo -->
      <router-link
        to="/"
        class="d-flex align-items-center gap-2 text-decoration-none"
      >
        <img src="/images/logo.png" alt="Logo" class="logo" />
      </router-link>

      <!-- Search -->
      <div class="search-box flex-grow-1 mx-4">
        <div class="input-group">
          <input type="text" class="form-control" placeholder="Tìm sách" />
          <span class="input-group-text bg-light">
            <i class="bi bi-search"></i>
          </span>
        </div>
      </div>

      <!-- RIGHT ACTIONS -->
      <div class="d-flex align-items-center gap-3">
        <!-- Category -->
        <div class="dropdown">
          <button
            class="btn btn-light dropdown-toggle"
            data-bs-toggle="dropdown"
          >
            Danh mục
          </button>
          <ul class="dropdown-menu">
            <li><a class="dropdown-item">Tiểu thuyết</a></li>
            <li><a class="dropdown-item">Kỹ năng</a></li>
            <li><a class="dropdown-item">Truyện tranh</a></li>
          </ul>
        </div>

        <!-- Cart -->
        <button class="btn btn-outline-danger">
          <i class="bi bi-cart"></i>
          Giỏ hàng
        </button>

        <!-- GUEST -->
        <template v-if="!isLoggedIn">
          <router-link to="/register" class="btn btn-outline-primary">
            Sign Up
          </router-link>
          <router-link to="/login" class="btn btn-primary">
            Sign In
          </router-link>
        </template>

        <!-- USER / ADMIN / STAFF -->
        <template v-else>
          <div class="dropdown">
            <button
              class="btn btn-dark dropdown-toggle"
              data-bs-toggle="dropdown"
            >
              <i class="bi bi-person-circle me-1"></i>
              {{ userName }}
            </button>

            <ul class="dropdown-menu dropdown-menu-end">
              <li v-if="isUser">
                <a class="dropdown-item">Tài khoản của tôi</a>
              </li>

              <li v-if="isAdmin || isStaff">
                <router-link class="dropdown-item" to="/admin">
                  Trang quản trị
                </router-link>
              </li>

              <li><hr class="dropdown-divider" /></li>
              <li>
                <button class="dropdown-item text-danger" @click="logout">
                  Đăng xuất
                </button>
              </li>
            </ul>
          </div>
        </template>
      </div>
    </div>

    <!-- ADMIN / STAFF CONTROL BAR -->
    <div
      v-if="isAdmin || isStaff"
      class="admin-bar border-top bg-light"
    >
      <div class="container d-flex justify-content-center gap-4 py-2">
        <div class="admin-item">📚 Quản lý sản phẩm</div>
        <div class="admin-item">📦 Quản lý đơn hàng</div>
        <div class="admin-item">📊 Thống kê</div>
        <div class="admin-item">💰 Khuyến mãi</div>

        <div v-if="isAdmin" class="admin-item">
          👤 Quản lý tài khoản
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
.header {
  width: 100%;
}

/* Logo */
.logo {
  width: 42px;
  height: 42px;
  object-fit: contain;
  cursor: pointer;
}

/* Search */
.search-box input {
  border-radius: 20px 0 0 20px;
}
.search-box .input-group-text {
  border-radius: 0 20px 20px 0;
}

/* Buttons */
.btn {
  white-space: nowrap;
}
.btn-primary {
  background-color: var(--bs-primary);
  color: #fff;
  border-color: var(--bs-primary);
}
.btn-primary:hover {
  background-color: #fff;
  color: var(--bs-primary);
  border-color: var(--bs-primary);
}

/* Admin bar */
.admin-bar {
  font-size: 14px;
}
.admin-item {
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 6px;
}
.admin-item:hover {
  background: #e9ecef;
}
</style>
