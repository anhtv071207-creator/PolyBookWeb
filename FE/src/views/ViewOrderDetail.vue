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

const route = useRoute();
const router = useRouter();

const order = ref(null);
const loading = ref(true);
const error = ref("");

// ================= STATUS MAP =================
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

// ================= LOAD DATA =================
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

// ================= CONDITIONS =================
const canCancel = computed(() =>
  order.value &&
  [0, 1].includes(order.value.trangThai)
);

const canReturn = computed(() =>
  order.value && order.value.trangThai === 4
);

// ================= ACTIONS =================
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

// ================= FORMAT =================
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
.product-img {
  width: 70px;
  height: 100px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #ddd;
}

.status-pending {
  background: #6c757d;
  color: #fff;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-confirmed {
  background: #0d6efd;
  color: #fff;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-packing {
  background: #0dcaf0;
  color: #fff;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-shipping {
  background: #ffc107;
  color: #000;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-success {
  background: #198754;
  color: #fff;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-cancel {
  background: #dc3545;
  color: #fff;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-return {
  background: #adb5bd;
  color: #000;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-unknown {
  background: #6c757d;
  color: #fff;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}
.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}
.btn-cancel {
  background: #fee2e2;
  color: #dc2626;
  border: none;
  font-weight: 600;
}

.btn-cancel:hover {
  background: #fecaca;
}

.btn-return {
  background: #e2e8f0;
  color: #334155;
  border: none;
  font-weight: 600;
}

.btn-return:hover {
  background: #cbd5e1;
}

</style>