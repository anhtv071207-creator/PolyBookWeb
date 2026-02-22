<template>
  <section class="container book-detail">
    <div class="breadcrumb-box">
      <span class="breadcrumb-link">Trang chủ</span>
      &nbsp;&gt;&nbsp;

      <template v-if="book.categories?.length">
        <template v-for="(cate, index) in book.categories" :key="cate.id">
          <span class="breadcrumb-link">
            {{ cate.ten }}
          </span>

          <span v-if="index < book.categories.length - 1">
            &nbsp;&gt;&nbsp;
          </span>
        </template>

        &nbsp;&gt;&nbsp;
      </template>

      <span class="active">{{ book.tieuDe }}</span>
    </div>

    <div class="detail-wrapper">
      <div class="row">
        <div class="col-md-5">
          <div class="image-box">
            <img :src="book.mainImage || '/books/dac-nhan-tam.jpg'" />
          </div>

          <div class="thumb-list">
            <img
              class="thumbnail"
              v-for="(img, index) in book.thumbnails?.slice(0, 6)"
              :key="index"
              :src="img"
              @click="book.mainImage = img"
            />
            <div v-if="book.thumbnails?.length > 6" class="more-thumb">
              +{{ book.thumbnails.length - 6 }}
            </div>
          </div>
        </div>

        <div class="col-md-7">
          <div class="right-wrapper">
            <div class="right-box top-box">
              <h3 class="book-title">{{ book.tieuDe }}</h3>
              <div class="meta">
                Tác giả: <b>{{ book.tacGia }}</b>
              </div>
              <div class="meta">Hình thức: Bìa mềm</div>

              <div class="rating-line">
                ★★★★★
                <span>({{ book.totalReviews || 0 }} đánh giá)</span>
              </div>

              <div class="price">{{ formatPrice(book.gia) }} đ</div>
              <div class="stock-text" :class="{ out: book.hangTon === 0 }">
                <template v-if="book.hangTon === 0"> HẾT HÀNG </template>
                <template v-else> Hàng tồn: {{ book.hangTon }} </template>
              </div>
            </div>

            <div class="right-box bottom-box">
              <div class="option-row">
                <span class="label">Số lượng</span>
                <div class="qty-wrapper">
                  <button class="qty-btn" @click="decrease">−</button>
                  <span class="qty-number">{{ quantity }}</span>
                  <button class="qty-btn" @click="increase">+</button>
                </div>
              </div>

              <div class="actions">
                <button class="btn-cart" @click="addToCart">
                  🛒 Thêm vào giỏ hàng
                </button>

                <button class="btn-buy" @click="buyNow">Mua ngay</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="book-extra mt-4">
      <div class="info-card">
        <div class="info-layout">
          <div class="info-left">
            <h4>Thông tin sản phẩm</h4>
            <table class="info-table">
              <tbody>
                <tr>
                  <td>Tác giả</td>
                  <td>{{ book.tacGia }}</td>
                </tr>
                <tr>
                  <td>Danh mục</td>
                  <td>Kỹ năng</td>
                </tr>
                <tr>
                  <td>Nhà xuất bản</td>
                  <td>NXB Trẻ</td>
                </tr>
                <tr>
                  <td>Năm xuất bản</td>
                  <td>2023</td>
                </tr>
                <tr>
                  <td>Số trang</td>
                  <td>320</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="info-right">
            <h4>Mô tả</h4>
            <p class="desc-text">{{ book.moTa }}</p>
          </div>
        </div>
      </div>

      <div class="review-card">
        <h4>Đánh giá sản phẩm</h4>

        <div class="review-summary">
          <div class="score">
            <div class="point">{{ book.avgRating || 0 }}</div>
            <div class="star">★★★★★</div>
            <div>({{ book.totalReviews || 0 }} đánh giá)</div>
          </div>

          <div class="rating-bars">
            <div class="bar" v-for="i in 5" :key="i">
              <span>{{ 6 - i }} sao</span>
              <div class="line">
                <div
                  class="fill"
                  :style="{ width: getPercent(6 - i) + '%' }"
                ></div>
              </div>
              <span>{{ getPercent(6 - i) }}%</span>
            </div>
          </div>
        </div>

        <div class="empty-review">Chưa có đánh giá nào cho sản phẩm này</div>
      </div>
    </div>
    <div v-if="showToast" class="toast-overlay">
      <div class="toast-box" :class="toastType">
        <div class="toast-icon">
          <span v-if="toastType === 'success'">✔</span>
          <span v-else>✕</span>
        </div>
        <div class="toast-text">{{ toastMessage }}</div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/services/api";
import { addItem } from "@/utils/cart";
import { useAuthStore } from "@/stores/auth";
import { computed } from "vue";
import { watch } from "vue";

const auth = useAuthStore();
const route = useRoute();
const router = useRouter();

const book = ref({});
const quantity = ref(1);

const totalReviews = computed(() => book.value.totalReviews || 0);

const getPercent = (star) => {
  if (!book.value.ratingStats || !totalReviews.value) return 0;
  return Math.round((book.value.ratingStats[star] / totalReviews.value) * 100);
};
watch(
  () => book.value.hangTon,
  (stock) => {
    if (quantity.value > stock) {
      quantity.value = stock || 1;
    }
  },
);
onMounted(async () => {
  const res = await api.get(`/books/${route.params.id}`);
  book.value = res.data;
});

const formatPrice = (v) => (v ? Number(v).toLocaleString("vi-VN") : "0");
const addToCart = async () => {
  const stock = book.value.hangTon ?? 0;

  if (stock <= 0 || quantity.value > stock) {
    showToastMsg("error", "Sản phẩm đã hết hàng");
    return false;
  }

  try {
    if (auth.isLoggedIn && auth.role === "USER") {
      await api.post(
        "/cart/add",
        {
          bookId: book.value.id,
          quantity: quantity.value,
        },
        {
          params: { userId: auth.user.id },
        },
      );

      showToastMsg("success", "Đã thêm vào giỏ hàng");
      return true;
    }

    addItem(book.value, quantity.value);
    showToastMsg("success", "Đã thêm vào giỏ hàng");
    return true;
  } catch (e) {
    showToastMsg("error", e.response?.data?.message || "Không đủ hàng tồn");
    return false;
  }
};

const activeTab = ref("info");

const buyNow = async () => {
  const ok = await addToCart();
  if (ok) router.push("/order");
};

const showToast = ref(false);
const toastMessage = ref("");
const toastType = ref("success");

const showToastMsg = (type, message) => {
  toastType.value = type;
  toastMessage.value = message;
  showToast.value = true;

  setTimeout(() => {
    showToast.value = false;
  }, 1800);
};
const increase = () => {
  if (quantity.value < book.value.hangTon) {
    quantity.value++;
  }
};

const decrease = () => {
  if (quantity.value > 1) {
    quantity.value--;
  }
};
</script>

<style scoped>
.right-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.right-box {
  border: 1px solid #e5e5e5;
  padding: 20px;
  background: #fff;
  border-radius: 14px;

  box-shadow:
    0 8px 24px rgba(0, 0, 0, 0.06),
    0 2px 6px rgba(0, 0, 0, 0.05);

  transition: all 0.3s ease;
}

.right-box:hover {
  transform: translateY(-3px);
  box-shadow:
    0 12px 32px rgba(0, 0, 0, 0.08),
    0 4px 10px rgba(0, 0, 0, 0.06);
}

.top-box {
  text-align: left;
}

.stock-text {
  margin-top: 10px;
  padding: 6px 0;
  background: #e5e5e5;
  text-align: center;
  font-size: 13px;
}

.bottom-box {
  background: #fafafa;
}

.book-detail {
  margin-top: 20px;
  font-size: 14px;
  background: #f5f6f8;
  padding: 20px;
  border-radius: 16px;
}

.breadcrumb-box {
  border: 1px solid #ddd;
  padding: 8px 12px;
  margin-bottom: 12px;
}

.detail-wrapper {
  border: 1px solid #ddd;
  padding: 16px;
}

.image-box {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: center;
}

.image-box img {
  height: 420px;
  width: 100%;
  object-fit: contain;
}

.thumb-list {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

.thumb-list img,
.more-thumb {
  width: 60px;
  height: 80px;
  border: 1px solid #ddd;
}

.more-thumb {
  background: #222;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.book-title {
  font-weight: 700;
}

.meta {
  color: #555;
}

.price {
  font-size: 30px;
  color: #d32f2f;
  font-weight: 700;
}

.option-row {
  display: flex;
  align-items: center;
}

.label {
  width: 90px;
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
  border-radius: 8px;
  border: 1px solid #ddd;
  background: #fafafa;
  cursor: pointer;
  transition: 0.2s;
}

.qty-btn:hover {
  background: #d32f2f;
  color: #fff;
  border-color: #d32f2f;
}

.qty-number {
  min-width: 50px;
  height: 32px;
  border-radius: 8px;
  border: 1px solid #ddd;
  background: #fff;
}

.actions {
  margin-top: 20px;
  display: flex;
  gap: 12px;
}

.btn-cart {
  border: 1px solid #d32f2f;
  color: #d32f2f;
  background: #fff;
  padding: 10px 18px;
}

.btn-buy {
  background: #d32f2f;
  color: #fff;
  border: none;
  padding: 10px 24px;
}
.toast-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.toast-box {
  width: 360px;
  background: #222;
  color: #fff;
  padding: 24px;
  border-radius: 12px;
  text-align: center;
}

.toast-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  font-size: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
}

.toast-box.success .toast-icon {
  background: #4caf50;
}

.toast-box.error .toast-icon {
  background: #f44336;
}

.toast-text {
  font-size: 15px;
  font-weight: 500;
}

.thumbnail:hover {
  border-color: #333;
}

.rating-line {
  color: #f5a623;
  font-size: 15px;
}

.rating-line span {
  color: #555;
  margin-left: 6px;
  font-size: 14px;
}

.book-price {
  font-size: 28px;
  font-weight: 700;
  color: #e53935;
}

.stock {
  font-size: 14px;
}

.stock.in {
  color: #2e7d32;
}

.stock.out {
  color: #c62828;
}

.book-tabs {
  grid-column: 1 / -1;
  margin-top: 40px;
}

.tab-item.active {
  color: #111;
  border-color: #111;
}

.tab-content {
  padding: 24px 0;
  line-height: 1.7;
  color: #333;
}

.book-description {
  white-space: pre-line;
}

.review-item {
  padding: 16px 0;
  border-bottom: 1px solid #eee;
}

.review-name {
  font-weight: 600;
}

.review-stars {
  color: #f5a623;
  font-size: 14px;
}

.review-text {
  margin-top: 6px;
  font-size: 14px;
  color: #444;
}

@media (max-width: 900px) {
  .book-detail {
    grid-template-columns: 1fr;
  }

  .main-image {
    height: 420px;
  }

  .book-images {
    flex-direction: column-reverse;
  }

  .thumbnail-list {
    flex-direction: row;
  }
}
.stock-text {
  margin-top: 10px;
  padding: 6px 0;
  background: #e5e5e5;
  text-align: center;
  font-size: 13px;
  font-weight: 600;
}

.stock-text.out {
  background: #d32f2f;
  color: #fff;
  font-weight: 700;
  letter-spacing: 1px;

  box-shadow:
    0 0 6px rgba(255, 0, 0, 0.6),
    0 0 12px rgba(255, 0, 0, 0.6),
    0 0 20px rgba(255, 0, 0, 0.6);

  text-shadow:
    0 0 6px rgba(255, 80, 80, 0.9),
    0 0 12px rgba(255, 80, 80, 0.9);
}
.info-box {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 40px;

  background: #fff;
  padding: 32px;
  border-radius: 14px;

  box-shadow:
    0 6px 18px rgba(0, 0, 0, 0.05),
    0 12px 32px rgba(0, 0, 0, 0.06);
}

.info-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  text-align: left;
}

.info-table td {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-table td:first-child {
  font-weight: 600;
  color: #666;
  width: 40%;
}
.desc-text {
  margin-top: 12px;
  line-height: 1.8;
  color: #444;
  font-size: 15px;
  text-align: left;
}
.review-box {
  background: #fff;
  padding: 32px;
  border-radius: 14px;

  box-shadow:
    0 6px 18px rgba(0, 0, 0, 0.05),
    0 12px 32px rgba(0, 0, 0, 0.06);
}

.star {
  font-size: 20px;
  color: #f5a623;
  margin: 8px 0;
}

.tab-item {
  padding: 12px 0;
  font-weight: 600;
  cursor: pointer;
  color: #777;
  position: relative;
  transition: 0.3s;
}

.tab-item.active::after {
  content: "";
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 3px;
  background: #d32f2f;
  border-radius: 3px;
}

.info-card,
.review-card {
  background: #fff;
  padding: 32px;
  border-radius: 18px;
  margin-bottom: 30px;

  border: 1px solid #000;

  box-shadow:
    0 10px 30px rgba(0, 0, 0, 0.06),
    0 4px 10px rgba(0, 0, 0, 0.04);

  transition: 0.3s ease;
}

.info-card:hover,
.review-card:hover {
  transform: translateY(-4px);
}

.info-layout {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 40px;
}

.review-summary {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 40px;
  margin-top: 20px;
}
.point {
  font-size: 52px;
  font-weight: 700;
  color: #d32f2f;
}

.star {
  font-size: 18px;
  color: #f5a623;
  margin: 6px 0;
}
.bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.line {
  flex: 1;
  height: 8px;
  background: #eee;
  border-radius: 6px;
  overflow: hidden;
}

.fill {
  height: 100%;
  background: linear-gradient(90deg, #f5a623, #ff9800);
  border-radius: 6px;
}
@media (max-width: 900px) {
  .info-layout {
    grid-template-columns: 1fr;
  }

  .review-summary {
    grid-template-columns: 1fr;
    text-align: center;
  }
}
</style>
