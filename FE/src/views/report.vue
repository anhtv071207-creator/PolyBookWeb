<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "@/services/api";

const router = useRouter();

const loading = ref(true);

const stats = ref({
  totalOrders: 0,
  totalRevenue: 0,
  totalUsers: 0,
  totalBooks: 0,
});

const goBackManagement = () => {
  router.back();
};

const formatCurrency = (value) => {
  return new Intl.NumberFormat("vi-VN").format(value || 0);
};

const fetchStatistics = async () => {
  try {
    loading.value = true;

    const [orderRes, revenueRes, userRes, bookRes] = await Promise.all([
      api.get("/management/orders?page=0&size=1000"),
      api.get("/management/payments/revenue"),
      api.get("/account-management/admin/users"),
      api.get("/books?page=0&size=1000"),
    ]);

    stats.value.totalOrders = orderRes?.data?.totalElements ?? 0;
    stats.value.totalRevenue = revenueRes?.data ?? 0;
    stats.value.totalUsers =
      userRes?.data?.totalElements ?? userRes?.data?.length ?? 0;
    stats.value.totalBooks = bookRes?.data?.totalElements ?? 0;
  } catch (error) {
    console.error("Lỗi thống kê:", error);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchStatistics);
</script>

<template>
  <div class="container">
    <div class="page-header">
      <button class="btn-back" @click="goBackManagement">← Quay lại</button>
      <h2>Thống kê báo cáo</h2>
    </div>

    <div class="stats-grid">
      <div class="stat-card blue">
        <div class="stat-icon">📦</div>
        <h4>Tổng đơn hàng</h4>
        <p>{{ stats.totalOrders }}</p>
      </div>

      <div class="stat-card green">
        <div class="stat-icon">💰</div>
        <h4>Doanh thu (đã giao)</h4>
        <p>{{ formatCurrency(stats.totalRevenue) }} đ</p>
      </div>

      <div class="stat-card orange">
        <div class="stat-icon">👤</div>
        <h4>Tổng người dùng</h4>
        <p>{{ stats.totalUsers }}</p>
      </div>

      <div class="stat-card purple">
        <div class="stat-icon">📚</div>
        <h4>Tổng sách</h4>
        <p>{{ stats.totalBooks }}</p>
      </div>
    </div>
  </div>
</template>
<style scoped>
.container {
  max-width: 1200px;
  margin: 40px auto;
  padding: 40px;
  background: #eef4ff;
  border-radius: 24px;
  min-height: 100vh;
}

/* ===== HEADER ===== */
.page-header {
  position: relative;
  text-align: center;
  margin-bottom: 45px;
}

.page-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #0d6efd;
  margin: 0;
}

.btn-back {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  padding: 10px 18px;
  border-radius: 14px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  background: linear-gradient(135deg, #0d6efd, #00c6ff);
  color: white;
  transition: 0.3s;
}

.btn-back:hover {
  transform: translateY(-50%) translateY(-3px);
  box-shadow: 0 10px 25px rgba(13, 110, 253, 0.35);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 28px;
}

.stat-card {
  background: linear-gradient(145deg, #ffffff, #f4f9ff);
  padding: 35px 25px;
  border-radius: 22px;
  text-align: center;
  transition: 0.3s;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.06);
  border: 2px solid transparent;
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 45px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.stat-card h4 {
  font-size: 14px;
  font-weight: 600;
  color: #666;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.stat-card p {
  font-size: 30px;
  font-weight: 800;
  margin: 0;
}

.blue {
  border-color: #0d6efd;
}

.green {
  border-color: #198754;
}

.orange {
  border-color: #fd7e14;
}

.purple {
  border-color: #6f42c1;
}
</style>
