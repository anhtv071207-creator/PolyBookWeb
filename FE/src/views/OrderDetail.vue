<template>
  <div class="order-detail container">
    <h3 class="mb-4">Chi tiết đơn hàng &gt; {{ order.maDonHang }}</h3>

    <div class="card mb-4 kh-info">
      <div class="card-body">
        <p><strong>Tên khách hàng:</strong> {{ order.hoTenNguoiNhan }}</p>
        <p><strong>Số điện thoại:</strong> {{ order.phone }}</p>
        <p><strong>Email:</strong> {{ order.email }}</p>
        <p><strong>Tổng số tiền:</strong> {{ formatMoney(order.tongTien) }}</p>
        <p>
          <strong>Địa chỉ:</strong>
          {{ order.diaChiNhanHang }}, {{ order.phuongXa }},
          {{ order.quanHuyen }}, {{ order.tinhThanh }},
          {{ order.quocGia }}
        </p>
      </div>
    </div>

    <div class="card mb-4">
      <div class="card-header">
        <strong>Sản phẩm</strong>
      </div>
      <div class="card-body">
        <div v-if="orderItems.length" class="product-list">
          <div
            v-for="item in orderItems"
            :key="item.bookId"
            class="product-item"
          >
            <img :src="item.anhBia" alt="" class="product-image" />

            <div class="product-info">
              <div class="product-name">{{ item.tieuDe }}</div>

              <div class="product-price">
                {{ formatMoney(item.donGia) }} x {{ item.soLuong }}
              </div>
            </div>
          </div>
        </div>

        <div v-else class="text-muted">Không có sản phẩm</div>
      </div>
    </div>

    <div class="card mb-4">
      <div class="card-header">
        <strong>Trạng thái</strong>
      </div>
      <div class="card-body status-actions">
        <button class="btn btn-secondary" disabled>Chờ xác nhận</button>

        <button
          class="btn btn-primary"
          :disabled="!canConfirm"
          @click="changeStatus(1)"
        >
          Đã xác nhận
        </button>

        <button
          class="btn btn-info"
          :disabled="!canPacking"
          @click="changeStatus(2)"
        >
          Đang đóng gói
        </button>

        <button
          class="btn btn-warning"
          :disabled="!canShipping"
          @click="changeStatus(3)"
        >
          Đang giao hàng
        </button>

        <button
          class="btn btn-success"
          :disabled="!canSuccess"
          @click="changeStatus(4)"
        >
          Giao thành công
        </button>

        <button
          class="btn btn-danger"
          :disabled="!canCancel"
          @click="changeStatus(5)"
        >
          Hủy đơn hàng
        </button>

        <button
          class="btn btn-secondary"
          :disabled="!canReturn"
          @click="changeStatus(6)"
        >
          Hoàn trả
        </button>
      </div>
    </div>

    <div class="d-flex justify-content-end gap-2">
      <button class="btn-delete" @click="deleteOrder(order.id)">Xóa</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/services/api";

const route = useRoute();
const router = useRouter();

const order = ref({
  id: null,
  maDonHang: "",
  hoTenNguoiNhan: "",
  phone: "",
  email: "",
  tongTien: 0,
  diaChiNhanHang: "",
  phuongXa: "",
  quanHuyen: "",
  tinhThanh: "",
  quocGia: "",
  trangThai: 0,
});

const orderItems = ref([]);

const fetchOrderDetail = async () => {
  const id = route.params.id;

  const res = await api.get(`/management/orders/${id}`);
  order.value = res.data;
  orderItems.value = res.data.items || [];
};

onMounted(fetchOrderDetail);

const canConfirm = computed(() => order.value.trangThai === 0);
const canPacking = computed(() => order.value.trangThai === 1);
const canShipping = computed(() => order.value.trangThai === 2);
const canSuccess = computed(() => order.value.trangThai === 3);

const canCancel = computed(() => ![4, 5, 6].includes(order.value.trangThai));

const canReturn = computed(() => order.value.trangThai === 4);

const changeStatus = async (newStatus) => {
  const id = order.value.id;

  await api.put(`/management/orders/${id}/status`, {
    trangThai: newStatus,
  });

  order.value.trangThai = newStatus;
};


const deleteOrder = async (id) => {
  await api.delete(`/management/orders/${id}`);
  router.push("/management/orders");
};

const formatMoney = (value) => {
  return new Intl.NumberFormat("vi-VN").format(value) + " đ";
};
</script>

<style scoped>
.order-detail {
  max-width: 900px;
}

.product-list {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}
.kh-info{
  text-align: left;
}
.product-item {
  display: flex;
  gap: 12px;
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 6px;
  width: 260px;
}

.product-image {
  width: 60px;
  height: 80px;
  object-fit: cover;
}

.product-name {
  font-weight: 600;
}

.status-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>
