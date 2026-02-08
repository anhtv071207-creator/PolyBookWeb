<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { ref, onMounted } from "vue";
import api from "@/services/api";

const auth = useAuthStore();
const router = useRouter();
console.log("isLoggedIn:", auth.isLoggedIn);
console.log("role:", auth.role);
const parentCategories = ref([]);
const childCategories = ref([]);
const selectedParentId = ref(null);

const isLoggedIn = computed(() => auth.isLoggedIn);
const role = computed(() => auth.role);
const userName = computed(() => auth.user?.name || "ho_ten");
const isAdmin = computed(() => role.value === "ADMIN");
const isStaff = computed(() => role.value === "STAFF");
const isUser = computed(() => role.value === "USER");

const logout = () => {
  auth.logout();
  // router.push("/login");
};
const fetchParentCategories = async () => {
  const res = await api.get("http://localhost:8080/api/categories/root");
  parentCategories.value = res.data;
};

const fetchChildCategories = async (parentId) => {
  selectedParentId.value = parentId;
  const res = await api.get(
    `http://localhost:8080/api/categories/parent/${parentId}`,
  );
  childCategories.value = res.data;
};
onMounted(() => {
  fetchParentCategories();
});
</script>

<template>
  <header class="header  shadow-sm"> 
    <!-- bg-white -->
    <div class="container header-row py-2">
      <div class="header-left">
        <router-link
          to="/"
          class="d-flex align-items-center gap-2 text-decoration-none"
        >
          <img src="/images/logo.png" alt="Logo" class="logo" />
        </router-link>
      </div>
      <div class="header-center">
        <div class="search-box">
          <div class="input-group">
            <input type="text" class="form-control" placeholder="Tìm sách" />
            <span class="input-group-text bg-light">
              <i class="bi bi-search"></i>
            </span>
          </div>
        </div>
      </div>
      <div class="header-right">
        <div class="d-flex align-items-center gap-3 header-actions">
          <div class="dropdown">
            <button
              class="btn btn-light dropdown-toggle"
              data-bs-toggle="dropdown"
            >
              Danh mục
            </button>
            <ul class="dropdown-menu category-mega p-3">
              <div class="row">
                <div class="col-4 border-end category-parent">
                  <li
                    v-for="parent in parentCategories"
                    :key="parent.id"
                    class="category-parent-item"
                    @mouseenter="fetchChildCategories(parent.id)"
                  >
                    {{ parent.tenDanhMuc }}
                  </li>
                </div>
                <div class="col-8 category-child">
                  <li
                    v-for="child in childCategories"
                    :key="child.id"
                    class="category-child-item"
                  >
                    {{ child.tenDanhMuc }}
                  </li>
                </div>
              </div>
            </ul>
          </div>

          <router-link
            v-if="!isAdmin && !isStaff"
            to="/cart"
            class="btn btn-outline-danger"
          >
            <i class="bi bi-cart"></i>
            Giỏ hàng
          </router-link>
          <router-link
            v-if="!isAdmin && !isStaff"
            to="/orders/view"
            class="btn btn-outline-secondary"
          >
            <i class="bi bi-receipt"></i>
            Đơn hàng
          </router-link>

          <div class="account-fixed">
            <template v-if="!isLoggedIn">
              <router-link to="/register" class="btn btn-outline-primary">
                Đăng Ký
              </router-link>
              <router-link to="/login" class="btn btn-primary">
                Đăng Nhập
              </router-link>
            </template>

            <template v-else>
              <div class="dropdown">
                <button
                  class="btn btn-dark dropdown-toggle"
                  data-bs-toggle="dropdown"
                  data-bs-display="static"
                >
                  <i class="bi bi-person-circle me-1"></i>
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
    </div>
  </header>
</template>

<style scoped>
/* .header {
  width: 100%;
  
}

.logo {
  width: 42px;
  height: 42px;
  object-fit: contain;
  cursor: pointer;
}
.search-box input {
  width: 320px;
  max-width: 100%;
  border-radius: 20px 0 0 20px;
}
.search-box .input-group-text {
  border-radius: 0 20px 20px 0;
}

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

.category-mega {
  width: 70vw;
  max-width: 900px;
  left: 50%;
  transform: translateX(-50%);
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.12);
}

.category-parent,
.category-child {
  max-height: 360px;
  overflow-y: auto;
}

.category-parent-item {
  padding: 10px 14px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  color: #333;
  transition: all 0.2s ease;
}

.category-parent-item:hover {
  background-color: #f1f3f5;
  color: var(--bs-primary);
}

.category-child-item {
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  color: #555;
  transition: all 0.15s ease;
}

.category-child-item:hover {
  background-color: #f8f9fa;
  color: var(--bs-danger);
}

.category-parent::-webkit-scrollbar,
.category-child::-webkit-scrollbar {
  width: 6px;
}

.category-parent::-webkit-scrollbar-thumb,
.category-child::-webkit-scrollbar-thumb {
  background-color: #ced4da;
  border-radius: 4px;
}
.header-row {
  display: flex;
  align-items: center;
}

.header-left {
  flex: 0 0 auto;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.header-right {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  gap: 12px;
}

.account-fixed {
  margin-left: auto;
  display: flex;
  align-items: center;
} */
/* ===== HEADER BASE ===== */
.header {
  width: 100%;
  background: linear-gradient(90deg, #243447, #2c3e50);
  color: #e5edf5;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header a {
  text-decoration: none;
  color: inherit;
}

/* ===== LOGO ===== */
.logo {
  width: 42px;
  height: 42px;
  object-fit: contain;
}

/* ===== LAYOUT ===== */
.header-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-left {
  flex: 0 0 auto;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.header-right {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
}

.header-actions {
  gap: 10px;
}

/* ===== SEARCH ===== */
.search-box input {
  width: 340px;
  max-width: 100%;
  border-radius: 999px 0 0 999px;
  border: none;
  padding-left: 14px;
}

.search-box input:focus {
  box-shadow: none;
}

.search-box .input-group-text {
  border-radius: 0 999px 999px 0;
  background-color: #e9ecef;
  border: none;
  cursor: pointer;
}

/* ===== BUTTONS ===== */
.btn {
  white-space: nowrap;
  border-radius: 999px;
  font-size: 14px;
  padding: 6px 14px;
}

/* primary */
.btn-primary {
  background-color: #4a6fa5;
  border-color: #4a6fa5;
}
.btn-primary:hover {
  background-color: #3d5f90;
  border-color: #3d5f90;
}

/* light (Danh mục) */
.btn-light {
  background-color: #3b5166;
  color: #e5edf5;
  border-color: #3b5166;
}
.btn-light:hover {
  background-color: #466078;
  border-color: #466078;
}

/* outline */
.btn-outline-danger,
.btn-outline-secondary {
  color: #e5edf5;
  border-color: #b6c2cf;
}
.btn-outline-danger:hover,
.btn-outline-secondary:hover {
  background-color: #3b5166;
  border-color: #3b5166;
}

/* account */
.btn-dark {
  background-color: #3b5166;
  border-color: #3b5166;
}
.btn-dark:hover {
  background-color: #466078;
}

/* ===== DROPDOWN COMMON ===== */
.dropdown-menu {
  background-color: #2c3e50;
  border: none;
  border-radius: 12px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.35);
  padding: 6px;
}

.dropdown-item {
  color: #e5edf5;
  border-radius: 8px;
  padding: 8px 14px;
  font-size: 14px;
}

.dropdown-item:hover {
  background-color: #3b5166;
  color: #ffffff;
}

.dropdown-divider {
  border-color: rgba(255, 255, 255, 0.12);
}

/* ===== CATEGORY MEGA MENU ===== */
.category-mega {
  width: 72vw;
  max-width: 920px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px;
}

.category-parent,
.category-child {
  max-height: 360px;
  overflow-y: auto;
}

/* parent */
.category-parent-item {
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  color: #2c3e50;
  transition: background 0.2s, color 0.2s;
}

.category-parent-item:hover {
  background-color: #e9eef3;
  color: #4a6fa5;
}

/* child */
.category-child-item {
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #444;
  transition: background 0.15s, color 0.15s;
}

.category-child-item:hover {
  background-color: #f1f3f5;
  color: #dc3545;
}

/* ===== SCROLLBAR ===== */
.category-parent::-webkit-scrollbar,
.category-child::-webkit-scrollbar {
  width: 6px;
}

.category-parent::-webkit-scrollbar-thumb,
.category-child::-webkit-scrollbar-thumb {
  background-color: #c2c9d1;
  border-radius: 4px;
}

/* ===== ACCOUNT FIX ===== */
.account-fixed {
  margin-left: auto;
  display: flex;
  align-items: center;
}

/* dropdown account – mặc định chữ sáng */
.dropdown-menu .dropdown-item {
  color: #e5edf5;              /* chữ sáng mặc định */
  background-color: transparent;
}

/* hover / focus */
.dropdown-menu .dropdown-item:hover,
.dropdown-menu .dropdown-item:focus {
  background-color: #3b5166;   /* nền nổi bật hơn */
  color: #ffffff;              /* trắng rõ */
}

/* item nguy hiểm (Đăng xuất) */
.dropdown-menu .dropdown-item.text-danger {
  color: #ff6b6b;              /* đỏ sáng trên nền tối */
}

.dropdown-menu .dropdown-item.text-danger:hover {
  background-color: rgba(220, 53, 69, 0.15);
  color: #ff8787;
}
/* ===== CATEGORY DROPDOWN ===== */
.category-mega {
  background-color: #2c3e50;
}

/* ===== CỘT CHA ===== */
.category-parent {
  border-right: 1px solid rgba(255, 255, 255, 0.15);
}

.category-parent-item {
  color: #ffffff;                 /* chữ trắng mặc định */
  background-color: transparent;
  transition: background-color 0.15s ease, color 0.15s ease;
}

.category-parent-item:hover {
  background-color: #ffffff;      /* nền trắng khi hover */
  color: #2c3e50;                  /* chữ = màu nền cũ */
}

/* ===== CỘT CON ===== */
.category-child-item {
  color: #ffffff;                 /* chữ trắng mặc định */
  background-color: transparent;
  transition: background-color 0.15s ease, color 0.15s ease;
}

.category-child-item:hover {
  background-color: #ffffff;      /* nền trắng */
  color: #2c3e50;                  /* chữ tối */
}

/* ===== SCROLLBAR ===== */
.category-parent::-webkit-scrollbar,
.category-child::-webkit-scrollbar {
  width: 6px;
}

.category-parent::-webkit-scrollbar-thumb,
.category-child::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.35);
  border-radius: 4px;
}

</style>
