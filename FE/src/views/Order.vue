<template>
  <div class="container checkout-page my-4">
    <div class="checkout-box mb-3">
      <div class="checkout-title">
        <h5>Địa chỉ giao hàng</h5>
      </div>

      <div class="row g-2">
        <div class="row g-2 align-items-center">
          <div class="col-3 label-form"><label>Họ tên người nhận</label></div>
          <div class="col-9">
            <input class="form-control" v-model="orderForm.fullName" />
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form"><label>Email</label></div>
          <div class="col-9">
            <input class="form-control" v-model="orderForm.email" />
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form"><label>Số điện thoại</label></div>
          <div class="col-9">
            <input class="form-control" v-model="orderForm.phone" />
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form"><label>Quốc gia</label></div>
          <div class="col-9">
            <select class="form-select" disabled>
              <option>Việt Nam</option>
            </select>
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form"><label>Tỉnh / Thành</label></div>
          <div class="col-9">
            <select class="form-select" v-model="selectedProvince" @change="onProvinceChange">
              <option value="">-- Chọn tỉnh / thành --</option>
              <option v-for="p in provinces" :key="p.code" :value="p">
                {{ p.name }}
              </option>
            </select>
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form"><label>Quận / Huyện</label></div>
          <div class="col-9">
            <select
              class="form-select"
              v-model="selectedDistrict"
              @change="onDistrictChange"
              :disabled="!districts.length"
            >
              <option value="">-- Chọn quận / huyện --</option>
              <option v-for="d in districts" :key="d.code" :value="d">
                {{ d.name }}
              </option>
            </select>
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form"><label>Phường / Xã</label></div>
          <div class="col-9">
            <select
              class="form-select"
              v-model="selectedWard"
              :disabled="!wards.length"
            >
              <option value="">-- Chọn phường / xã --</option>
              <option v-for="w in wards" :key="w.code" :value="w">
                {{ w.name }}
              </option>
            </select>
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form"><label>Địa chỉ nhận</label></div>
          <div class="col-9">
            <input class="form-control" v-model="orderForm.address" />
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form"><label>Thời gian nhận</label></div>
          <div class="col-9">
            <input type="datetime-local" class="form-control" v-model="orderForm.thoiGianNhan" />
          </div>
        </div>
      </div>
    </div>

    <div class="checkout-box mb-3">
      <div class="checkout-title">Phương thức thanh toán</div>
      <label class="radio-row" v-for="m in paymentMethods" :key="m.value">
        <input type="radio" name="payment" :value="m.value" v-model="orderForm.paymentMethod" />
        {{ m.label }}
      </label>
    </div>

    <div class="checkout-box">
      <div class="checkout-title">Kiểm tra lại đơn hàng</div>

      <table class="order-table">
        <tr v-for="item in cartItems" :key="item.id">
          <td class="img"><img :src="item.image" /></td>
          <td>{{ item.name }}</td>
          <td>{{ formatPrice(item.price) }}</td>
          <td>{{ item.qty }}</td>
          <td>{{ formatPrice(item.price * item.qty) }}</td>
        </tr>
      </table>

      <div class="summary">
        <div><span>Thành tiền</span><span>{{ formatPrice(subTotal) }}</span></div>
        <div><span>Phí ship</span><span>{{ formatPrice(SHIPPING_FEE) }}</span></div>
        <div class="sum"><span>Tổng</span><span>{{ formatPrice(totalPrice) }}</span></div>
      </div>

      <div class="row g-2">
        <div class="col-6">
          <label><input type="checkbox" v-model="agree" /> Đồng ý điều khoản</label>
        </div>
        <div class="col-6">
          <button class="btn btn-dark w-100" :disabled="!agree" @click="placeOrder">
            Đặt hàng
          </button>
        </div>
      </div>
    </div>
  </div>

  <div v-if="showToast" class="toast-overlay">
    <div class="toast-box">
      <div class="toast-icon">✔</div>
      <div class="toast-text">{{ toastMessage }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import axios from "axios";
import { getItems } from "@/utils/cart";
import api from "@/services/api";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();
const SHIPPING_FEE = 10000;

const cartItems = ref([]);
onMounted(() => {
  cartItems.value = getItems();
  loadProvinces();
});

const subTotal = computed(() =>
  cartItems.value.reduce((s, i) => s + i.price * i.qty, 0)
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
  thoiGianNhan: "",
  paymentMethod: "COD",
});

const provinces = ref([]);
const districts = ref([]);
const wards = ref([]);

const selectedProvince = ref("");
const selectedDistrict = ref("");
const selectedWard = ref("");

const loadProvinces = async () => {
  const res = await axios.get("https://provinces.open-api.vn/api/p/");
  provinces.value = res.data;
};

const onProvinceChange = async () => {
  districts.value = [];
  wards.value = [];
  selectedDistrict.value = "";
  selectedWard.value = "";

  if (!selectedProvince.value) return;

  orderForm.value.tinhThanh = selectedProvince.value.name;

  const res = await axios.get(
    `https://provinces.open-api.vn/api/p/${selectedProvince.value.code}?depth=2`
  );
  districts.value = res.data.districts;
};

const onDistrictChange = async () => {
  wards.value = [];
  selectedWard.value = "";

  if (!selectedDistrict.value) return;

  orderForm.value.quanHuyen = selectedDistrict.value.name;

  const res = await axios.get(
    `https://provinces.open-api.vn/api/d/${selectedDistrict.value.code}?depth=2`
  );
  wards.value = res.data.wards;
};

watch(selectedWard, (v) => {
  if (v) orderForm.value.phuongXa = v.name;
});

/* ===== ORDER ===== */
const agree = ref(false);
const showToast = ref(false);
const toastMessage = ref("");

const paymentMethods = [
  { value: "COD", label: "Thanh toán khi nhận hàng" },
  { value: "MOMO", label: "Ví Momo" },
  { value: "VNPAY", label: "Ví VNPay" },
];

const placeOrder = async () => {
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
    thoiGianNhan: orderForm.value.thoiGianNhan,
    paymentMethod: orderForm.value.paymentMethod,
    tongTien: totalPrice.value,
    items: cartItems.value.map(i => ({
      bookId: i.id,
      soLuong: i.qty,
      donGia: i.price
    }))
  };

  try {
    await api.post("/orders", payload);
    toastMessage.value = "Đặt hàng thành công";
    showToast.value = true;
    localStorage.removeItem("cart");
    cartItems.value = [];
    setTimeout(() => (showToast.value = false), 2000);
  } catch {
    toastMessage.value = "Đặt hàng thất bại";
    showToast.value = true;
    setTimeout(() => (showToast.value = false), 2000);
  }
};
</script>

<style scoped>
.checkout-page { font-size: 14px; }
.label-form { text-align: left; }
.checkout-box { border: 1px solid #bfbfbf; padding: 12px; }
.checkout-title { font-weight: 600; margin-bottom: 8px; border-bottom: 1px solid #bfbfbf; }
.order-table img { width: 60px; }
.summary { width: 280px; margin-left: auto; }
.summary div { display: flex; justify-content: space-between; }
.summary .sum { font-weight: 600; border-top: 1px solid #ccc; }
.toast-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,.4);
  display: flex; align-items: center; justify-content: center;
}
.toast-box { background: #222; color: #fff; padding: 24px; border-radius: 8px; }
.toast-icon {
  width: 48px; height: 48px; background: #4caf50;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  margin: 0 auto 12px;
}
</style>
