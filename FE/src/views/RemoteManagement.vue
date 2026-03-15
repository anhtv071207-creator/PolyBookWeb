<template>
  <div v-if="canSeeAdminBar" class="admin-page">
    <h2 class="mb-4">Trang quản trị</h2>

    <div class="admin-grid">
      <router-link to="/management/product" class="admin-card">
        <i class="bi bi-book admin-icon"></i>
        <span>Quản lý sản phẩm</span>
      </router-link>

      <router-link to="/management/category" class="admin-card">
        <i class="bi bi-folder admin-icon"></i>
        <span>Quản lý danh mục</span>
      </router-link>

      <router-link to="/management/orders" class="admin-card">
        <i class="bi bi-box-seam admin-icon"></i>
        <span>Quản lý đơn hàng</span>
      </router-link>

      <router-link to="/reports" class="admin-card">
        <i class="bi bi-bar-chart admin-icon"></i>
        <span>Thống kê báo cáo</span>
      </router-link>

      <router-link to="/promotions" class="admin-card">
        <i class="bi bi-tag admin-icon"></i>
        <span>Quản lý khuyến mãi</span>
      </router-link>

      <router-link v-if="isAdmin" to="/managements/account" class="admin-card">
        <i class="bi bi-people admin-icon"></i>
        <span>Quản lý người dùng</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { ref, onMounted } from "vue";
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();
const auth = useAuthStore();
const isLoggedIn = computed(() => auth.isLoggedIn);
const role = computed(() => auth.role);

const router = useRouter();
const isAdmin = computed(() => role.value === "ADMIN");
const isStaff = computed(() => role.value === "STAFF");
const canSeeAdminBar = computed(() => isAdmin.value || isStaff.value);
</script>
<style scoped>
.admin-page {
  padding: 40px 24px;
  max-width: 1100px;
  margin: auto;
}

.admin-page h2 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 28px;
  color: #1e293b;
}

/* GRID */

.admin-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 22px;
}

/* CARD */

.admin-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;

  display: flex;
  align-items: center;
  gap: 16px;

  padding: 22px;

  text-decoration: none;
  color: #1f2937;

  transition: all 0.25s ease;
}

.admin-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 22px rgba(0, 0, 0, 0.08);
  border-color: #d1d5db;
}

/* ICON */

.admin-icon {
  font-size: 26px;
  width: 48px;
  height: 48px;

  display: flex;
  align-items: center;
  justify-content: center;

  background: #f1f5f9;
  border-radius: 10px;
}

/* TEXT */

.admin-card span {
  font-size: 18px;
  font-weight: 600;
}
.admin-card:nth-child(1) .admin-icon { background:#e0f2fe }
.admin-card:nth-child(2) .admin-icon { background:#ecfdf5 }
.admin-card:nth-child(3) .admin-icon { background:#fef3c7 }
.admin-card:nth-child(4) .admin-icon { background:#ede9fe }
.admin-card:nth-child(5) .admin-icon { background:#fee2e2 }
.admin-card:nth-child(6) .admin-icon { background:#dcfce7 }
/* ===== DARK MODE ADMIN PAGE ===== */

.dark .admin-page {
  color: #e2e8f0;
}

.dark .admin-page h2 {
  color: #f1f5f9;
}

/* card */

.dark .admin-card {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .admin-card:hover {
  box-shadow: 0 10px 22px rgba(0,0,0,0.6);
  border-color: #475569;
}

/* icon */

.dark .admin-icon {
  background: #0f172a;
  color: #e2e8f0;
}

/* icon colors vẫn giữ nhưng dịu hơn */

.dark .admin-card:nth-child(1) .admin-icon { background:#1e3a8a }
.dark .admin-card:nth-child(2) .admin-icon { background:#065f46 }
.dark .admin-card:nth-child(3) .admin-icon { background:#78350f }
.dark .admin-card:nth-child(4) .admin-icon { background:#4c1d95 }
.dark .admin-card:nth-child(5) .admin-icon { background:#7f1d1d }
.dark .admin-card:nth-child(6) .admin-icon { background:#14532d }
</style>
