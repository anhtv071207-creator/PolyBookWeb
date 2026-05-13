<script setup>
import { ref, onMounted, computed, watch } from "vue";
import api from "@/services/api";
import { useRouter } from "vue-router";

// ===== CHART =====
import { Line, Bar } from "vue-chartjs";
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  BarElement,
  CategoryScale,
  LinearScale,
  PointElement,
} from "chart.js";

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  LineElement,
  BarElement,
  CategoryScale,
  LinearScale,
  PointElement,
);
const quickRange = ref(""); // "7d", "1m", "6m", "1y"
// ===== ROUTER =====
const router = useRouter();
const goBackManagement = () => {
  router.push("/managements");
};
const topByQuantity = ref([]);
const topByRevenue = ref([]);
const fetchTopProducts = async () => {
  try {
  const res1 = await api.get("/management/payments/top-quantity");
  const res2 = await api.get("/management/payments/top-revenue");

    topByQuantity.value = res1.data;
    topByRevenue.value = res2.data;
  } catch (err) {
    console.error(err);
  }
};
// ===== TAB =====
const currentMode = ref("add");
const setMode = (mode) => {
  currentMode.value = mode;

  if (mode === "add") {
    fetchRevenueByMonth();
    fetchTotalRevenue();
  }

  if (mode === "edit") {
    if (!revenueByDate.value.length) {
      applyQuickRange("7d");
    }
  }
  if (mode === "top") {
    fetchTopProducts();
  }
};

// ===== DASHBOARD STATE =====
const startDate = ref("");
const endDate = ref("");
const selectedYear = ref(new Date().getFullYear());

const revenueByDate = ref([]);
const revenueByMonth = ref([]);
const totalRevenue = ref(0);

// ===== FORMAT =====
const formatMoney = (value) => {
  return new Intl.NumberFormat("vi-VN").format(value || 0) + " ₫";
};

// ===== CHART DATA =====
const lineChartData = computed(() => ({
  labels: revenueByDate.value.map((i) => i.date),
  datasets: [
    {
      label: "Doanh thu",
      data: revenueByDate.value.map((i) => i.total),

      borderColor: "#3b82f6",
      backgroundColor: "rgba(59,130,246,0.2)",
      fill: true,

      tension: 0.4, // làm mượt line
      pointRadius: 3,
      pointBackgroundColor: "#3b82f6",
      pointHoverRadius: 6,
      borderWidth: 2,
    },
  ],
}));

const barChartData = computed(() => ({
  labels: revenueByMonth.value.map((i) => "T" + i.month),
  datasets: [
    {
      label: "Doanh thu",
      data: revenueByMonth.value.map((i) => i.total),

      backgroundColor: "#6366f1",
      borderRadius: 6,
      barThickness: 30,
    },
  ],
}));

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,

  plugins: {
    legend: {
      display: true,
      labels: {
        color: "#64748b",
        font: {
          size: 13,
          weight: "500",
        },
      },
    },
    tooltip: {
      backgroundColor: "#111827",
      titleColor: "#fff",
      bodyColor: "#e5e7eb",
      borderColor: "#374151",
      borderWidth: 1,
      padding: 10,
      cornerRadius: 8,
      callbacks: {
        label: (ctx) => {
          return " " + formatMoney(ctx.raw);
        },
      },
    },
  },

  scales: {
    x: {
      ticks: {
        color: "#94a3b8",
      },
      grid: {
        display: false,
      },
    },
    y: {
      ticks: {
        color: "#94a3b8",
        callback: (value) => formatMoney(value),
      },
      grid: {
        color: "rgba(148,163,184,0.2)",
      },
    },
  },
};
const isAuto = ref(false);
const applyQuickRange = (type) => {
  isAuto.value = true;
  quickRange.value = type;

  const now = new Date();
  const end = new Date();
  let start = new Date();

  switch (type) {
    case "7d":
      start.setDate(now.getDate() - 7);
      break;
    case "1m":
      start.setMonth(now.getMonth() - 1);
      break;
    case "6m":
      start.setMonth(now.getMonth() - 6);
      break;
    case "1y":
      start.setFullYear(now.getFullYear() - 1);
      break;
  }

  startDate.value = start.toISOString().slice(0, 10);
  endDate.value = end.toISOString().slice(0, 10);

  fetchRevenueByDate();

  setTimeout(() => {
    isAuto.value = false;
  }, 0);
};
// ===== API =====

// Theo ngày
const fetchRevenueByDate = async () => {
  if (!startDate.value || !endDate.value) return;

  try {
    const res = await api.get("/management/payments/revenue-by-date", {
      params: {
        start: startDate.value + "T00:00:00",
        end: endDate.value + "T23:59:59",
      },
    });

    revenueByDate.value = res.data;
  } catch (err) {
    console.error(err);
  }
};
watch([startDate, endDate], () => {
  if (!isAuto.value) {
    quickRange.value = "";
  }
});
// Theo tháng
const fetchRevenueByMonth = async () => {
  try {
    const res = await api.get("/management/payments/revenue-by-month", {
      params: { year: selectedYear.value },
    });

    revenueByMonth.value = res.data;
  } catch (err) {
    console.error(err);
  }
};

// Tổng doanh thu
const fetchTotalRevenue = async () => {
  try {
    const res = await api.get("/management/payments/total-revenue");
    totalRevenue.value = res.data;
  } catch (err) {
    console.error(err);
  }
};

// ===== INIT =====
onMounted(() => {
  fetchRevenueByMonth();
  fetchTotalRevenue();

  applyQuickRange("7d"); // đủ rồi
});
</script>
<template>
  <div class="page" :class="{ 'edit-mode': currentMode === 'edit' }">
    <div class="page-header">
      <button class="btn-back" @click="goBackManagement">← Quay lại</button>
      <h2>Dashboard</h2>
    </div>

    <div class="form-wrapper">
      <!-- TAB -->
      <div class="side-tabs">
        <div
          class="tab-item"
          :class="{ 'active-add': currentMode === 'add' }"
          @click="setMode('add')"
        >
          Thống kê doanh thu
        </div>

        <div
          class="tab-item"
          :class="{ 'active-edit': currentMode === 'edit' }"
          @click="setMode('edit')"
        >
          Tra cứu doanh thu
        </div>
        <div
          class="tab-item"
          :class="{ 'active-edit': currentMode === 'top' }"
          @click="setMode('top')"
        >
          Sản phẩm bán chạy
        </div>
      </div>

      <!-- CONTENT -->
      <div class="tab-content">
        <!-- TAB 1 -->
        <div v-if="currentMode === 'add'">
          <div class="card">
            <h2>Thống kê doanh thu</h2>
            <div class="dashboard-cards">
              <div class="stat-card">
                <p>Tổng doanh thu</p>
                <h3>{{ formatMoney(totalRevenue) }}</h3>
              </div>
            </div>

            <div class="chart-box">
              <h3>Doanh thu theo tháng</h3>
              <Bar :data="barChartData" :options="chartOptions" />
            </div>
          </div>
        </div>

        <!-- TAB 2 -->
        <div v-if="currentMode === 'edit'">
          <div class="card">
            <h2>Tra cứu doanh thu</h2>

            <!-- FILTER DATE -->
            <div class="filter-row">
              <input type="date" v-model="startDate" />
              <input type="date" v-model="endDate" />
              <button class="btn small" @click="fetchRevenueByDate">
                Lọc ngày
              </button>
            </div>
            <div class="quick-filter">
              <button
                :class="{ active: quickRange === '7d' }"
                @click="applyQuickRange('7d')"
              >
                7 ngày
              </button>

              <button
                :class="{ active: quickRange === '1m' }"
                @click="applyQuickRange('1m')"
              >
                1 tháng
              </button>

              <button
                :class="{ active: quickRange === '6m' }"
                @click="applyQuickRange('6m')"
              >
                6 tháng
              </button>

              <button
                :class="{ active: quickRange === '1y' }"
                @click="applyQuickRange('1y')"
              >
                1 năm
              </button>
            </div>
            <!-- CHART DATE -->
            <div class="chart-box">
              <h3>Biểu đồ doanh thu</h3>
              <Line :data="lineChartData" :options="chartOptions" />
            </div>
          </div>
        </div>
        <!-- TAB 3 -->
        <div v-if="currentMode === 'top'">
          <div class="card">
            <h2>Sản phẩm bán chạy</h2>

            <div class="top-grid">
              <!-- Bảng 1 -->
              <div class="top-box">
                <h3>Theo số lượng</h3>

                <div
                  class="top-item"
                  v-for="item in topByQuantity"
                  :key="item.id"
                >
                  <span class="name">{{ item.name }}</span>
                  <span class="value">{{ item.totalSold }}</span>
                </div>
              </div>

              <!-- Bảng 2 -->
              <div class="top-box">
                <h3>Theo doanh thu (tháng)</h3>

                <div
                  class="top-item"
                  v-for="item in topByRevenue"
                  :key="item.id"
                >
                  <span class="name">{{ item.name }}</span>
                  <span class="value">{{ formatMoney(item.revenue) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 30px 5%;
  background: #f7f7f7;
  min-height: 100vh;
}

.page-header {
  position: relative;
  margin-bottom: 25px;
}

.page-header h2 {
  text-align: center;
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.btn-back {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  padding: 8px 14px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
}

.btn-back:hover {
  background: #f2f2f2;
}

.form-wrapper {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 24px;
  margin-bottom: 35px;
}

.card {
  background: white;
  border-radius: 10px;
  padding: 24px;
  border: 1px solid #e2e2e2;
}

.card h2 {
  text-align: left;
  font-size: 20px;
  margin-bottom: 18px;
  color: #333;
}

.side-tabs {
  background: white;
  border-radius: 10px;
  border: 1px solid #e2e2e2;
  display: flex;
  flex-direction: column;
}

.tab-item {
  padding: 12px;
  text-align: center;
  cursor: pointer;
  border-bottom: 1px solid #eee;
  font-weight: 500;
}

.tab-item:hover {
  background: #f5f5f5;
}

.tab-item.active-add,
.tab-item.active-edit {
  background: #333;
  color: white;
}

.form-group {
  margin-bottom: 16px;
}

label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #555;
}

input,
textarea {
  width: 100%;
  padding: 10px 12px;
  border-radius: 6px;
  border: 1px solid #dcdcdc;
  font-size: 14px;
}

input:focus,
textarea:focus {
  outline: none;
  border-color: #888;
}

textarea {
  min-height: 90px;
}

.preview-box img {
  width: 140px;
  border-radius: 6px;
  margin-top: 10px;
}

.btn {
  width: 100%;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
  font-weight: 500;
}

.btn:hover {
  background: #f2f2f2;
}

.small {
  width: auto;
  padding: 6px 12px;
}

.btn-category {
  margin-top: 10px;
  border: 1px dashed #ccc;
  background: #fafafa;
}

.btn-category:hover {
  background: #f2f2f2;
}

.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin: 10px 0;
}

.tag.selected {
  background: #eee;
  border: 1px solid #ddd;
  padding: 5px 10px;
  border-radius: 14px;
  font-size: 12px;
}

.book-item {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.book-item:hover {
  background: #fafafa;
}

.book-thumb {
  width: 70px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.author {
  margin: 2px 0;
  font-size: 13px;
  color: #666;
}

.book-meta {
  margin-top: 6px;
  display: flex;
  gap: 10px;
  font-size: 13px;
}

.price {
  font-weight: 600;
}

.stock {
  color: #777;
}

.actions {
  margin-left: auto;
  display: flex;
  gap: 6px;
}

.popup-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 140px;
}

.popup-wrapper {
  width: 800px;
  background: white;
  padding: 20px;
  border-radius: 10px;
  max-height: 70vh;
  overflow-y: auto;
}

.popup-header {
  position: relative;
  background: white;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.tag-container,
.selected-content {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  border: 1px solid #eee;
  padding: 10px;
  border-radius: 6px;
  background: #fafafa;
}

.tag-item {
  padding: 5px 10px;
  border: 1px solid #ccc;
  border-radius: 14px;
  cursor: pointer;
  font-size: 12px;
}

.tag-item.active {
  background: #333;
  color: white;
}
.quick-filter {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.quick-filter button {
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
}

.quick-filter button.active {
  background: #333;
  color: white;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 6px;
}

.pagination button {
  padding: 5px 10px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background: white;
}

.pagination button.active {
  background: #333;
  color: white;
}

.delete-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
}

.delete-box {
  width: 360px;
  background: white;
  padding: 20px;
  border-radius: 10px;
}

.delete-actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-cancel {
  background: white;
  border: 1px solid #ccc;
}

.btn-danger {
  background: #e74c3c;
  color: white;
  border: none;
}
/* ===== DARK MODE ===== */

.dark .page {
  background: #0f172a;
}

.dark .page-header h2 {
  color: #f1f5f9;
}

.dark .btn-back {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .btn-back:hover {
  background: #334155;
}

/* card */

.dark .card {
  background: #1e293b;
  border-color: #334155;
}

.dark .card h2 {
  color: #f1f5f9;
}

/* side tabs */

.dark .side-tabs {
  background: #1e293b;
  border-color: #334155;
}

.dark .tab-item {
  color: #e2e8f0;
  border-bottom: 1px solid #334155;
}

.dark .tab-item:hover {
  background: #334155;
}

.dark .tab-item.active-add,
.dark .tab-item.active-edit {
  background: #475569;
  color: white;
}

/* form */

.dark label {
  color: #cbd5f5;
}

.dark input,
.dark textarea {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark input:focus,
.dark textarea:focus {
  border-color: #64748b;
}

/* button */

.dark .btn {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .btn:hover {
  background: #334155;
}

/* category tags */

.dark .tag.selected {
  background: #334155;
  border-color: #475569;
  color: #e2e8f0;
}

/* book list */

.dark .book-item {
  border-bottom: 1px solid #334155;
}

.dark .book-item:hover {
  background: #1e293b;
}

.dark .title {
  color: #f1f5f9;
}

.dark .author {
  color: #94a3b8;
}

.dark .stock {
  color: #94a3b8;
}

/* popup */

.dark .popup-wrapper {
  background: #1e293b;
  color: #e2e8f0;
}

.dark .popup-header {
  background: #1e293b;
  border-bottom: 1px solid #334155;
}

.dark .tag-container,
.dark .selected-content {
  background: #0f172a;
  border-color: #334155;
}

.dark .tag-item {
  border-color: #475569;
  color: #e2e8f0;
}

.dark .tag-item.active {
  background: #475569;
}

/* pagination */

.dark .pagination button {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .pagination button.active {
  background: #475569;
}

/* delete confirm */

.dark .delete-box {
  background: #1e293b;
  color: #e2e8f0;
}

.dark .btn-cancel {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}
/* dark mode button category */

.dark .btn-category {
  background: #1e293b;
  border: 1px dashed #475569;
  color: #e2e8f0;
}

.dark .btn-category:hover {
  background: #334155;
}
.alert {
  margin: 10px 0;
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 14px;
}

.alert.success {
  background: #e6fffa;
  color: #065f46;
  border: 1px solid #34d399;
}

.alert.error {
  background: #ffe6e6;
  color: #7f1d1d;
  border: 1px solid #f87171;
}
.input-error {
  border-color: #ff4d4f !important;
  box-shadow: 0 0 0 2px rgba(255, 77, 79, 0.1);
}

.error-text {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 5px;
  font-weight: 500;
  text-align: left;
}

/* Dark mode */
.dark .input-error {
  background: #3b0d0d;
  border-color: #ef4444 !important;
}
/* Style này dùng chung cho cả Form Thêm và Sửa */
.input-error {
  border-color: #ff4d4f !important;
  background-color: #fff1f0;
}

.error-text {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 4px;
  font-weight: 500;
}

.input-error:focus {
  box-shadow: 0 0 0 2px rgba(255, 77, 79, 0.2) !important;
}
.toast-overlay {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
}

.toast-box {
  min-width: 280px;
  background: #0f172a;
  color: #fff;
  padding: 12px 14px;
  border-radius: 10px;
  display: flex;
  gap: 10px;
  align-items: center;
}

.toast-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toast-box.success .toast-icon {
  background: #10b981;
}

.toast-box.error .toast-icon {
  background: #ef4444;
}
.filter-row {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.dashboard-cards {
  display: flex;
  gap: 12px;
  margin: 15px 0;
}

.stat-card {
  flex: 1;
  padding: 15px;
  border-radius: 10px;
  background: #f1f5f9;
}

.stat-card h3 {
  margin: 5px 0 0;
  font-size: 20px;
}

.chart-box {
  margin-top: 20px;
  height: 320px;
}
.dark .quick-filter button {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .quick-filter button:hover {
  background: #334155;
}

.dark .quick-filter button.active {
  background: #475569;
  color: #fff;
}
.dark .filter-row input {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .filter-row button {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .filter-row button:hover {
  background: #334155;
}
.dark .chart-box {
  background: #1e293b;
  padding: 15px;
  border-radius: 10px;
  border: 1px solid #334155;
}

.dark .chart-box h3 {
  color: #f1f5f9;
}
.dark p {
  color: #cbd5f5;
}
.stat-card,
.quick-filter button,
.btn,
.tab-item {
  transition: all 0.2s ease;
}
.top-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.top-box {
  background: #f8fafc;
  padding: 15px;
  border-radius: 10px;
}

.top-box h3 {
  margin-bottom: 10px;
  font-size: 16px;
}

.top-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #e5e7eb;
}

.top-item:last-child {
  border-bottom: none;
}

.name {
  font-weight: 500;
}

.value {
  font-weight: 600;
}
</style>
