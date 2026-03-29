<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore(); // Centralized theme management
const auth = useAuthStore();
const router = useRouter();

const parentCategories = ref([]);
const childCategories = ref([]);
const selectedParentId = ref(null);
const keyword = ref("");

// Auth Computed
const isLoggedIn = computed(() => auth.isLoggedIn);
const userName = computed(() => auth.user?.name || "Khách");
const isAdmin = computed(() => auth.role === "ADMIN");
const isStaff = computed(() => auth.role === "STAFF");
const isUser = computed(() => auth.role === "USER");

const logout = () => {
  auth.logout();
  router.push("/login");
};

const fetchParentCategories = async () => {
  try {
    const res = await api.get("http://localhost:8080/api/categories/root");
    parentCategories.value = res.data;
    if (res.data.length > 0) {
      fetchChildCategories(res.data[0].id);
    }
  } catch (error) {
    console.error("Failed to load categories", error);
  }
};

const fetchChildCategories = async (parentId) => {
  if (selectedParentId.value === parentId) return;
  selectedParentId.value = parentId;
  try {
    const res = await api.get(`http://localhost:8080/api/categories/parent/${parentId}`);
    childCategories.value = res.data;
  } catch (error) {
    childCategories.value = [];
  }
};

const searchBooks = () => {
  const query = keyword.value.trim();
  if (!query) return;
  router.push({ path: "/search", query: { keyword: query } });
};

const goToCategory = (id) => {
  router.push(`/category/${id}`);
};

onMounted(() => {
  fetchParentCategories();
});
</script>

<template>
  <header class="app-header">
    <div class="container header-inner">
      <router-link to="/" class="logo-area">
        <img src="/images/logo.png" class="logo" />
        <span class="brand">Polybook</span>
      </router-link>

      <div class="search-box">
        <input
          v-model="keyword"
          @keyup.enter="searchBooks"
          placeholder="Tìm kiếm sách..."
        />
        <button @click="searchBooks">
          <i class="bi bi-search"></i>
        </button>
      </div>

      <div class="header-actions">
        <router-link v-if="!isAdmin && !isStaff" to="/cart" class="action-btn">
          <i class="bi bi-cart"></i>
          <span> Giỏ hàng</span>
        </router-link>

        <router-link
          v-if="!isAdmin && !isStaff"
          to="/orders/view"
          class="action-btn"
        >
          <i class="bi bi-receipt"></i>
          <span> Đơn hàng</span>
        </router-link>
        <button class="action-btn" @click="theme.toggleTheme()">
          <i v-if="theme.darkMode" class="bi bi-sun"></i>
          <i v-else class="bi bi-moon"></i>
        </button>
        <div class="account-area">
          <template v-if="!isLoggedIn">
            <router-link to="/login" class="login-btn"> Đăng nhập </router-link>
          </template>

          <template v-else>
            <div class="dropdown">
              <button class="user-btn" data-bs-toggle="dropdown">
                <i class="bi bi-person-circle"></i>
                {{ userName }}
              </button>

              <ul class="dropdown-menu dropdown-menu-end">
                <li v-if="isUser || isStaff">
                  <router-link class="dropdown-item" to="/info">
                    Thông tin
                  </router-link>
                </li>

                <li v-if="isAdmin || isStaff">
                  <router-link class="dropdown-item" to="/managements">
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
    </div>

    <!-- CATEGORY BAR -->

    <div class="category-bar">
      <div class="container">
        <div class="category-dropdown">
          <button class="category-trigger" data-bs-toggle="dropdown">
            <i class="bi bi-list"></i>
            Danh mục
          </button>

          <div class="dropdown-menu mega-menu">
            <div class="mega-left">
              <div
                v-for="parent in parentCategories"
                :key="parent.id"
                class="mega-parent"
                @mouseenter="fetchChildCategories(parent.id)"
                @click="goToCategory(parent.id)"
              >
                {{ parent.tenDanhMuc }}
              </div>
            </div>

            <div class="mega-right">
              <div
                v-for="child in childCategories"
                :key="child.id"
                class="mega-child"
                @click="goToCategory(child.id)"
              >
                {{ child.tenDanhMuc }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
/* ===== BASE STYLES ===== */
.app-header {
  background: #ffffff;
  border-bottom: 1px solid #e2e8f0;
  position: sticky;
  top: 0;
  z-index: 1000;
  transition: all 0.3s ease;
}

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 12px 0;
}

/* LOGO */
.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
}

.logo {
  height: 42px;
  object-fit: contain;
}

.brand {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
  letter-spacing: -0.5px;
}

/* SEARCH BOX */
.search-box {
  flex: 1;
  max-width: 550px;
  display: flex;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.search-box input {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid #cbd5e1;
  border-right: none;
  border-radius: 8px 0 0 8px;
  outline: none;
  transition: border-color 0.2s;
}

.search-box input:focus {
  border-color: #2563eb;
}

.search-box button {
  border: none;
  background: #2563eb;
  color: white;
  padding: 0 20px;
  border-radius: 0 8px 8px 0;
  cursor: pointer;
  transition: background 0.2s;
}

.search-box button:hover {
  background: #1d4ed8;
}

/* ACTIONS */
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 13px;
  color: #475569;
  text-decoration: none;
  background: none;
  border: none;
  padding: 8px;
  border-radius: 8px;
  transition: 0.2s;
  cursor: pointer;
}

.action-btn i {
  font-size: 20px;
  margin-bottom: 2px;
}

.action-btn:hover {
  background: #f1f5f9;
  color: #2563eb;
}

/* ACCOUNT & LOGIN */
.login-btn {
  background: #2563eb;
  color: white;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  text-decoration: none;
  transition: 0.2s;
}

.user-btn {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  padding: 8px 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  color: #1e293b;
  cursor: pointer;
}

/* ===== CATEGORY BAR & MEGA MENU ===== */
.category-bar {
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.category-trigger {
  border: none;
  background: none;
  padding: 12px 0;
  font-weight: 600;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.mega-menu {
  width: 900px;
  padding: 0; /* Reset padding để chia cột đều */
  overflow: hidden;
  border: 1px solid #e2e8f0;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

.mega-left {
  width: 35%;
  background: #f8fafc;
  padding: 10px;
  border-right: 1px solid #e2e8f0;
  max-height: 450px;
  overflow-y: auto;
}

.mega-parent {
  padding: 12px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mega-parent:hover, .mega-parent.active {
  background: #ffffff;
  color: #2563eb;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.mega-right {
  width: 65%;
  padding: 20px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  align-content: start;
}

.mega-child {
  padding: 10px;
  border-radius: 6px;
  cursor: pointer;
  color: #475569;
  font-size: 14px;
  transition: 0.2s;
}

.mega-child:hover {
  background: #f1f5f9;
  color: #2563eb;
  padding-left: 15px; /* Hiệu ứng dịch chuyển nhẹ */
}

/* ===== DARK MODE OVERRIDES ===== */
.dark .app-header {
  background: #0f172a;
  border-bottom: 1px solid #1e293b;
}

.dark .brand, .dark .category-trigger, .dark .user-btn {
  color: #f1f5f9;
}

.dark .category-bar {
  background: #1e293b;
  border-top: 1px solid #334155;
}

.dark .search-box input {
  background: #1e293b;
  border-color: #334155;
  color: #f1f5f9;
}

.dark .action-btn {
  color: #cbd5e1;
}

.dark .action-btn:hover {
  background: #334155;
  color: #60a5fa;
}

.dark .mega-menu {
  background: #0f172a;
  border-color: #334155;
}

.dark .mega-left {
  background: #1e293b;
  border-right-color: #334155;
}

.dark .mega-parent, .dark .mega-child {
  color: #cbd5e1;
}

.dark .mega-parent:hover {
  background: #334155;
  color: #60a5fa;
}

.dark .mega-child:hover {
  background: #1e293b;
  color: #60a5fa;
}

/* Bootstrap Dropdown Display */
.category-dropdown .dropdown-menu {
  display: none;
}
.category-dropdown .dropdown-menu.show {
  display: flex;
}
/* 1. Sửa nút User (nút bấm hiện tên Drake Johnson) */
.dark .user-btn {
  background-color: #1e293b !important; /* Màu nền tối */
  color: #f1f5f9 !important;            /* Màu chữ sáng */
  border-color: #334155 !important;      /* Màu viền tối */
}

.dark .user-btn:hover {
  background-color: #334155 !important;
}

/* 2. Sửa Menu xổ xuống (Dropdown Menu của Account) */
.dark .dropdown-menu {
  background-color: #1e293b !important; /* Nền menu tối */
  border: 1px solid #334155 !important;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.5) !important;
}

/* 3. Sửa các mục bên trong menu (Thông tin, Quản trị, Đăng xuất) */
.dark .dropdown-item {
  color: #cbd5e1 !important;
}

.dark .dropdown-item:hover {
  background-color: #334155 !important;
  color: #60a5fa !important;
}

/* 4. Sửa đường gạch ngang chia cách trong menu */
.dark .dropdown-divider {
  border-top: 1px solid #334155 !important;
  opacity: 1;
}

/* 5. Fix cho Mega Menu (Danh mục) nếu nó cũng đang bị trắng nền */
.dark .mega-menu {
  background-color: #0f172a !important;
}

.dark .mega-right {
  background-color: #0f172a !important; /* Đảm bảo phần bên phải không bị trắng */
}
</style>