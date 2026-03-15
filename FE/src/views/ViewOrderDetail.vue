<template>
  <div class="container py-4 order-detail">
    <button class="btn btn-link mb-3" @click="router.back()">
      ← Quay lại
    </button>

    <div v-if="loading" class="text-center py-5">
      Đang tải...
    </div>

    <div v-else-if="error" class="alert alert-danger">
      {{ error }}
    </div>

    <div v-else class="card shadow-sm p-4">
      <h4 class="mb-3">Chi tiết đơn hàng</h4>

      <div class="row mb-3">
        <div class="col-md-6">
          <p><b>Mã đơn:</b> #{{ order.id }}</p>
          <p><b>Ngày tạo:</b> {{ formatDate(order.ngayTao) }}</p>
        </div>

        <div class="col-md-6 text-md-end">
          <p>
            <b>Trạng thái:</b>
            <span :class="statusInfo.class">
              {{ statusInfo.text }}
            </span>
          </p>
          <p class="text-danger fw-bold">
            Tổng tiền: {{ formatPrice(order.tongTien) }}
          </p>
        </div>
      </div>

      <hr />

      <h5>Thông tin người nhận</h5>
      <p class="mb-1"><b>{{ order.hoTenNguoiNhan }}</b></p>
      <p class="mb-1">{{ order.phone }} – {{ order.email }}</p>
      <p>
        {{ order.diaChiNhanHang }},
        {{ order.phuongXa }},
        {{ order.quanHuyen }},
        {{ order.tinhThanh }},
        {{ order.quocGia }}
      </p>

      <hr />

      <h5>Sản phẩm</h5>

      <div
        v-for="item in order?.items || []"
        :key="item.bookId"
        class="product-item d-flex align-items-center mb-3"
      >
        <img
          :src="getImageUrl(item.anhBia)"
          class="product-img"
          alt="book"
        />

        <div class="flex-grow-1 ms-3">
          <div class="fw-semibold">{{ item.tieuDe }}</div>
          <div>Số lượng: {{ item.soLuong }}</div>
        </div>

        <div class="text-end">
          <div>{{ formatPrice(item.donGia) }}</div>
          <div class="fw-bold text-danger">
            {{ formatPrice(item.thanhTien) }}
          </div>
        </div>
      </div>

      <div class="mt-4 d-flex gap-2">
<button
  class="btn btn-cancel"
  :disabled="!canCancel"
  @click="cancelOrder"
>
  Hủy đơn
</button>

<button
  class="btn btn-return"
  :disabled="!canReturn"
  @click="returnOrder"
>
  Hoàn trả
</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();
const route = useRoute();
const router = useRouter();

const order = ref(null);
const loading = ref(true);
const error = ref("");

const STATUS_MAP = {
  0: { text: "Chờ xác nhận", class: "status-pending" },
  1: { text: "Đã xác nhận", class: "status-confirmed" },
  2: { text: "Đang đóng gói", class: "status-packing" },
  3: { text: "Đang giao hàng", class: "status-shipping" },
  4: { text: "Giao thành công", class: "status-success" },
  5: { text: "Hủy", class: "status-cancel" },
  6: { text: "Hoàn trả", class: "status-return" },
};

const statusInfo = computed(() => {
  if (!order.value) return { text: "", class: "" };

  return (
    STATUS_MAP[order.value.trangThai] || {
      text: "Không xác định",
      class: "status-unknown",
    }
  );
});

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

const canCancel = computed(() =>
  order.value &&
  [0, 1].includes(order.value.trangThai)
);

const canReturn = computed(() =>
  order.value && order.value.trangThai === 4
);

const cancelOrder = async () => {
  if (!confirm("Bạn có chắc muốn hủy đơn hàng này?")) return;

  try {
    const res = await api.put(
      `/orders/${order.value.id}/status`,
      { trangThai: 5 }
    );
    order.value = res.data;
  } catch {
    alert("Hủy đơn thất bại");
  }
};

const returnOrder = async () => {
  if (!confirm("Bạn có chắc muốn hoàn trả đơn hàng này?")) return;

  try {
    const res = await api.put(
      `/orders/${order.value.id}/status`,
      { trangThai: 6 }
    );
    order.value = res.data;
  } catch {
    alert("Hoàn trả thất bại");
  }
};


const formatPrice = (price) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};

const formatDate = (date) => {
  return new Date(date).toLocaleString("vi-VN");
};

const getImageUrl = (path) => {
return path;
};
</script>

<style scoped>
.product-item {
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.product-img {
  width: 70px;
  height: 100px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #ddd;
}

/* status */

.status-pending,
.status-confirmed,
.status-packing,
.status-shipping,
.status-success,
.status-cancel,
.status-return,
.status-unknown {
  padding: 5px 12px;
  border-radius: 14px;
  font-size: 12px;
  font-weight: 500;
  display: inline-block;
}

.status-pending {
  background: #f1f3f5;
  color: #495057;
}

.status-confirmed {
  background: #e7f1ff;
  color: #0d6efd;
}

.status-packing {
  background: #e6f7ff;
  color: #0dcaf0;
}

.status-shipping {
  background: #fff4e6;
  color: #f59f00;
}

.status-success {
  background: #e6f7ee;
  color: #198754;
}

.status-cancel {
  background: #ffe3e3;
  color: #dc3545;
}

.status-return {
  background: #f1f3f5;
  color: #6c757d;
}

.status-unknown {
  background: #f1f3f5;
  color: #6c757d;
}

/* buttons */

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-cancel {
  background: #ffe3e3;
  color: #dc3545;
  border: none;
  font-weight: 600;
}

.btn-cancel:hover {
  background: #ffd5d5;
}

.btn-return {
  background: #f1f3f5;
  color: #495057;
  border: none;
  font-weight: 600;
}

.btn-return:hover {
  background: #e9ecef;
}
/* ===== DARK MODE ORDER DETAIL ===== */

.dark .order-detail {
  color: #e2e8f0;
}

.dark .card {
  background: #1e293b;
  border-color: #334155;
}

.dark .product-item {
  border-bottom: 1px solid #334155;
}

.dark .product-img {
  border-color: #334155;
}

/* status */

.dark .status-pending {
  background: #334155;
  color: #cbd5f5;
}

.dark .status-confirmed {
  background: #1e3a8a;
  color: #93c5fd;
}

.dark .status-packing {
  background: #0e7490;
  color: #67e8f9;
}

.dark .status-shipping {
  background: #78350f;
  color: #facc15;
}

.dark .status-success {
  background: #064e3b;
  color: #4ade80;
}

.dark .status-cancel {
  background: #7f1d1d;
  color: #f87171;
}

.dark .status-return {
  background: #334155;
  color: #94a3b8;
}

.dark .status-unknown {
  background: #334155;
  color: #94a3b8;
}

/* buttons */

.dark .btn-cancel {
  background: #7f1d1d;
  color: #f87171;
}

.dark .btn-cancel:hover {
  background: #991b1b;
}

.dark .btn-return {
  background: #334155;
  color: #cbd5f5;
}

.dark .btn-return:hover {
  background: #475569;
}

/* back button */

.dark .btn-link {
  color: #93c5fd;
}
/* ===== FIX TEXT DARK MODE ===== */

.dark .order-detail,
.dark .order-detail p,
.dark .order-detail div,
.dark .order-detail h4,
.dark .order-detail h5 {
  color: #e2e8f0;
}

/* text muted */

.dark .text-muted {
  color: #94a3b8 !important;
}

/* bootstrap text colors */

.dark .text-danger {
  color: #f87171 !important;
}

/* product title */

.dark .product-item .fw-semibold {
  color: #f1f5f9;
}

/* hr line */

.dark hr {
  border-color: #334155;
}
</style>