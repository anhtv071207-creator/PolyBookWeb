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
.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 12px;
  margin-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.item-img {
  width: 70px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.item-info {
  flex: 1;
  min-width: 200px;
}

.old-price {
  text-decoration: line-through;
  color: gray;
  font-size: 14px;
}

.new-price {
  color: #dc3545;
  font-weight: 600;
}

.qty-wrapper {
  display: flex;
  align-items: center;
  gap: 6px;
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

.item-total {
  min-width: 110px;
  font-weight: 600;
}

.btn-remove {
  border: none;
  background: transparent;
  color: #999;
  font-size: 18px;
  cursor: pointer;

  width: 36px;
  height: 36px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  position: relative;
  z-index: 10;
}

.empty-cart {
  max-width: 900px;
  margin: auto;
}
</style>
