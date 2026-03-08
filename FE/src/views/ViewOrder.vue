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
            <tr v-for="o in paginatedOrders" :key="o.id">
              <td>#{{ o.id }}</td>
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
  return orders.value.slice(start, start + pageSize);
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
};

const lookup = async () => {
  const trimmed = keyword.value.trim();

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
  background: #f4f6f9;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 16px;
}

.lookup-card {
  width: 100%;
  max-width: 1100px;
  background: #fff;
  border-radius: 14px;
  padding: 32px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);

  min-height: 550px;
  display: flex;
  flex-direction: column;
}

.lookup-header {
  text-align: center;
  margin-bottom: 30px;
}

.lookup-header h3 {
  margin-bottom: 6px;
  font-weight: 600;
}

.lookup-header p {
  color: #6c757d;
  font-size: 14px;
}

.search-box {
  display: flex;
  gap: 0;
  margin-bottom: 20px;
}

.search-box input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #dee2e6;
  border-right: none;
  border-radius: 8px 0 0 8px;
  outline: none;
  font-size: 14px;
}

.search-box input:focus {
  border-color: #0d6efd;
}

.search-box button {
  padding: 0 20px;
  border: none;
  background: #0d6efd;
  color: #fff;
  font-weight: 500;
  border-radius: 0 8px 8px 0;
  cursor: pointer;
  transition: 0.2s;
}

.search-box button:hover {
  background: #0b5ed7;
}

.search-box button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.spinner {
  width: 18px;
  height: 18px;
  border: 3px solid #fff;
  border-top: 3px solid transparent;
  border-radius: 50%;
  display: inline-block;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-box {
  background: #ffe3e3;
  color: #dc3545;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.empty-box {
  text-align: center;
  padding: 30px;
  color: #6c757d;
}

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

th,
td {
  padding: 12px 10px;
  text-align: center;
  font-size: 14px;
}

tbody tr {
  border-bottom: 1px solid #eee;
  transition: 0.2s;
}

tbody tr:hover {
  background: #f9fbff;
}

.money {
  font-weight: 600;
  color: #198754;
}

.status-pill {
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  color: #fff;
}

.view-btn {
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #dee2e6;
  background: #fff;
  cursor: pointer;
  transition: 0.2s;
}

.view-btn:hover {
  background: #0d6efd;
  color: #fff;
  border-color: #0d6efd;
}
/* Pagination */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 8px;
}

.pagination button {
  padding: 6px 12px;
  border: 1px solid #dee2e6;
  background: #fff;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.2s;
  min-width: 36px;
}

.pagination button:hover {
  background: #f0f4ff;
}

.pagination button.active {
  background: #0d6efd;
  color: #fff;
  border-color: #0d6efd;
}

.pagination button.arrow {
  font-weight: bold;
}

.pagination button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
.status-pending {
  background: #6c757d;
}

.status-confirmed {
  background: #0d6efd;
}

.status-packing {
  background: #0dcaf0;
}

.status-shipping {
  background: #ffc107;
  color: #000;
}

.status-success {
  background: #198754;
}

.status-cancel {
  background: #dc3545;
}

.status-return {
  background: #adb5bd;
  color: #000;
}
</style>
