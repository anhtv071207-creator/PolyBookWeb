<template>
  <section class="order-management">
<div class="page-header">
  <button class="btn-back" @click="goBackManagement">
    ← Quay lại
  </button>
  <h2>Quản lý sản phẩm</h2>
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
        <tr v-for="order in orders" :key="order.id">
          <td>{{ order.id }}</td>
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
  padding: 40px 5%;
  background: #eef4ff;
  min-height: 100vh;
}

h2 {
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  color: #007bff;
  margin-bottom: 30px;
}

.order-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 15px 35px rgba(0, 123, 255, 0.15);
}

.order-table thead {
  background: #f1f6ff;
}

.order-table th {
  padding: 16px;
  font-weight: 600;
  color: #007bff;
  text-align: center;
  font-size: 14px;
}

.order-table td {
  padding: 16px;
  text-align: center;
  font-size: 14px;
}

.order-table tr {
  transition: 0.2s;
}

.order-table tr:hover {
  background: #f4f9ff;
}

.status-pill {
  padding: 6px 14px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.status-pending {
  background: #e2e8f0;
  color: #334155;
}

.status-confirmed {
  background: #dbeafe;
  color: #007bff;
}

.status-packing {
  background: #cffafe;
  color: #0891b2;
}

.status-shipping {
  background: #fef3c7;
  color: #b45309;
}

.status-success {
  background: #dcfce7;
  color: #15803d;
}

.status-cancel {
  background: #fee2e2;
  color: #dc2626;
}

.status-return {
  background: #e5e7eb;
  color: #374151;
}
.page-header {
  position: relative;
  margin-bottom: 35px;
}

.page-header h2 {
  text-align: center;
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
  background: linear-gradient(135deg, #007bff, #00c6ff);
  color: white;
  transition: 0.25s;
}

.btn-back:hover {
  transform: translateY(-50%) translateY(-2px);
  box-shadow: 0 10px 25px rgba(0, 123, 255, 0.35);
}

.btn-edit {
  padding: 8px 16px;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 600;
  background: linear-gradient(135deg, #007bff, #00c6ff);
  border: none;
  color: white;
  cursor: pointer;
  transition: 0.2s;
}

.btn-edit:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

.empty {
  padding: 30px;
  font-weight: 500;
  color: #64748b;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.pagination button {
  padding: 8px 14px;
  border-radius: 12px;
  border: none;
  background: #f1f6ff;
  cursor: pointer;
  transition: 0.2s;
  min-width: 40px;
  font-weight: 500;
}

.pagination button:hover {
  background: #e2edff;
}

.pagination button.active {
  background: linear-gradient(135deg, #007bff, #00c6ff);
  color: white;
}

.pagination button.arrow {
  font-weight: bold;
  font-size: 16px;
}

.pagination button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
</style>
