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
        <button
          class="btn btn-error"
          :disabled="!canError"
          @click="changeStatus(7)"
        >
          Lỗi
        </button>
      </div>
    </div>

    <div class="action-bottom">
      <button class="btn-back" @click="goBack">Quay lại</button>
      <button class="btn-delete" @click="deleteOrder(order.id)">Xóa</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();

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
const canError = computed(() => ![4,5,6,7].includes(order.value.trangThai));

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
const goBack = () => {
  router.push("/management/orders");
};
</script>

<style scoped>
.order-detail {
  max-width: 1000px;
  margin: auto;
  padding: 30px;
  background: #f6f7f9;
  min-height: 100vh;
}

h3 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #333;
}

/* CARD */

.card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid #e5e5e5;
}

.card-header {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #333;
}

.card-body p {
  margin-bottom: 6px;
  font-size: 14px;
}

/* PRODUCT */

.product-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 6px;
}

.product-image {
  width: 50px;
  height: 70px;
  object-fit: cover;
  border-radius: 4px;
}

.product-name {
  font-weight: 600;
  font-size: 14px;
}

.product-price {
  font-size: 13px;
  color: #666;
}

/* STATUS */

.status-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.status-actions .btn {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  border: 1px solid #dcdcdc;
  background: white;
  cursor: pointer;
}

.status-actions .btn:hover:not(:disabled) {
  background: #f2f2f2;
}

.status-actions .btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.status-actions .btn {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  border: 1px solid #dcdcdc;
  background: #f8f8f8;
  color: #333;
  cursor: pointer;
}
.status-error {
  color: #991b1b;
  font-weight: 600;
}
.btn-error {
  background: #991b1b;
  color: white;
  border: none;
}

.btn-error:hover:not(:disabled) {
  background: #7f1d1d;
}

.status-actions .btn:hover:not(:disabled) {
  background: #eeeeee;
}

/* BOTTOM ACTION */

.action-bottom {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-back,
.btn-delete {
  padding: 8px 16px;
  border-radius: 6px;
  border: 1px solid #ccc;
  font-size: 13px;
  cursor: pointer;
  background: white;
}

.btn-back:hover {
  background: #f2f2f2;
}

.btn-delete {
  background: #dc2626;
  color: white;
  border: none;
}

.btn-delete:hover {
  background: #b91c1c;
}
</style>
