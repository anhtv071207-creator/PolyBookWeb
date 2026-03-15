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
            <div v-for="item in cartItems" :key="item.bookId" class="cart-item">
              <img :src="item.image" class="item-img" />

              <div class="item-info">
                <div class="fw-semibold">{{ item.name }}</div>

                <div v-if="item.discountPercent > 0">
                  <div class="old-price">
                    {{ formatPrice(item.originalPrice) }}
                  </div>

                  <div class="new-price">
                    {{ formatPrice(item.price) }}
                  </div>
                </div>

                <div v-else class="new-price">
                  {{ formatPrice(item.price) }}
                </div>
              </div>

              <div class="qty-wrapper">
                <button
                  class="qty-btn"
                  :disabled="item.loading"
                  @click="decrease(item)"
                >
                  −
                </button>

                <span class="qty-number">
                  {{ item.qty }}
                </span>

                <button
                  class="qty-btn"
                  :disabled="item.loading"
                  @click="increase(item)"
                >
                  +
                </button>
              </div>

              <div class="item-total">
                {{ formatPrice(item.price * item.qty) }}
              </div>

              <button
                class="btn-remove"
                type="button"
                :disabled="item.loading"
                @click.stop.prevent="remove(item.bookId)"
              >
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

            <button class="btn btn-danger w-100" @click="order">
              Đặt Hàng
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="card empty-cart">
      <div class="card-header fw-semibold">Giỏ hàng (0 sản phẩm)</div>

      <div class="card-body text-center py-5">
        <p>Chưa có sản phẩm nào trong giỏ hàng của bạn</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { getItems, removeItem, updateQty } from "@/utils/cart";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();

const auth = useAuthStore();
const router = useRouter();
const cartItems = ref([]);


const loadCart = async () => {
  if (auth.isLoggedIn && auth.role === "USER") {
    const res = await api.get(`/cart/user/${auth.user.id}`);

    cartItems.value = mapFromApi(res.data);
  } else {
    cartItems.value = getItems().map((i) => ({
      id: i.id,
      bookId: i.id,
      name: i.name,

      originalPrice: Number(i.price),

      price: Number(i.discount > 0 ? i.salePrice : i.price),

      discountPercent: i.discount || 0,

      qty: i.qty,
      image: i.image,
      loading: false,
    }));
  }
};

onMounted(async () => {
  await loadCart();
});


const mapFromApi = (cart) =>
  (cart.items || []).map((i) => ({
    id: i.bookId,
    bookId: i.bookId,
    name: i.tieuDe,
    originalPrice: Number(i.gia),
    price: Number(i.salePrice),
    discountPercent: i.discount || 0,
    qty: i.soLuong,
    image: i.imageUrl,
    loading: false,
  }));


const increase = async (item) => {
  try {
    if (auth.isLoggedIn && auth.role === "USER") {
      item.loading = true;

      await api.post(
        "/cart/add",
        { bookId: item.id, quantity: 1 },
        { params: { userId: auth.user.id } },
      );

      await loadCart();
    } else {
      item.qty++;
      updateQty(item.id, item.qty);
    }
  } catch (e) {
    alert(e.response?.data?.message || "Không thể tăng số lượng");
  } finally {
    item.loading = false;
  }
};


const decrease = async (item) => {
  if (item.qty <= 1) return;

  try {
    if (auth.isLoggedIn && auth.role === "USER") {
      item.loading = true;

      await api.post(
        "/cart/add",
        { bookId: item.id, quantity: -1 },
        { params: { userId: auth.user.id } },
      );

      await loadCart();
    } else {
      item.qty--;
      updateQty(item.id, item.qty);
    }
  } catch (e) {
    alert(e.response?.data?.message || "Không thể giảm số lượng");
  } finally {
    item.loading = false;
  }
};


const remove = async (bookId) => {
  const item = cartItems.value.find((i) => i.bookId === bookId);

  try {
    if (auth.isLoggedIn && auth.role === "USER") {
      if (item) item.loading = true;

      await api.delete(`/cart/items/${bookId}`, {
        params: { userId: auth.user.id },
      });

      await loadCart();
    } else {
      removeItem(bookId);
      cartItems.value = cartItems.value.filter((i) => i.bookId !== bookId);
    }
  } catch (e) {
    console.error(e);
  } finally {
    if (item) item.loading = false;
  }
};


const totalPrice = computed(() =>
  cartItems.value.reduce((sum, i) => sum + i.price * i.qty, 0),
);


const formatPrice = (p) => Number(p || 0).toLocaleString("vi-VN") + " đ";


const order = () => router.push("/order");
</script>

<style scoped>
.cart-page{
  max-width:1100px;
}

/* item */

.cart-item{
  display:grid;
  grid-template-columns:80px 1fr 120px 120px 40px;
  align-items:center;
  gap:16px;

  padding:14px 0;
  border-bottom:1px solid #f0f0f0;
}

/* image */

.item-img{
  width:70px;
  height:100px;
  object-fit:cover;
  border-radius:6px;
  border:1px solid #eee;
}

/* info */

.item-info{
  display:flex;
  flex-direction:column;
  gap:4px;
}

.item-info .fw-semibold{
  font-size:15px;
  color:#111;
}

.old-price{
  text-decoration:line-through;
  color:#888;
  font-size:13px;
}

.new-price{
  color:#e53935;
  font-weight:600;
  font-size:15px;
}

/* quantity */

.qty-wrapper{
  display:flex;
  align-items:center;
  justify-content:center;
  gap:6px;
}

.qty-btn{
  width:30px;
  height:30px;

  border:1px solid #ddd;
  background:#fff;
  border-radius:6px;

  font-size:16px;
  cursor:pointer;

  transition:0.2s;
}

.qty-btn:hover{
  background:#f5f5f5;
}

.qty-number{
  width:32px;
  text-align:center;
  font-weight:500;
}

/* total */

.item-total{
  font-weight:600;
  text-align:right;
}

/* remove */

.btn-remove{
  border:none;
  background:transparent;

  color:#999;
  font-size:18px;
  cursor:pointer;

  transition:0.2s;
}

.btn-remove:hover{
  color:#e53935;
}

/* card */

.card{
  border:1px solid #eee;
  border-radius:10px;
}

.card-header{
  background:#fafafa;
  border-bottom:1px solid #eee;
}

/* empty */

.empty-cart{
  max-width:700px;
  margin:auto;
}

/* responsive */

@media (max-width:768px){

  .cart-item{
    grid-template-columns:70px 1fr;
    grid-template-rows:auto auto auto;
    gap:10px;
  }

  .qty-wrapper{
    justify-content:flex-start;
  }

  .item-total{
    text-align:left;
  }
}
/* ===== DARK MODE CART ===== */

.dark .cart-page {
  color: #e2e8f0;
}

.dark .card {
  background: #1e293b;
  border-color: #334155;
}

.dark .card-header {
  background: #0f172a;
  border-bottom: 1px solid #334155;
  color: #e2e8f0;
}

.dark .cart-item {
  border-bottom: 1px solid #334155;
}

.dark .item-info .fw-semibold {
  color: #f1f5f9;
}

.dark .old-price {
  color: #94a3b8;
}

.dark .new-price {
  color: #f87171;
}

.dark .item-img {
  border-color: #334155;
}

/* quantity */

.dark .qty-btn {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .qty-btn:hover {
  background: #1e293b;
}

.dark .qty-number {
  color: #e2e8f0;
}

/* remove */

.dark .btn-remove {
  color: #94a3b8;
}

.dark .btn-remove:hover {
  color: #f87171;
}

/* empty cart */

.dark .empty-cart {
  background: #1e293b;
  border-color: #334155;
}

/* voucher text */

.dark .text-muted {
  color: #94a3b8 !important;
}
</style>
