<template>
  <div class="lookup-wrapper">
    <div class="lookup-card">
      <div class="lookup-header">
        <h3>Tra cứu đơn hàng</h3>
        <p>Nhập email hoặc số điện thoại để tìm đơn hàng</p>
      </div>

      <div class="search-box">
        <input
          v-model="keyword"
          type="text"
          placeholder="Email hoặc số điện thoại..."
          @keyup.enter="lookup"
        />
        <button @click="lookup" :disabled="loading">
          <span v-if="!loading">Tra cứu</span>
          <span v-else class="spinner"></span>
        </button>
      </div>

      <div v-if="error" class="error-box">
        {{ error }}
      </div>

      <div v-if="searched && !orders.length && !error" class="empty-box">
        Không tìm thấy đơn hàng
      </div>

      <div v-if="orders.length" class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Mã đơn</th>
              <th>Ngày tạo</th>
              <th>Tổng tiền</th>
              <th>Trạng thái</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(o, index) in paginatedOrders" :key="o.id">
              <td>
                {{ (currentPage - 1) * pageSize + index + 1 }}
              </td>
              <td>{{ o.maDonHang }}</td>
              <td>{{ formatDate(o.ngayTao) }}</td>
              <td class="money">{{ formatMoney(o.tongTien) }}</td>
              <td>
                <span
                  class="status-pill"
                  :class="STATUS_MAP[Number(o.trangThai)]?.class"
                >
                  {{ STATUS_MAP[Number(o.trangThai)]?.text }}
                </span>
              </td>
              <td>
                <button class="view-btn" @click="viewDetail(o.id)">Xem</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div class="pagination" v-if="totalPages >= 1">
          <button
            class="arrow"
            @click="changePage(currentPage - 1)"
            :disabled="currentPage === 1"
          >
            ←
          </button>

          <button
            v-for="p in totalPages"
            :key="p"
            @click="changePage(p)"
            :class="{ active: currentPage === p }"
          >
            {{ p }}
          </button>

          <button
            class="arrow"
            @click="changePage(currentPage + 1)"
            :disabled="currentPage === totalPages"
          >
            →
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "@/services/api";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { computed } from "vue";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();
const router = useRouter();
const auth = useAuthStore();
const userId = computed(() => auth.userId);
const keyword = ref("");
const orders = ref([]);
const error = ref("");
const loading = ref(false);
const searched = ref(false);

const currentPage = ref(1);
const pageSize = 5;

const totalPages = computed(() => Math.ceil(orders.value.length / pageSize));

const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return sortedOrders.value.slice(start, start + pageSize);
});

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

const formatMoney = (value) => {
  if (!value) return "0 đ";
  return new Intl.NumberFormat("vi-VN").format(value) + " đ";
};

const formatDate = (date) => {
  if (!date) return "";
  return new Date(date).toLocaleDateString("vi-VN");
};

const STATUS_MAP = {
  0: { text: "Chờ xác nhận", class: "status-pending" },
  1: { text: "Đã xác nhận", class: "status-confirmed" },
  2: { text: "Đang đóng gói", class: "status-packing" },
  3: { text: "Đang giao hàng", class: "status-shipping" },
  4: { text: "Giao thành công", class: "status-success" },
  5: { text: "Hủy", class: "status-cancel" },
  6: { text: "Hoàn trả", class: "status-return" },
  7: { text: "Lỗi", class: "status-error" }, // Thêm dòng này
};
const sortedOrders = computed(() => {
  return [...orders.value].sort((a, b) => {
    const statusA = Number(a.trangThai);
    const statusB = Number(b.trangThai);

    const isDoneA = [4, 5, 6, 7].includes(statusA);
    const isDoneB = [4, 5, 6, 7].includes(statusB);

    // 1. Nhóm chưa hoàn thành lên trước
    if (isDoneA !== isDoneB) {
      return isDoneA ? 1 : -1;
    }

    // 2. Nếu đều là nhóm chưa hoàn thành → sort theo ngày mới nhất
    if (!isDoneA && !isDoneB) {
      return new Date(b.ngayTao) - new Date(a.ngayTao);
    }

    // 3. Nếu đều thuộc nhóm hoàn thành
    // Ưu tiên status = 4
    if (statusA === 4 && statusB !== 4) return -1;
    if (statusB === 4 && statusA !== 4) return 1;

    // 4. 5,6,7 như nhau → sort theo ngày (mới nhất trước)
    return new Date(b.ngayTao) - new Date(a.ngayTao);
  });
});
const lookup = async () => {
  const trimmed = keyword.value.trim();
  if (!trimmed && !userId.value) {
    error.value = "Vui lòng nhập email hoặc số điện thoại";
    return;
  }
  orders.value = [];
  error.value = "";
  searched.value = true;

  try {
    loading.value = true;

    const params = trimmed
      ? { keyword: trimmed }
      : userId.value
        ? { userId: userId.value }
        : {};

    if (!Object.keys(params).length) return;

    const res = await api.get("/orders/lookup", { params });

    orders.value = res.data.content || [];
    currentPage.value = 1;

    if (!orders.value.length) {
      error.value = "Không tìm thấy đơn hàng";
    }
  } catch (err) {
    error.value = "Có lỗi xảy ra khi tra cứu";
  } finally {
    loading.value = false;
  }
};

const viewDetail = (id) => {
  router.push(`/orders/view/${id}`);
};
const autoLoadUserOrders = async () => {
  if (!userId.value) return;

  try {
    loading.value = true;
    searched.value = true;

    const res = await api.get("/orders/lookup", {
      params: { userId: userId.value },
    });

    orders.value = res.data.content || [];
    currentPage.value = 1;
  } catch (err) {
    error.value = "Không thể tải đơn hàng";
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  if (userId.value) {
    autoLoadUserOrders();
  }
});
</script>
<style scoped>
.lookup-wrapper {
  min-height: 100vh;
  background: #f6f7f9;
  display: flex;
  justify-content: center;
  padding: 40px 16px;
}

.lookup-card {
  width: 100%;
  max-width: 1000px;
  background: #fff;
  border-radius: 10px;
  padding: 28px;
  border: 1px solid #e6e6e6;
}

/* header */

.lookup-header {
  text-align: center;
  margin-bottom: 24px;
}

.lookup-header h3 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 4px;
}

.lookup-header p {
  font-size: 14px;
  color: #666;
}

/* search */

.search-box {
  display: flex;
  margin-bottom: 20px;
}

.search-box input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-right: none;
  border-radius: 6px 0 0 6px;
  font-size: 14px;
  outline: none;
}

.search-box input:focus {
  border-color: #0d6efd;
}

.search-box button {
  padding: 0 18px;
  border: 1px solid #0d6efd;
  background: #0d6efd;
  color: white;
  border-radius: 0 6px 6px 0;
  cursor: pointer;
  font-size: 14px;
}

.search-box button:hover {
  background: #0b5ed7;
}

.search-box button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* message */

.error-box {
  background: #ffe3e3;
  color: #dc3545;
  padding: 10px;
  border-radius: 6px;
  margin-bottom: 16px;
}

.empty-box {
  text-align: center;
  padding: 25px;
  color: #666;
}

/* table */

.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: #f8f9fa;
}

th {
  font-size: 13px;
  font-weight: 600;
  padding: 10px;
}

td {
  font-size: 13px;
  padding: 10px;
}

tbody tr {
  border-bottom: 1px solid #eee;
}

tbody tr:hover {
  background: #fafafa;
}

.money {
  font-weight: 600;
  color: #198754;
}

/* status */

.status-pill {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-pending {
  background: #f1f3f5;
  color: #495057;
}

.status-confirmed {
  background: #e7f1ff;
  color: #0d6efd;
}

.status-packing {
  background: #e6f7ff;
  color: #0dcaf0;
}

.status-shipping {
  background: #fff4e6;
  color: #f59f00;
}

.status-success {
  background: #e6f7ee;
  color: #198754;
}

.status-cancel {
  background: #ffe3e3;
  color: #dc3545;
}

.status-return {
  background: #f1f3f5;
  color: #6c757d;
}

/* button */

.view-btn {
  padding: 5px 12px;
  border-radius: 5px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  font-size: 13px;
}

.view-btn:hover {
  background: #f2f2f2;
}

/* pagination */

.pagination {
  margin-top: 18px;
  display: flex;
  justify-content: center;
  gap: 6px;
}

.pagination button {
  padding: 5px 10px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 5px;
  cursor: pointer;
}

.pagination button:hover {
  background: #f5f5f5;
}

.pagination button.active {
  background: #0d6efd;
  color: white;
  border-color: #0d6efd;
}

.pagination button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
/* ===== DARK MODE LOOKUP ORDER ===== */

.dark .lookup-wrapper {
  background: #0f172a;
}

.dark .lookup-card {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

/* header */

.dark .lookup-header p {
  color: #94a3b8;
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

.dark .search-box button {
  border-color: #2563eb;
  background: #2563eb;
}

.dark .search-box button:hover {
  background: #1d4ed8;
}

/* message */

.dark .error-box {
  background: #3f1d1d;
  color: #f87171;
}

.dark .empty-box {
  color: #94a3b8;
}

/* table */

.dark thead {
  background: #0f172a;
}

.dark tbody tr {
  border-bottom: 1px solid #334155;
}

.dark tbody tr:hover {
  background: #1e293b;
}

.dark th,
.dark td {
  color: #e2e8f0;
}

.dark .money {
  color: #4ade80;
}

/* status */

.dark .status-pending {
  background: #334155;
  color: #cbd5f5;
}

.dark .status-confirmed {
  background: #1e3a8a;
  color: #93c5fd;
}

.dark .status-packing {
  background: #0e7490;
  color: #67e8f9;
}

.dark .status-shipping {
  background: #78350f;
  color: #facc15;
}

.dark .status-success {
  background: #064e3b;
  color: #4ade80;
}

.dark .status-cancel {
  background: #7f1d1d;
  color: #f87171;
}

.dark .status-return {
  background: #334155;
  color: #94a3b8;
}

/* button */

.dark .view-btn {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .view-btn:hover {
  background: #1e293b;
}

/* pagination */

.dark .pagination button {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .pagination button:hover {
  background: #1e293b;
}

.dark .pagination button.active {
  background: #2563eb;
  border-color: #2563eb;
}
/* Tìm đến khu vực status colors và thêm: */
.status-error {
  background: #7f1d1d; /* Đỏ đậm giống trang quản lý bạn gửi */
  color: #ffffff;
} /* Tìm đến khu vực .dark .status-... và thêm: */
.dark .status-error {
  background: rgba(
    220,
    38,
    38,
    0.3
  ); /* Đỏ nhạt nền trong suốt cho dễ nhìn trong darkmode */
  color: #f87171;
  border: 1px solid rgba(220, 38, 38, 0.5);
}
</style>
