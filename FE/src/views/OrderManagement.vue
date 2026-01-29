<template>
  <section class="order-management">
    <h2>Quản lý đơn hàng</h2>

    <table class="order-table">
      <thead>
        <tr>
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
          <td>{{ order.maDonHang }}</td>

          <td>
            <span class="status-pill">
              {{ mapTrangThai(order.trangThai) }}
            </span>
          </td>

          <td>{{ order.tenKhachHang }}</td>

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

const mapTrangThai = (value) => {
  switch (value) {
    case 0:
      return "Chờ xác nhận";
    case 1:
      return "Đã xác nhận";
    case 2:
      return "Đang đóng gói";
    case 3:
      return "Đang giao hàng";
    case 4:
      return "Giao thành công";
    case 5:
      return "Hủy";
    case 6:
      return "Hoàn trả";
    default:
      return "Không xác định";
  }
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
  background: #f1f1f1;
  font-size: 13px;
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
