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
            <select
              class="form-select"
              v-model="selectedProvince"
              @change="onProvinceChange"
            >
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
      </div>
    </div>

    <div class="checkout-box mb-3">
      <div class="checkout-title">Phương thức thanh toán</div>
      <label class="radio-row" v-for="m in paymentMethods" :key="m.value">
        <input
          type="radio"
          name="payment"
          :value="m.value"
          v-model="orderForm.paymentMethod"
        />
        {{ m.label }}
      </label>
    </div>

    <div class="checkout-box mb-3">
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
    </div>
    <div class="checkout-box">
      <div class="summary">
        <div>
          <span>Thành tiền</span><span>{{ formatPrice(subTotal) }}</span>
        </div>
        <div>
          <span>Phí ship</span><span>{{ formatPrice(SHIPPING_FEE) }}</span>
        </div>
        <div class="sum">
          <span>Tổng</span><span>{{ formatPrice(totalPrice) }}</span>
        </div>
      </div>

      <div class="row g-2">
        <div class="col-6">
          <label
            ><input type="checkbox" v-model="agree" /> Với việc mua hàng bạn đồng ý với <a href="">Điều khoản và điêu kiện của chúng tôi</a></label
          >
        </div>
        <div class="col-6">
          <button
            class="btn btn-dark w-100"
            :disabled="!agree"
            @click="placeOrder"
          >
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
import { getItems } from "@/utils/cart";
import api from "@/services/api";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();
const SHIPPING_FEE = 10000;

const cartItems = ref([]);
onMounted(async () => {
  cartItems.value = getItems();
  loadProvinces();
  console.log(
    "[Checkout] userId =",
    auth.user?.id ?? null,
    "| isLoggedIn =",
    auth.isLoggedIn,
  );
  if (auth.user?.id) {
    await loadUserInfo(auth.user.id);
  }
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
  paymentMethod: "COD",
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
    `https://provinces.open-api.vn/api/p/${selectedProvince.value.code}?depth=2`
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
    `https://provinces.open-api.vn/api/d/${selectedDistrict.value.code}?depth=2`
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
    paymentMethod: orderForm.value.paymentMethod,
    items: cartItems.value.map((i) => ({
      bookId: i.id,
      soLuong: i.qty,
    })),
  };

  try {
    await api.post("/orders", payload);
    toastMessage.value = "Đặt hàng thành công";
    showToast.value = true;
    localStorage.removeItem("cart");
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
    p => p.name === data.tinhThanh
  );

  if (selectedProvince.value) {
    await onProvinceChange();
    selectedDistrict.value = districts.value.find(
      d => d.name === data.quanHuyen
    );
  }

  if (selectedDistrict.value) {
    await onDistrictChange();
    selectedWard.value = wards.value.find(
      w => w.name === data.phuongXa
    );
  }
};

</script>

<style scoped>
.checkout-page {
  font-size: 14px;
}
.label-form {
  text-align: left;
}
.checkout-box {
  border: 1px solid #bfbfbf;
  padding: 12px;
  
}
.radio-row {
  display: block;      
  margin-bottom: 8px;
  text-align: left;
}

.checkout-title {
  font-weight: 600;
  margin-bottom: 8px;
  border-bottom: 1px solid #bfbfbf;
}
.order-table img {
  width: 60px;
}
.summary {
  width: 280px;
  margin-left: auto;
  font-size: 18px;
}
.summary div {
  display: flex;
  justify-content: space-between;
}
.summary .sum {
  font-size : 20px;
  font-weight: 700;
  border-top: 1px solid #ccc;
  color: #ff0000;
}
.toast-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
}
.toast-box {
  background: #222;
  color: #fff;
  padding: 24px;
  border-radius: 8px;
}
.toast-icon {
  width: 48px;
  height: 48px;
  background: #4caf50;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
}
.order-table {
  width: 100%;
  border-collapse: collapse;
}

.order-table td {
  vertical-align: middle;
}

.order-table td.img {
  width: 70px;
}

.order-table td.name {
  width: 45%;
}

.order-table td.price {
  width: 15%;
  text-align: right;
  padding-right: 24px;
}

.order-table td.qty {
  width: 10%;
  text-align: center;
}

.order-table td.total {
  width: 15%;
  text-align: right;
}

</style>
