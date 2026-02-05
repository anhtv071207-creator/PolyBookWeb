

<template>
  <div class="container py-4">
    <h4>Tra cứu đơn hàng</h4>

    <input
      v-model="keyword"
      class="form-control mb-2"
      placeholder="Nhập email hoặc số điện thoại"
    />

    <button class="btn btn-primary mb-3" @click="lookup">Tra cứu</button>

    <div v-if="error" class="text-danger">{{ error }}</div>

    <table v-if="orders.length" class="table">
      <thead>
        <tr>
          <th>Id</th>
          <th>Mã đơn hàng</th>
          <th>Ngày tạo</th>
          <th>Tổng số tiền</th>
          <th>Trạng thái</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="o in orders" :key="o.id">
          <td>{{ o.id }}</td>
          <td>{{ o.maDonHang }}</td>
          <td>{{ o.ngayTao }}</td>
          <td>{{ o.tongTien }}</td>
          <td>
            <span class="status-pill" :class="statusInfo(o.trangThai).class">
              {{ statusInfo(o.trangThai).text }}
            </span>
          </td>

          <td>
            <button
              class="btn btn-sm btn-outline-secondary"
              @click="viewDetail(o.id)"
            >
              Xem
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script setup>
import { ref } from "vue";
import api from "@/services/api";
import { useRouter } from "vue-router";

const router = useRouter();
const keyword = ref("");
const orders = ref([]);
const error = ref("");

const lookup = async () => {
  if (!keyword.value) return;

  try {
    const res = await api.get("/orders/lookup", {
      params: { keyword: keyword.value },
    });
    orders.value = res.data;
    error.value = "";
  } catch {
    orders.value = [];
    error.value = "Không tìm thấy đơn hàng";
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

const viewDetail = (id) => {
  router.push(`/orders/view/${id}`);
};
</script>
<style scoped>
.status-pill {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
  color: #fff;
  display: inline-block;
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