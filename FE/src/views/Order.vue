<template>
  <div class="container checkout-page my-5">
    <div class="row g-4">
      <div class="col-lg-7">
        <div class="checkout-box mb-4">
          <div class="checkout-title">
            <h5><i class="bi bi-geo-alt me-2"></i>Địa chỉ giao hàng</h5>
          </div>

          <div class="row g-3">
            <div class="col-12">
              <label class="form-label">Họ tên người nhận</label>
              <input
                class="form-control"
                v-model="orderForm.fullName"
                placeholder="VD: Nguyễn Văn A"
              />
            </div>

            <div class="col-md-6">
              <label class="form-label">Email</label>
              <input
                type="email"
                class="form-control"
                v-model="orderForm.email"
                placeholder="nva@gmail.com"
              />
            </div>

            <div class="col-md-6">
              <label class="form-label">Số điện thoại</label>
              <input
                class="form-control"
                v-model="orderForm.phone"
                placeholder="0901234xxx"
              />
            </div>

            <div class="col-md-4">
              <label class="form-label">Tỉnh / Thành</label>
              <select
                class="form-select"
                v-model="selectedProvince"
                @change="onProvinceChange"
              >
                <option value="">Chọn tỉnh</option>
                <option v-for="p in provinces" :key="p.code" :value="p">
                  {{ p.name }}
                </option>
              </select>
            </div>

            <div class="col-md-4">
              <label class="form-label">Quận / Huyện</label>
              <select
                class="form-select"
                v-model="selectedDistrict"
                @change="onDistrictChange"
                :disabled="!districts.length"
              >
                <option value="">Chọn huyện</option>
                <option v-for="d in districts" :key="d.code" :value="d">
                  {{ d.name }}
                </option>
              </select>
            </div>

            <div class="col-md-4">
              <label class="form-label">Phường / Xã</label>
              <select
                class="form-select"
                v-model="selectedWard"
                :disabled="!wards.length"
              >
                <option value="">Chọn xã</option>
                <option v-for="w in wards" :key="w.code" :value="w">
                  {{ w.name }}
                </option>
              </select>
            </div>

            <div class="col-12">
              <label class="form-label">Địa chỉ chi tiết</label>
              <input
                class="form-control"
                v-model="orderForm.address"
                placeholder="Số nhà, tên đường..."
              />
            </div>
          </div>
        </div>

        <div class="checkout-box">
          <div class="checkout-title">
            <h5>
              <i class="bi bi-credit-card me-2"></i>Phương thức thanh toán
            </h5>
          </div>

          <div class="payment-grid">
            <label
              class="payment-card"
              v-for="m in paymentMethods"
              :key="m.value"
              :class="{ active: orderForm.phuongThucThanhToan === m.value }"
            >
              <input
                type="radio"
                name="payment"
                :value="m.value"
                v-model="orderForm.phuongThucThanhToan"
                class="d-none"
              />
              <div class="d-flex align-items-center">
                <div class="custom-radio me-3"></div>
                <span class="fw-medium">{{ m.label }}</span>
              </div>
            </label>
          </div>
        </div>
      </div>

      <div class="col-lg-5">
        <div class="checkout-box sticky-box">
          <div class="checkout-title">
            <h5>Đơn hàng của bạn ({{ cartItems.length }})</h5>
          </div>

          <div class="order-items-list">
            <div class="order-item" v-for="item in cartItems" :key="item.id">
              <div class="item-img">
                <img :src="item.image" />
                <span class="item-qty-badge">{{ item.qty }}</span>
              </div>
              <div class="item-info">
                <p class="item-name">{{ item.name }}</p>
                <div class="item-price">
                  <span class="new-price">{{ formatPrice(item.price) }}</span>
                  <span
                    v-if="item.discountPercent > 0"
                    class="old-price ms-2"
                    >{{ formatPrice(item.originalPrice) }}</span
                  >
                </div>
              </div>
              <div class="item-total fw-semibold">
                {{ formatPrice(item.price * item.qty) }}
              </div>
            </div>
          </div>

          <div class="summary-section mt-4">
            <div class="d-flex justify-content-between mb-2">
              <span class="text-muted">Tạm tính</span>
              <span>{{ formatPrice(subTotal) }}</span>
            </div>
            <div class="d-flex justify-content-between mb-3">
              <span class="text-muted">Phí vận chuyển</span>
              <span>{{ formatPrice(SHIPPING_FEE) }}</span>
            </div>
            <div
              class="total-row d-flex justify-content-between align-items-center"
            >
              <span class="fw-bold">Tổng cộng</span>
              <span class="total-amount">{{ formatPrice(totalPrice) }}</span>
            </div>
          </div>

          <div class="agree-box my-3">
            <div class="form-check">
              <input
                class="form-check-input"
                type="checkbox"
                id="agreeCheck"
                v-model="agree"
              />
              <label
                class="form-check-label text-muted"
                for="agreeCheck"
                style="font-size: 0.9rem"
              >
                Tôi đã đọc và đồng ý với điều khoản mua hàng
              </label>
            </div>
          </div>

          <button
            class="btn btn-primary-custom w-100 btn-lg"
            :disabled="!agree"
            @click="placeOrder"
          >
            ĐẶT HÀNG NGAY
          </button>
        </div>
      </div>
    </div>
  </div>

  <transition name="fade">
    <div v-if="showToast" class="toast-custom-overlay">
      <div class="toast-custom-card">
        <div class="icon-circle mb-3">✔</div>
        <p class="mb-0 fw-bold">{{ toastMessage }}</p>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { getItems } from "@/utils/cart";
import api from "@/services/api";
import { useAuthStore } from "@/stores/auth";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();

const auth = useAuthStore();

const SHIPPING_FEE = 10000;
const cartItems = ref([]);

onMounted(async () => {
  loadProvinces();

  console.log(
    "[Checkout] userId =",
    auth.user?.id ?? null,
    "| isLoggedIn =",
    auth.isLoggedIn,
  );

  let rawItems = [];

  if (auth.user?.id) {
    await loadUserInfo(auth.user.id);

    const res = await api.get(`/cart/user/${auth.user.id}`);

    rawItems = res.data.items.map((i) => ({
      id: i.bookId,
      name: i.tieuDe,
      image: i.imageUrl,
      price: Number(i.salePrice),
      originalPrice: Number(i.gia),
      discountPercent: i.discount,
      qty: i.soLuong,
    }));
  } else {
    rawItems = getItems().map((i) => ({
      id: i.id,
      name: i.name,
      image: i.image,

      originalPrice: Number(i.price),

      price: Number(i.discount > 0 ? i.salePrice : i.price),

      discountPercent: i.discount || 0,

      qty: i.qty,
    }));
  }

  cartItems.value = rawItems;
});

const subTotal = computed(() =>
  cartItems.value.reduce((s, i) => s + i.price * i.qty, 0),
);

const totalPrice = computed(() => subTotal.value + SHIPPING_FEE);

const formatPrice = (v) => v.toLocaleString("vi-VN") + " đ";

const orderForm = ref({
  fullName: "",
  email: "",
  phone: "",
  quocGia: "Việt Nam",
  tinhThanh: "",
  quanHuyen: "",
  phuongXa: "",
  address: "",
  phuongThucThanhToan: "COD",
});

const provinces = ref([]);
const districts = ref([]);
const wards = ref([]);

const selectedProvince = ref("");
const selectedDistrict = ref("");
const selectedWard = ref("");

const loadProvinces = async () => {
  const res = await fetch("https://provinces.open-api.vn/api/p/");
  provinces.value = await res.json();
};

const onProvinceChange = async () => {
  districts.value = [];
  wards.value = [];
  selectedDistrict.value = "";
  selectedWard.value = "";

  if (!selectedProvince.value) return;

  orderForm.value.tinhThanh = selectedProvince.value.name;

  const res = await fetch(
    `https://provinces.open-api.vn/api/p/${selectedProvince.value.code}?depth=2`,
  );

  const data = await res.json();

  districts.value = data.districts;
};

const onDistrictChange = async () => {
  wards.value = [];
  selectedWard.value = "";

  if (!selectedDistrict.value) return;

  orderForm.value.quanHuyen = selectedDistrict.value.name;

  const res = await fetch(
    `https://provinces.open-api.vn/api/d/${selectedDistrict.value.code}?depth=2`,
  );

  const data = await res.json();

  wards.value = data.wards;
};

watch(selectedWard, (v) => {
  if (v) orderForm.value.phuongXa = v.name;
});

const agree = ref(true);
const showToast = ref(false);
const toastMessage = ref("");

const paymentMethods = [
  { value: "COD", label: "Thanh toán khi nhận hàng" },
  { value: "MOMO", label: "Ví Momo" },
  { value: "VNPAY", label: "Ví VNPay" },
];

const placeOrder = async () => {
  const error = validateOrder();

  if (error) {
    toastMessage.value = error;
    showToast.value = true;
    setTimeout(() => (showToast.value = false), 2000);
    return;
  }

  const payload = {
    userId: auth.user?.id || null,
    hoTenNguoiNhan: orderForm.value.fullName,
    email: orderForm.value.email,
    phone: orderForm.value.phone,
    quocGia: orderForm.value.quocGia,
    tinhThanh: orderForm.value.tinhThanh,
    quanHuyen: orderForm.value.quanHuyen,
    phuongXa: orderForm.value.phuongXa,
    diaChiNhanHang: orderForm.value.address,
    phuongThucThanhToan: orderForm.value.phuongThucThanhToan,
  };

  if (!auth.user?.id) {
    payload.items = cartItems.value.map((i) => ({
      bookId: i.id,
      soLuong: i.qty,
    }));
  }

  try {
    await api.post("/orders", payload);

    toastMessage.value = "Đặt hàng thành công";
    showToast.value = true;

    if (!auth.user?.id) {
      localStorage.removeItem("cart");
    }

    cartItems.value = [];

    setTimeout(() => (showToast.value = false), 2000);
  } catch (err) {
    toastMessage.value = err.response?.data?.message || "Đặt hàng thất bại";

    showToast.value = true;

    setTimeout(() => (showToast.value = false), 2000);
  }
};

const validateOrder = () => {
  if (!orderForm.value.fullName?.trim())
    return "Vui lòng nhập họ tên người nhận";

  if (!orderForm.value.email?.trim()) return "Vui lòng nhập email";

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (!emailRegex.test(orderForm.value.email)) return "Email không hợp lệ";

  if (!orderForm.value.phone?.trim()) return "Vui lòng nhập số điện thoại";

  const phoneRegex = /^(0|\+84)[0-9]{9}$/;

  if (!phoneRegex.test(orderForm.value.phone))
    return "Số điện thoại không hợp lệ";

  if (!orderForm.value.tinhThanh) return "Vui lòng chọn tỉnh / thành";

  if (!orderForm.value.quanHuyen) return "Vui lòng chọn quận / huyện";

  if (!orderForm.value.phuongXa) return "Vui lòng chọn phường / xã";

  if (!orderForm.value.address?.trim())
    return "Vui lòng nhập địa chỉ nhận hàng";

  if (!cartItems.value.length) return "Giỏ hàng trống";

  if (!agree.value) return "Bạn phải đồng ý điều khoản";

  return null;
};

const loadUserInfo = async (userId) => {
  try {
    const res = await api.get(`/users/${userId}/info`);
    const u = res.data;

    orderForm.value.fullName = u.hoTenNguoiNhan;
    orderForm.value.email = u.email;
    orderForm.value.phone = u.phone;
    orderForm.value.quocGia = u.quocGia || "Việt Nam";
    orderForm.value.tinhThanh = u.tinhThanh;
    orderForm.value.quanHuyen = u.quanHuyen;
    orderForm.value.phuongXa = u.phuongXa;
    orderForm.value.address = u.diaChi;

    await loadUserAddress(u);
  } catch (e) {
    console.warn("Không lấy được UserInfo");
  }
};

const loadUserAddress = async (data) => {
  await loadProvinces();

  selectedProvince.value = provinces.value.find(
    (p) => p.name === data.tinhThanh,
  );

  if (selectedProvince.value) {
    await onProvinceChange();

    selectedDistrict.value = districts.value.find(
      (d) => d.name === data.quanHuyen,
    );
  }

  if (selectedDistrict.value) {
    await onDistrictChange();

    selectedWard.value = wards.value.find((w) => w.name === data.phuongXa);
  }
};
</script>

<style scoped>
/* Layout */
.checkout-page {
  max-width: 1200px;
  font-family: "Inter", sans-serif;
}

/* Box */
.checkout-box {
  background: #fff;
  border-radius: 14px;
  padding: 26px;
  border: 1px solid #e9ecef;
  box-shadow: 0 8px 20px rgba(0,0,0,0.04);
}

.checkout-title {
  margin-bottom: 18px;
  border-bottom: 1px solid #f1f3f5;
  padding-bottom: 10px;
}

.checkout-title h5 {
  font-weight: 700;
  font-size: 1.1rem;
  margin: 0;
}

/* Sticky order summary */
.sticky-box {
  position: sticky;
  top: 90px;
}

/* Form */
.form-label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #495057;
}

.form-control,
.form-select {
  border-radius: 8px;
  border: 1px solid #dee2e6;
  padding: 10px 12px;
  font-size: 0.95rem;
  transition: all 0.2s;
}

.form-control:hover,
.form-select:hover {
  border-color: #adb5bd;
}

.form-control:focus,
.form-select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59,130,246,0.15);
}

/* Payment */
.payment-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-card {
  border: 1.5px solid #e5e7eb;
  padding: 14px 16px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.payment-card:hover {
  background: #f8fafc;
}

.payment-card.active {
  border-color: #2563eb;
  background: #eff6ff;
}

.custom-radio {
  width: 18px;
  height: 18px;
  border: 2px solid #cbd5e1;
  border-radius: 50%;
  position: relative;
}

.payment-card.active .custom-radio {
  border-color: #2563eb;
}

.payment-card.active .custom-radio::after {
  content: "";
  width: 9px;
  height: 9px;
  background: #2563eb;
  border-radius: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

/* Order list */
.order-items-list {
  max-height: 320px;
  overflow-y: auto;
  padding-right: 4px;
}

.order-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f1f3f5;
  align-items: center;
}

.item-img img {
  width: 60px;
  height: 75px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #eee;
}

.item-qty-badge {
  position: absolute;
  top: -7px;
  right: -7px;
  background: #6c757d;
  color: white;
  font-size: 11px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-name {
  font-size: 0.9rem;
  font-weight: 500;
  margin: 0;
}

.new-price {
  font-weight: 600;
}

.old-price {
  font-size: 0.8rem;
  text-decoration: line-through;
  color: #adb5bd;
}

.item-total {
  font-size: 0.9rem;
}

/* Summary */
.summary-section {
  font-size: 0.95rem;
}

.total-row {
  border-top: 2px dashed #e9ecef;
  padding-top: 14px;
  margin-top: 8px;
}

.total-amount {
  font-size: 1.6rem;
  font-weight: 800;
  color: #dc2626;
}

/* Button */
.btn-primary-custom {
  background: #2563eb;
  border: none;
  border-radius: 10px;
  padding: 14px;
  font-weight: 700;
  transition: all 0.2s;
}

.btn-primary-custom:hover:not(:disabled) {
  background: #1d4ed8;
}

.btn-primary-custom:disabled {
  opacity: 0.6;
}

/* Toast */
.toast-custom-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.35);
  backdrop-filter: blur(3px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.toast-custom-card {
  background: white;
  padding: 28px 50px;
  border-radius: 14px;
  text-align: center;
  box-shadow: 0 15px 30px rgba(0,0,0,0.15);
}

.icon-circle {
  width: 58px;
  height: 58px;
  background: #22c55e;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  margin: 0 auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity .3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
/* ===== DARK MODE CHECKOUT ===== */

.dark .checkout-box {
  background: #1e293b;
  border-color: #334155;
  box-shadow: none;
}

.dark .checkout-title {
  border-bottom: 1px solid #334155;
}

.dark .checkout-title h5 {
  color: #e2e8f0;
}

/* form */

.dark .form-label {
  color: #cbd5f5;
}

.dark .form-control,
.dark .form-select {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .form-control::placeholder {
  color: #94a3b8;
}

.dark .form-control:focus,
.dark .form-select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59,130,246,0.25);
}

/* payment */

.dark .payment-card {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .payment-card:hover {
  background: #1e293b;
}

.dark .payment-card.active {
  background: #1e3a8a;
  border-color: #3b82f6;
}

.dark .custom-radio {
  border-color: #64748b;
}

/* order items */

.dark .order-item {
  border-bottom: 1px solid #334155;
}

.dark .item-img img {
  border-color: #334155;
}

.dark .item-name {
  color: #e2e8f0;
}

.dark .old-price {
  color: #94a3b8;
}

.dark .new-price {
  color: #f87171;
}

/* summary */

.dark .summary-section {
  color: #e2e8f0;
}

.dark .total-row {
  border-top: 2px dashed #334155;
}

.dark .total-amount {
  color: #f87171;
}

/* checkbox */

.dark .form-check-label {
  color: #cbd5f5 !important;
}

/* toast */

.dark .toast-custom-card {
  background: #1e293b;
  color: #e2e8f0;
}
</style>
