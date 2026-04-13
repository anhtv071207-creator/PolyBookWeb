<template>
  <section class="order-management">
    <div class="page-header">
      <button class="btn-back" @click="goBackManagement">← Quay lại</button>
      <h2>Quản lý đơn hàng</h2>
    </div>

    <table class="order-table">
      <thead>
        <tr>
          <th>Id</th>
          <th>Mã đơn hàng</th>
          <th>Trạng thái</th>
          <th>Khách hàng</th>
          <th>Sản phẩm</th>
          <th>Tổng số tiền</th>
          <th>Ngày tạo</th>
          <th>Hành động</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="(order, index) in orders" :key="order.id">
          <td>{{ currentPage * pageSize + index + 1 }}</td>
          <td>{{ order.maDonHang }}</td>

          <td>
            <span
              class="status-pill"
              :class="statusInfo(order.trangThai).class"
            >
              {{ statusInfo(order.trangThai).text }}
            </span>
          </td>

          <td>{{ order.hoTenNguoiNhan }}</td>

          <td>{{ order.sanPhamTomTat }}</td>

          <td>{{ formatMoney(order.tongTien) }}</td>

          <td>{{ formatDate(order.ngayTao) }}</td>

          <td>
            <button class="btn-edit" @click="editOrder(order.id)">
              Chỉnh sửa
            </button>
          </td>
        </tr>

        <tr v-if="orders.length === 0">
          <td colspan="7" class="empty">Không có đơn hàng</td>
        </tr>
      </tbody>
    </table>
    <div class="pagination" v-if="totalPages > 1">
      <button
        class="arrow"
        @click="changePage(currentPage - 1)"
        :disabled="currentPage === 0"
      >
        ←
      </button>

      <button
        v-for="p in totalPages"
        :key="p"
        @click="changePage(p - 1)"
        :class="{ active: currentPage === p - 1 }"
      >
        {{ p }}
      </button>

      <button
        class="arrow"
        @click="changePage(currentPage + 1)"
        :disabled="currentPage >= totalPages - 1"
      >
        →
      </button>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();

const router = useRouter();

const orders = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = 10;

const fetchOrder = async (page = 0) => {
  const res = await api.get("/management/orders", {
    params: {
      page,
      size: pageSize,
    },
  });

  orders.value = res.data.content;
  totalPages.value = res.data.totalPages;
  currentPage.value = res.data.number;
};
const goBackManagement = () => {
  router.push("/managements");
};
onMounted(() => fetchOrder());

const changePage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    fetchOrder(page);
  }
};

const STATUS_MAP = {
  0: { text: "Chờ xác nhận", class: "status-pending" },
  1: { text: "Đã xác nhận", class: "status-confirmed" },
  2: { text: "Đang đóng gói", class: "status-packing" },
  3: { text: "Đang giao hàng", class: "status-shipping" },
  4: { text: "Giao thành công", class: "status-success" },
  5: { text: "Hủy", class: "status-cancel" },
  6: { text: "Hoàn trả", class: "status-return" },
  7: { text: "Lỗi", class: "status-error" },   
};

const statusInfo = (value) => {
  return STATUS_MAP[value] || { text: "Không xác định", class: "" };
};

const formatMoney = (value) => new Intl.NumberFormat("vi-VN").format(value);

const formatDate = (value) =>
  value ? new Date(value).toLocaleDateString("vi-VN") : "";

const editOrder = (id) => {
  router.push(`/management/orders/${id}`);
};
</script>

<style scoped>
.order-management {
  padding: 32px 5%;
  background: #f6f7f9;
  min-height: 100vh;
}

/* header */

.page-header {
  position: relative;
  margin-bottom: 24px;
}

.page-header h2 {
  text-align: center;
  margin: 0;
  font-size: 22px;
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
  border: 1px solid #dcdcdc;
  background: white;
  cursor: pointer;
}

.btn-back:hover {
  background: #f2f2f2;
}

/* table */

.order-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border: 1px solid #e5e5e5;
}

.order-table thead {
  background: #f4f4f4;
}

.order-table th {
  padding: 12px;
  font-weight: 600;
  color: #444;
  font-size: 13px;
  text-align: center;
  border-bottom: 1px solid #e5e5e5;
}

.order-table td {
  padding: 12px;
  font-size: 13px;
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
}

.order-table tbody tr:hover {
  background: #fafafa;
}

/* status */

.status-pill {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

/* status colors */

.status-pending {
  background: #f1f1f1;
  color: #555;
}

.status-confirmed {
  background: #e8f0ff;
  color: #2563eb;
}

.status-packing {
  background: #e6fffb;
  color: #0891b2;
}

.status-shipping {
  background: #fff7e6;
  color: #d97706;
}

.status-success {
  background: #e8f8ee;
  color: #15803d;
}

.status-cancel {
  background: #ffecec;
  color: #dc2626;
}

.status-error {
  background: #7f1d1d;
  color: #fff;
}
.status-return {
  background: #ececec;
  color: #555;
}

/* button */

.btn-edit {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  border: 1px solid #dcdcdc;
  background: white;
  cursor: pointer;
}

.btn-edit:hover {
  background: #f2f2f2;
}

/* empty */

.empty {
  padding: 24px;
  color: #777;
  font-size: 14px;
}

/* pagination */

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
  gap: 6px;
}

.pagination button {
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #dcdcdc;
  background: white;
  cursor: pointer;
  font-size: 13px;
}

.pagination button:hover {
  background: #f2f2f2;
}

.pagination button.active {
  background: #333;
  color: white;
  border-color: #333;
}

.pagination button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
/* ===== DARK MODE ===== */

.dark .order-management {
  background: #0f172a;
}

/* header */

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

/* table */

.dark .order-table {
  background: #1e293b;
  border-color: #334155;
}

.dark .order-table thead {
  background: #0f172a;
}

.dark .order-table th {
  color: #e2e8f0;
  border-bottom: 1px solid #334155;
}

.dark .order-table td {
  color: #e2e8f0;
  border-bottom: 1px solid #334155;
}

.dark .order-table tbody tr:hover {
  background: #334155;
}

/* status pills */

.dark .status-pending {
  background: #334155;
  color: #e2e8f0;
}

.dark .status-confirmed {
  background: rgba(59,130,246,0.2);
  color: #60a5fa;
}

.dark .status-packing {
  background: rgba(6,182,212,0.2);
  color: #22d3ee;
}

.dark .status-shipping {
  background: rgba(245,158,11,0.2);
  color: #fbbf24;
}

.dark .status-success {
  background: rgba(34,197,94,0.2);
  color: #4ade80;
}

.dark .status-cancel {
  background: rgba(239,68,68,0.2);
  color: #f87171;
}

.dark .status-return {
  background: #334155;
  color: #e2e8f0;
}

/* button */

.dark .btn-edit {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .btn-edit:hover {
  background: #334155;
}

/* empty */

.dark .empty {
  color: #94a3b8;
}

/* pagination */

.dark .pagination button {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .pagination button:hover {
  background: #334155;
}

.dark .pagination button.active {
  background: #475569;
  border-color: #475569;
  color: white;
}
</style>
