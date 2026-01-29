<template>
  <div class="container checkout-page my-4">
    <!-- Địa chỉ giao hàng -->
    <div class="checkout-box mb-3">
      <div class="checkout-title">
        <h5>Địa chỉ giao hàng</h5>
      </div>

      <div class="row g-2">
        <div class="row g-2 align-items-center">
          <div class="col-3 label-form">
            <label>Họ tên người nhận</label>
          </div>
          <div class="col-9">
            <input
              class="form-control"
              v-model="orderForm.fullName"
              placeholder="Nhập họ tên người nhận"
            />
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form">
            <label>Email</label>
          </div>
          <div class="col-9">
            <input
              class="form-control"
              v-model="orderForm.email"
              placeholder="Nhập email"
            />
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form">
            <label>Số điện thoại</label>
          </div>
          <div class="col-9">
            <input
              class="form-control"
              v-model="orderForm.phone"
              placeholder="Nhập số điện thoại"
            />
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form">
            <label>Quốc gia</label>
          </div>
          <div class="col-9">
            <select class="form-select" v-model="orderForm.quocGia">
              <option>Việt Nam</option>
            </select>
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form">
            <label>Tỉnh/Thành phố</label>
          </div>
          <div class="col-9">
            <input class="form-control" v-model="orderForm.tinhThanh" />
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form">
            <label>Quận/Huyện</label>
          </div>
          <div class="col-9">
            <input class="form-control" v-model="orderForm.quanHuyen" />
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form">
            <label>Phường/Xã</label>
          </div>
          <div class="col-9">
            <input class="form-control" v-model="orderForm.phuongXa" />
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form">
            <label>Địa chỉ nhận hàng</label>
          </div>
          <div class="col-9">
            <input
              class="form-control"
              v-model="orderForm.address"
              placeholder="Nhập địa chỉ nhận hàng"
            />
          </div>
        </div>

        <div class="row g-2 align-items-center">
          <div class="col-3 label-form">
            <label>Thời gian nhận</label>
          </div>
          <div class="col-9">
            <input
              type="datetime-local"
              class="form-control"
              v-model="orderForm.thoiGianNhan"
            />
          </div>
        </div>
      </div>
    </div>

    <div class="checkout-box mb-3">
      <div class="checkout-title">Phương thức thanh toán</div>

      <label
        class="radio-row"
        v-for="m in paymentMethods"
        :key="m.value"
      >
        <input
          type="radio"
          name="payment"
          :value="m.value"
          v-model="orderForm.paymentMethod"
        />
        {{ m.label }}
      </label>
    </div>

    <div class="checkout-box">
      <div class="checkout-title">Kiểm tra lại đơn hàng</div>

      <table class="order-table">
        <tr v-for="item in cartItems" :key="item.id">
          <td class="img">
            <img :src="item.image" />
          </td>
          <td class="name">{{ item.name }}</td>
          <td class="price">{{ formatPrice(item.price) }}</td>
          <td class="qty">{{ item.qty }}</td>
          <td class="total">
            {{ formatPrice(item.price * item.qty) }}
          </td>
        </tr>
      </table>

      <div class="summary">
        <div>
          <span>Thành tiền</span>
          <span>{{ formatPrice(subTotal) }}</span>
        </div>
        <div>
          <span>Phí vận chuyển</span>
          <span>{{ formatPrice(SHIPPING_FEE) }}</span>
        </div>
        <div class="sum">
          <span>Tổng số tiền</span>
          <span>{{ formatPrice(totalPrice) }}</span>
        </div>
      </div>

      <div class="row g-2 align-items-center">
        <div class="col-6">
          <label class="agree">
            <input type="checkbox" v-model="agree" />
            Với việc Đặt Hàng bạn đồng ý với
            <a href="#">Điều khoản & Điều kiện của chúng tôi</a>
          </label>
        </div>
        <div class="col-6">
          <button class="btn btn-dark w-100 mt-2" @click="placeOrder">
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
import { ref, computed, onMounted } from "vue";
import { getItems } from "@/utils/cart";
import api from "@/services/api";
import { useAuthStore } from "@/stores/auth";

const showToast = ref(false);
const toastMessage = ref("");

const auth = useAuthStore();
const SHIPPING_FEE = 10000;

const cartItems = ref([]);

onMounted(() => {
  cartItems.value = getItems();
});

const subTotal = computed(() =>
  cartItems.value.reduce((sum, i) => sum + i.price * i.qty, 0)
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

const paymentMethods = [
  { value: "COD", label: "Thanh toán khi nhận hàng" },
  { value: "BANK", label: "ATM | Internet Banking" },
  { value: "MOMO", label: "Ví Momo" },
  { value: "VNPAY", label: "Ví VNPay" },
  { value: "ZALOPAY", label: "Ví ZaloPay" },
  { value: "SHOPEEPAY", label: "Ví ShopeePay" },
];

const agree = ref(false);

const placeOrder = async () => {
  if (!agree.value) {
    alert("Bạn cần đồng ý với điều khoản trước khi đặt hàng");
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
    thoiGianNhan: orderForm.value.thoiGianNhan,
    tongTien: totalPrice.value,
    items: cartItems.value.map((i) => ({
      bookId: i.id,
      soLuong: i.qty,
      donGia: i.price,
    })),
  };

  try {
    await api.post("/orders", payload);

    toastMessage.value = "Đặt hàng thành công";
    showToast.value = true;

    localStorage.removeItem("cart");
    cartItems.value = [];

    setTimeout(() => {
      showToast.value = false;
    }, 2000);

  } catch (e) {
    toastMessage.value = "Đặt hàng thất bại";
    showToast.value = true;

    setTimeout(() => {
      showToast.value = false;
    }, 2000);
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
.checkout-title {
  font-weight: 600;
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid #bfbfbf;
}
.form-control {
  height: 34px;
  font-size: 13px;
}
.radio-row {
  display: block;
  margin-bottom: 6px;
  text-align: left;
}
.order-table {
  width: 100%;
}
.order-table img {
  width: 60px;
}
.summary {
  width: 280px;
  margin-left: auto;
}
.summary div {
  display: flex;
  justify-content: space-between;
}
.summary .sum {
  font-weight: 600;
  border-top: 1px solid #ccc;
  padding-top: 6px;
}
.toast-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.toast-box {
  background: #222;
  color: #fff;
  padding: 24px 32px;
  border-radius: 8px;
  text-align: center;
  min-width: 300px;
}

.toast-icon {
  width: 48px;
  height: 48px;
  background: #4caf50;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 22px;
  margin: 0 auto 12px;
}

.toast-text {
  font-size: 15px;
}

</style>
