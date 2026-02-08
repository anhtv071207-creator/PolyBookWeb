<template>
  <div v-if="canSeeAdminBar" class="admin-page">
    <h2 class="mb-4">Trang quản trị</h2>

    <div class="admin-grid">
      <router-link to="/management/product" class="admin-card horizontal">
        <img
          src="/images/product.png"
          alt="Quản lý sản phẩm"
          class="admin-image"
        />
        <span>Quản lý sản phẩm</span>
      </router-link>

      <router-link to="/management/category" class="admin-card horizontal">
        <img
          src="/images/category.png"
          alt="Quản lý danh mục"
          class="admin-image"
        />
        <span>Quản lý danh mục</span>
      </router-link>

      <router-link to="/management/orders" class="admin-card horizontal">
        <img
          src="/images/order.png"
          alt="Quản lý danh mục"
          class="admin-image"
        />
        <span>Quản lý đơn hàng</span>
      </router-link>
      <router-link to="/reports" class="admin-card horizontal">
        <img
          src="/images/report.png"
          alt="Thống kê báo cáo"
          class="admin-image"
        />
        <span>Thống kê báo cáo</span>
      </router-link>
      <router-link to="/promotions" class="admin-card horizontal">
        <img
          src="/images/promotions.png"
          alt="Thống kê báo cáo"
          class="admin-image"
        />
        <span>Quản lý khuyến mãi</span>
      </router-link>
      <router-link to="/managements/account" class="admin-card horizontal">
        <img
          src="/images/account.png"
          alt="Quản lý tài khoản"
          class="admin-image"
        />
        <span>Quản lý tài khoản</span>
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
  padding: 24px;
}

.admin-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 24px;
}

.admin-card {
  background: #8eb4ff;
  height: 120px;
  border-radius: 10px;

  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;

  color: #000;
  text-decoration: none;
  font-weight: 600;

  transition: all 0.25s ease;
}


.admin-image {
  width: 72px;
  height: 72px;
  object-fit: contain;
  flex-shrink: 0;
}

.admin-card span {
  font-size: 22px;
}

.admin-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.15);
}
</style>
