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
      params: { keyword: keyword.value }
    });
    orders.value = res.data;
    error.value = "";
  } catch {
    orders.value = [];
    error.value = "Không tìm thấy đơn hàng";
  }
};

const viewDetail = (id) => {
  router.push(`/orders/${id}`);
};
</script>

<template>
  <div class="container py-4">
    <h4>Tra cứu đơn hàng</h4>

    <input
      v-model="keyword"
      class="form-control mb-2"
      placeholder="Nhập email hoặc số điện thoại"
    />

    <button class="btn btn-primary mb-3" @click="lookup">
      Tra cứu
    </button>

    <div v-if="error" class="text-danger">{{ error }}</div>

    <table v-if="orders.length" class="table">
      <thead>
        <tr>
          <th>Mã</th>
          <th>Ngày</th>
          <th>Tổng tiền</th>
          <th>Trạng thái</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="o in orders" :key="o.id">
          <td>{{ o.id }}</td>
          <td>{{ o.ngayTao }}</td>
          <td>{{ o.tongTien }}</td>
          <td>{{ o.trangThai }}</td>
          <td>
            <button class="btn btn-sm btn-outline-secondary"
                    @click="viewDetail(o.id)">
              Xem
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
