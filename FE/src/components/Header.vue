<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();
const auth = useAuthStore();
const router = useRouter();

const parentCategories = ref([]);
const childCategories = ref([]);
const selectedParentId = ref(null);

const darkMode = ref(false);

const isLoggedIn = computed(() => auth.isLoggedIn);
const role = computed(() => auth.role);
const userName = computed(() => auth.user?.name || "ho_ten");
const isAdmin = computed(() => role.value === "ADMIN");
const isStaff = computed(() => role.value === "STAFF");
const isUser = computed(() => role.value === "USER");

const keyword = ref("");

const toggleDarkMode = () => {
  darkMode.value = !darkMode.value;

  if (darkMode.value) {
    document.documentElement.classList.add("dark");
    localStorage.setItem("theme", "dark");
  } else {
    document.documentElement.classList.remove("dark");
    localStorage.setItem("theme", "light");
  }
};

const logout = () => {
  auth.logout();
  router.push("/");
};

const fetchParentCategories = async () => {
  const res = await api.get("http://localhost:8080/api/categories/root");
  parentCategories.value = res.data;

  if (res.data.length > 0) {
    fetchChildCategories(res.data[0].id);
  }
};

const searchBooks = () => {
  if (!keyword.value.trim()) return;

  router.push({
    path: "/search",
    query: { keyword: keyword.value },
  });
};

const fetchChildCategories = async (parentId) => {
  if (selectedParentId.value === parentId) return;

  selectedParentId.value = parentId;
  const res = await api.get(
    `http://localhost:8080/api/categories/parent/${parentId}`,
  );
  childCategories.value = res.data;
};

const goToCategory = (id) => {
  router.push(`/category/${id}`);
};

onMounted(() => {
  fetchParentCategories();

  const savedTheme = localStorage.getItem("theme");

  if (savedTheme === "dark") {
    darkMode.value = true;
    document.documentElement.classList.add("dark");
  }
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
        </router-link>

        <router-link
          v-if="!isAdmin && !isStaff"
          to="/orders/view"
          class="action-btn"
        >
          <i class="bi bi-receipt"></i>
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
.app-header {
  background: #ffffff;
  border-bottom: 1px solid #e2e8f0;
  position: sticky;
  top: 0;
  z-index: 1000;
}

/* ===== HEADER TOP ===== */

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 14px 0;
}

/* LOGO */

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}

.logo {
  height: 40px;
}

.brand {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
}

/* SEARCH */

.search-box {
  flex: 1;
  max-width: 550px;
  display: flex;
}

.search-box input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid #cbd5e1;
  border-right: none;
  border-radius: 8px 0 0 8px;
  outline: none;
}

.search-box input:focus {
  border-color: #2563eb;
}

.search-box button {
  border: none;
  background: #2563eb;
  color: white;
  padding: 0 18px;
  border-radius: 0 8px 8px 0;
  cursor: pointer;
}

.search-box button:hover {
  background: #1d4ed8;
}

/* HEADER ACTIONS */

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-btn {
  font-size: 20px;
  color: #1e293b;
  transition: 0.2s;
}

.action-btn:hover {
  color: #2563eb;
}

/* LOGIN BUTTON */

.login-btn {
  background: #2563eb;
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: 500;
  text-decoration: none;
}

.login-btn:hover {
  background: #1d4ed8;
}

/* USER BUTTON */

.user-btn {
  background: #f1f5f9;
  border: none;
  padding: 8px 12px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

/* ===== CATEGORY BAR ===== */

.category-bar {
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.category-dropdown {
  position: relative;
}

.category-trigger {
  border: none;
  background: none;
  padding: 10px 0;
  font-weight: 600;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* ===== MEGA MENU ===== */

.mega-menu {
  width: 840px;
  padding: 16px;
  display: flex;
  gap: 20px;
}

/* LEFT SIDE */

.mega-left {
  width: 40%;
  border-right: 1px solid #e2e8f0;
}

.mega-parent {
  padding: 10px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.2s;
}

.mega-parent:hover {
  background: #e2e8f0;
}

/* RIGHT SIDE */

.mega-right {
  width: 60%;
  padding-left: 10px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 6px;
}

.mega-child {
  padding: 8px 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.2s;
}

.mega-child:hover {
  background: #e2e8f0;
  color: #2563eb;
}
.category-dropdown .dropdown-menu {
  display: none;
}

.category-dropdown .dropdown-menu.show {
  display: flex;
}
/* ===== DARK MODE HEADER ===== */

.dark .app-header {
  background: #1e293b;
  border-bottom: 1px solid #334155;
}

.dark .category-bar {
  background: #1e293b;
  border-top: 1px solid #334155;
}

/* search */

.dark .search-box input {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .search-box input::placeholder {
  color: #94a3b8;
}

/* icons */

.dark .action-btn {
  color: #e2e8f0;
}

.dark .action-btn:hover {
  color: #60a5fa;
}

/* account */

.dark .user-btn {
  background: #334155;
  color: #e2e8f0;
}

/* category text */

.dark .category-trigger {
  color: #e2e8f0;
}

/* mega menu */

.dark .mega-menu {
  background: #1e293b;
  border: 1px solid #334155;
}

.dark .mega-left {
  border-right: 1px solid #334155;
}

.dark .mega-parent,
.dark .mega-child {
  color: #e2e8f0;
}

.dark .mega-parent:hover,
.dark .mega-child:hover {
  background: #334155;
  color: #60a5fa;
}
</style>
