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
const goBack = () => {
  router.push("/management/orders");
};  
</script>

<style scoped>
/* ===== PAGE ===== */
.order-detail {
  max-width: 1000px;
  margin: auto;
  padding: 40px;
  background: #eef4ff;
  min-height: 100vh;
}

h3 {
  font-size: 24px;
  font-weight: 700;
  color: #007bff;
  margin-bottom: 30px;
}

/* ===== CARD ===== */
.card {
  background: linear-gradient(145deg, #ffffff, #f4f9ff);
  border-radius: 22px;
  padding: 25px;
  margin-bottom: 30px;
  border: 2px solid #007bff;
  box-shadow: 0 15px 35px rgba(0, 123, 255, 0.15);
}

.card-header {
  font-weight: 600;
  font-size: 16px;
  color: #007bff;
  margin-bottom: 15px;
}

.card-body p {
  margin-bottom: 8px;
  font-size: 14px;
}

.kh-info {
  text-align: left;
}

.product-list {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.product-item {
  display: flex;
  gap: 12px;
  padding: 14px;
  border-radius: 16px;
  background: white;
  border: 1px solid #e6eefc;
  width: 260px;
  transition: 0.25s;
}

.product-item:hover {
  background: #f1f6ff;
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 123, 255, 0.2);
}

.product-image {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
}

.product-name {
  font-weight: 600;
  margin-bottom: 4px;
}

.product-price {
  font-size: 13px;
  color: #007bff;
}

.status-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.status-actions .btn {
  padding: 8px 16px;
  border-radius: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.25s;
  border: 2px solid transparent;
}

.status-actions .btn:not(:disabled) {
  border: 2px solid #007bff;
}

.status-actions .btn:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 
    0 0 0 3px rgba(0, 123, 255, 0.25),
    0 8px 20px rgba(0, 123, 255, 0.35);
}

.status-actions .btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  border: 2px solid transparent;
  box-shadow: none;
}

.status-actions .btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-secondary {
  background: #e2e8f0;
  color: #334155;
}

.btn-primary {
  background: linear-gradient(135deg, #007bff, #00c6ff);
  color: white;
}

.btn-info {
  background: #cffafe;
  color: #0891b2;
}

.btn-warning {
  background: #fef3c7;
  color: #b45309;
}

.btn-success {
  background: #dcfce7;
  color: #15803d;
}

.btn-danger {
  background: #fee2e2;
  color: #dc2626;
}

.status-actions .btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 123, 255, 0.25);
}

.btn-delete {
  padding: 10px 20px;
  border-radius: 14px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  background: crimson;
  color: white;
  transition: 0.25s;
}

.btn-delete:hover {
  background: #b3002d;
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(220, 20, 60, 0.4);
}
.action-bottom {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 10px;
}

.btn-back {
  padding: 10px 20px;
  border-radius: 14px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  background: linear-gradient(135deg, #007bff, #00c6ff);
  color: white;
  transition: 0.25s;
}

.btn-back:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(0, 123, 255, 0.35);
}
</style>
