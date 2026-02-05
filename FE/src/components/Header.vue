<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { ref, onMounted } from "vue";
import api from "@/services/api";

const canSeeAdminBar = computed(() => isAdmin.value || isStaff.value);

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
  router.push("/login");
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
  <header class="header bg-white shadow-sm">
    <div class="container d-flex align-items-center py-2">
      <router-link
        to="/"
        class="d-flex align-items-center gap-2 text-decoration-none"
      >
        <img src="/images/logo.png" alt="Logo" class="logo" />
      </router-link>

      <div class="search-box mx-4">
        <div class="input-group">
          <input type="text" class="form-control" placeholder="Tìm sách" />
          <span class="input-group-text bg-light">
            <i class="bi bi-search"></i>
          </span>
        </div>
      </div>

      <div class="d-flex align-items-center gap-3">
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

        <router-link to="/cart" class="btn btn-outline-danger">
          <i class="bi bi-cart"></i>
          Giỏ hàng
        </router-link>
        <router-link to="/orders/view" class="btn btn-outline-secondary">
          <i class="bi bi-receipt"></i>
          Đơn hàng
        </router-link>

        <template v-if="!isLoggedIn">
          <router-link to="/register" class="btn btn-outline-primary">
            Sign Up
          </router-link>
          <router-link to="/login" class="btn btn-primary">
            Sign In
          </router-link>
        </template>

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

    <div v-if="canSeeAdminBar" class="admin-bar border-top">
      <div class="container d-flex justify-content-center gap-5 py-3">
        <router-link to="/management/product" class="admin-item">
          <i class="bi bi-book"></i>
          <span>Quản lý sản phẩm</span>
        </router-link>
        <router-link to="/management/category" class="admin-item">
          <i class="bi bi-book"></i>
          <span>Quản lý danh mục</span>
        </router-link>
        <router-link to="/management/orders" class="admin-item">
          <i class="bi bi-box-seam"></i>
          <span>Quản lý đơn hàng</span>
        </router-link>

        <router-link to="/reports" class="admin-item">
          <i class="bi bi-bar-chart"></i>
          <span>Thống kê báo cáo</span>
        </router-link>

        <router-link to="/promotions" class="admin-item">
          <i class="bi bi-cash-stack"></i>
          <span>Quản lý khuyến mãi</span>
        </router-link>

        <router-link
          v-if="isAdmin"
          to="/managements/account"
          class="admin-item"
        >
          <i class="bi bi-people"></i>
          <span>Quản lý tài khoản</span>
        </router-link>
      </div>
    </div>
  </header>
</template>

<style scoped>
.header {
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

</style>
