<template>
  <div class="cart-page container my-4">
    <h4 class="mb-3">Giỏ hàng</h4>

    <div v-if="cartItems.length" class="row">
      <div class="col-md-8">
        <div class="card mb-3">
          <div class="card-header fw-semibold">
            Giỏ hàng ( {{ cartItems.length }} sản phẩm )
          </div>

          <div class="card-body">
            <div
              v-for="item in cartItems"
              :key="item.id"
              class="cart-item d-flex align-items-center mb-3"
            >
              <img :src="item.image" class="item-img" />

              <div class="flex-grow-1 ms-3">
                <div class="fw-semibold">{{ item.name }}</div>
                <div class="text-danger fw-semibold">
                  {{ formatPrice(item.price) }}
                </div>
              </div>

              <div class="qty-wrapper">
                <button class="qty-btn" @click="decrease(item)">−</button>
                <span class="qty-number">{{ item.qty }}</span>
                <button class="qty-btn" @click="increase(item)">+</button>
              </div>

              <div class="item-total ms-3">
                {{ formatPrice(item.price * item.qty) }}
              </div>

              <button class="btn-remove ms-3" @click="remove(item.id)">
                ✕
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="col-md-4">
        <div class="card mb-3">
          <div class="card-header fw-semibold">Khuyến mãi</div>
          <div class="card-body">
            <div class="voucher">
              <div class="fw-semibold">Mã giảm giá 10K</div>
              <div class="text-muted small">Áp dụng đơn hàng từ 120K</div>
              <button class="btn btn-primary w-100 mt-2">Mua thêm</button>
            </div>
          </div>
        </div>

        <div class="card">
          <div class="card-body">
            <div class="d-flex justify-content-between mb-2">
              <span>Thành tiền</span>
              <span>{{ formatPrice(totalPrice) }}</span>
            </div>

            <div class="d-flex justify-content-between fw-bold mb-3">
              <span>Tổng số tiền</span>
              <span class="text-danger">
                {{ formatPrice(totalPrice) }}
              </span>
            </div>

            <button class="btn btn-danger w-100" @click="order">Đặt Hàng</button>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="card empty-cart">
      <div class="card-header fw-semibold">Giỏ hàng ( 0 sản phẩm )</div>
      <div class="card-body text-center py-5">
        <p>Chưa có sản phẩm nào trong giỏ hàng của bạn</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { getItems, removeItem, updateQty } from "@/utils/cart";
import Order from "./Order.vue";
import { useRouter } from "vue-router";

const cartItems = ref([]);

onMounted(() => {
  cartItems.value = getItems();
});

const increase = (item) => {
  item.qty++;
  updateQty(item.id, item.qty);
};

const decrease = (item) => {
  if (item.qty > 1) {
    item.qty--;
    updateQty(item.id, item.qty);
  }
};

const remove = (id) => {
  cartItems.value = cartItems.value.filter((i) => i.id !== id);
  removeItem(id);
};

const totalPrice = computed(() =>
  cartItems.value.reduce((sum, i) => sum + i.price * i.qty, 0),
);

const formatPrice = (price) => price.toLocaleString("vi-VN") + " đ";
const router = useRouter();

const order = () => {
  router.push("/order");
};
</script>

<style scoped>
.cart-item {
  border-bottom: 1px solid #eee;
  padding-bottom: 12px;
}

.item-img {
  width: 70px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.qty-box button {
  width: 28px;
  height: 28px;
  border: 1px solid #ddd;
  background: #fff;
}

.qty-box span {
  width: 32px;
  text-align: center;
}

.item-total {
  font-weight: 600;
  white-space: nowrap;
}

.btn-remove {
  border: none;
  background: transparent;
  color: #999;
  font-size: 18px;
}

.empty-cart {
  max-width: 900px;
  margin: auto;
}
.qty-wrapper {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-right: 24px;
}

.qty-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qty-number {
  width: 28px;
  text-align: center;
}
</style>
