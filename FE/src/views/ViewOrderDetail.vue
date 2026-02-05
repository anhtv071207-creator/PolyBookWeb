<template>
  <div class="container py-4">
    <button class="btn btn-link mb-3" @click="router.back()">
      ← Quay lại
    </button>

    <div v-if="loading">Đang tải...</div>
    <div v-else-if="error" class="text-danger">{{ error }}</div>

    <div v-else>
      <h4>Chi tiết đơn hàng</h4>

      <p><b>Mã đơn:</b> {{ order.id }}</p>
      <p><b>Ngày tạo:</b> {{ order.ngayTao }}</p>
      <p><b>Trạng thái:</b> {{ order.trangThai }}</p>
      <p><b>Tổng tiền:</b> {{ order.tongTien }}</p>

      <hr />

      <h5>Người nhận</h5>
      <p>{{ order.hoTenNguoiNhan }}</p>
      <p>{{ order.phone }} – {{ order.email }}</p>
      <p>
        {{ order.diaChiNhanHang }},
        {{ order.phuongXa }},
        {{ order.quanHuyen }},
        {{ order.tinhThanh }},
        {{ order.quocGia }}
      </p>

      <hr />

      <h5>Sản phẩm</h5>
      <table class="table">
        <thead>
          <tr>
            <th>Tên sách</th>
            <th>Số lượng</th>
            <th>Đơn giá</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in order.items" :key="item.id">
            <td>{{ item.tenSach }}</td>
            <td>{{ item.soLuong }}</td>
            <td>{{ item.donGia }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/services/api";

const route = useRoute();
const router = useRouter();

const order = ref(null);
const loading = ref(true);
const error = ref("");

const loadDetail = async () => {
  try {
    const res = await api.get(`/orders/${route.params.id}`);
    order.value = res.data;
  } catch {
    error.value = "Không tìm thấy đơn hàng";
  } finally {
    loading.value = false;
  }
};

onMounted(loadDetail);
</script>