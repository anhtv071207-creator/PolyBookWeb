<template>
  <section class="order-management">
    <h2>Quản lý đơn hàng</h2>

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
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "@/services/api";

const router = useRouter();
const orders = ref([]);

const fetchOrder = async () => {
  const res = await api.get("/management/orders");
  orders.value = res.data;
};

onMounted(fetchOrder);

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
  padding: 24px;
}

.order-table {
  width: 100%;
  border-collapse: collapse;
}

.order-table th,
.order-table td {
  border: 1px solid #333;
  padding: 8px;
  text-align: center;
}

.status-pill {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
  color: #fff;
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

.btn-edit {
  padding: 4px 12px;
  background: #222;
  color: #fff;
  border: none;
  cursor: pointer;
}

.empty {
  text-align: center;
  padding: 20px;
}
</style>
