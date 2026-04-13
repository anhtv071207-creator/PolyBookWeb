<template>
  <div class="order-detail container">
    <div class="header-top">
      <button class="btn-back" @click="goBack">← Quay lại</button>
      <h3>Chi tiết đơn hàng &gt; {{ order.maDonHang }}</h3>
    </div>
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
                <span class="unit-price">
                  {{ formatMoney(item.donGia) }} x {{ item.soLuong }}
                </span>

                <span class="total-price">
                  {{ formatMoney(item.donGia * item.soLuong) }}
                </span>
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
          @click="openConfirm(1)"
        >
          Đã xác nhận
        </button>

        <button
          class="btn btn-info"
          :disabled="!canPacking"
          @click="openConfirm(2)"
        >
          Đang đóng gói
        </button>

        <button
          class="btn btn-warning"
          :disabled="!canShipping"
          @click="openConfirm(3)"
        >
          Đang giao hàng
        </button>

        <button
          class="btn btn-success"
          :disabled="!canSuccess"
          @click="openConfirm(4)"
        >
          Giao thành công
        </button>

        <button
          class="btn btn-danger"
          :disabled="!canCancel"
          @click="openConfirm(5)"
        >
          Hủy đơn hàng
        </button>

        <button
          class="btn btn-secondary"
          :disabled="!canReturn"
          @click="openConfirm(6)"
        >
          Hoàn trả
        </button>
        <button
          class="btn btn-error"
          :disabled="!canError"
          @click="openConfirm(7)"
        >
          Lỗi
        </button>
      </div>
    </div>

    <div class="action-bottom">
      <!-- <button class="btn-delete" @click="deleteOrder(order.id)">Xóa</button> -->
    </div>
  </div>
  <div v-if="showConfirm" class="confirm-overlay">
    <div class="confirm-box">
      <p>
        Bạn có muốn chuyển trạng thái đơn hàng sang
        <strong>{{ getStatusName(pendingStatus) }}</strong> không?
      </p>

      <div class="confirm-actions">
        <button class="btn-confirm" @click="confirmChangeStatus">Có</button>
        <button class="btn-cancel" @click="cancelConfirm">Không</button>
      </div>
    </div>
  </div>
  <transition name="fade">
    <div v-if="showToast" class="toast-custom-overlay">
      <div class="toast-custom-card">
        <div class="icon-circle">✔</div>
        <p>{{ toastMessage }}</p>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";
const showConfirm = ref(false);
const pendingStatus = ref(null);
const theme = useThemeStore();
const openConfirm = (status) => {
  pendingStatus.value = status;
  showConfirm.value = true;
};

const confirmChangeStatus = async () => {
  const id = order.value.id;

  try {
    await api.put(`/management/orders/${id}/status`, {
      trangThai: pendingStatus.value,
    });

    order.value.trangThai = pendingStatus.value;

    // ===== TOAST =====
    toastMessage.value =
      "Đã chuyển trạng thái sang: " + getStatusName(pendingStatus.value);

    showToast.value = true;
    setTimeout(() => (showToast.value = false), 3000);

    showConfirm.value = false;
  } catch (e) {
    toastMessage.value = "Cập nhật trạng thái thất bại";
    showToast.value = true;
    setTimeout(() => (showToast.value = false), 3000);
  }
};

const cancelConfirm = () => {
  showConfirm.value = false;
  pendingStatus.value = null;
};
const route = useRoute();
const router = useRouter();
const showToast = ref(false);
const toastMessage = ref("");

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
const canError = computed(() => ![4, 5, 6, 7].includes(order.value.trangThai));
const getStatusName = (status) => {
  const map = {
    0: "Chờ xác nhận",
    1: "Đã xác nhận",
    2: "Đang đóng gói",
    3: "Đang giao hàng",
    4: "Giao thành công",
    5: "Hủy đơn hàng",
    6: "Hoàn trả",
    7: "Lỗi",
  };
  return map[status] || "";
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
.confirm-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.confirm-box {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 350px;
  text-align: center;
}

.confirm-actions {
  margin-top: 15px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.btn-confirm {
  background: #16a34a;
  color: white;
  border: none;
  padding: 6px 14px;
  border-radius: 6px;
}

.btn-cancel {
  background: #e5e5e5;
  border: none;
  padding: 6px 14px;
  border-radius: 6px;
}
.toast-custom-overlay {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 99999;
}

.toast-custom-card {
  min-width: 260px;
  padding: 12px 16px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 10px;

  background: #ecfdf5;
  color: #065f46;

  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.icon-circle {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #10b981;
  color: white;

  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
.header-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.header-top h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}
</style>
