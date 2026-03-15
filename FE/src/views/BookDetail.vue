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

  <div v-if="book.discount > 0" class="discount-ribbon">
    -{{ book.discount }}%
  </div>
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

              <div class="price">
                <template v-if="book.discount > 0">
                  <div class="price-box">
                    <span class="price-sale">
                      {{ formatPrice(book.salePrice) }} đ
                    </span>
                    <span class="price-old">
                      {{ formatPrice(book.gia) }} đ
                    </span>
                  </div>
                </template>

                <template v-else> {{ formatPrice(book.gia) }} đ </template>
              </div>
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
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();

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
/* Replace entire <style scoped> with this */

.book-detail {
  margin-top: 20px;
  font-size: 14px;
  background: #f7fafc;
  padding: 20px;
  border-radius: 12px;
  color: #1f2937;
}

/* Breadcrumb */
.breadcrumb-box {
  border: 1px solid #e6e9ee;
  padding: 10px 12px;
  margin-bottom: 14px;
  border-radius: 10px;
  background: #fff;
  color: #475569;
  font-size: 14px;
}
.breadcrumb-link {
  color: #475569;
}
.breadcrumb-box .active {
  color: #0f172a;
  font-weight: 600;
}

/* Layout */
.detail-wrapper {
  border: 1px solid #e6e9ee;
  padding: 18px;
  border-radius: 12px;
  background: #fff;
}

/* Image box */
.image-box {
  border: 1px solid #eef2f6;
  padding: 10px;
  text-align: center;
  position: relative;
  border-radius: 10px;
  background: #fbfdff;
}
.image-box img {
  max-height: 520px;
  width: 100%;
  object-fit: contain;
  border-radius: 6px;
}

/* Discount ribbon (subtle) */
.discount-ribbon {
  position: absolute;
  top: 12px;
  left: 12px;
  background: linear-gradient(180deg,#ef4444,#dc2626);
  color: #fff;
  font-weight: 700;
  font-size: 13px;
  padding: 6px 10px;
  border-radius: 6px;
  box-shadow: 0 6px 18px rgba(16,24,40,0.08);
}

/* Thumbnail list */
.thumb-list {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  align-items: center;
  flex-wrap: nowrap;
  overflow-x: auto;
  padding-bottom: 6px;
}
.thumbnail {
  width: 64px;
  height: 92px;
  border: 1px solid #e6e9ee;
  object-fit: cover;
  border-radius: 6px;
  cursor: pointer;
  transition: transform .14s ease, box-shadow .14s ease, border-color .14s;
}
.thumbnail:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 18px rgba(16,24,40,0.06);
}
.thumbnail.active, .thumbnail:focus {
  border-color: #2563eb;
  box-shadow: 0 8px 20px rgba(37,99,235,0.12);
}
.more-thumb {
  min-width: 64px;
  height: 92px;
  border-radius: 6px;
  background: #111827;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

/* Right side wrapper */
.right-wrapper { display:flex; flex-direction:column; gap:14px; }
.right-box {
  border-radius: 12px;
  padding: 18px;
  background: #ffffff;
  border: 1px solid #eef2f6;
  box-shadow: 0 8px 18px rgba(16,24,40,0.03);
  transition: transform .18s ease, box-shadow .18s ease;
}
.right-box:hover {
  transform: translateY(-4px);
  box-shadow: 0 14px 34px rgba(16,24,40,0.06);
}

/* Title & meta */
.book-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #0f172a;
}
.meta { color: #526172; font-size: 14px; margin-bottom: 6px; }

/* Rating line */
.rating-line { color: #f59e0b; font-size: 14px; margin: 8px 0; }
.rating-line span { color: #64748b; margin-left: 8px; font-size: 13px; }

/* Price */
.price { margin-top: 8px; display:flex; align-items:baseline; gap:12px; }
.price-sale {
  font-size: 28px;
  color: #dc2626;
  font-weight: 700;
}
.price-old {
  font-size: 15px;
  color: #64748b;
  text-decoration: line-through;
}

/* Stock badge (muted) */
.stock-text {
  margin-top: 10px;
  padding: 8px 10px;
  background: #f1f5f9;
  text-align: center;
  font-size: 13px;
  border-radius: 8px;
  color: #0f172a;
  font-weight: 600;
}
.stock-text.out {
  background: #fee2e2;
  color: #991b1b;
  box-shadow: none;
}

/* Options row */
.option-row {
  display:flex;
  align-items:center;
  gap:12px;
  margin-bottom: 8px;
}
.label { width: 90px; font-weight: 600; color: #334155; }

/* Qty */
.qty-wrapper {
  display:flex;
  align-items:center;
  gap:10px;
}
.qty-btn {
  width:36px;
  height:36px;
  border-radius:8px;
  border:1px solid #e6e9ee;
  background:#fff;
  cursor:pointer;
  font-size:18px;
  line-height:1;
  display:inline-flex;
  align-items:center;
  justify-content:center;
  transition: background .12s, color .12s, transform .12s;
}
.qty-btn:hover { background:#eff6ff; color:#1e3a8a; transform: translateY(-2px); }
.qty-number {
  min-width:56px;
  height:36px;
  border-radius:8px;
  border:1px solid #e6e9ee;
  background:#fff;
  display:inline-flex;
  align-items:center;
  justify-content:center;
  font-weight:600;
}

/* Actions */
.actions { display:flex; gap:12px; margin-top:12px; flex-wrap:wrap; }
.btn-cart {
  border:1px solid #2563eb;
  background: #fff;
  color: #2563eb;
  padding:10px 16px;
  border-radius:10px;
  cursor:pointer;
  transition: background .14s, color .14s, transform .14s;
}
.btn-cart:hover { background:#2563eb; color:#fff; transform: translateY(-3px); }
.btn-buy {
  background:#2563eb;
  color:#fff;
  border:none;
  padding:10px 18px;
  border-radius:10px;
  cursor:pointer;
}
.btn-buy:disabled,
.btn-cart[disabled] {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Extra info & reviews */
.book-extra { margin-top:18px; display:flex; flex-direction:column; gap:18px; }
.info-card, .review-card {
  background:#ffffff;
  border-radius:12px;
  padding:20px;
  border:1px solid #eef2f6;
  box-shadow:0 8px 22px rgba(16,24,40,0.03);
}
.info-layout { display:grid; grid-template-columns: 1fr 2fr; gap:28px; align-items:start; }
.info-table { width:100%; border-collapse: collapse; font-size:14px; color:#334155; }
.info-table td { padding:8px 0; border-bottom: 1px solid #f1f5f9; }
.info-table td:first-child { font-weight:600; color:#475569; width:40%; }

/* Review summary */
.review-summary { display:grid; grid-template-columns: 160px 1fr; gap:20px; align-items:center; }
.point { font-size:40px; font-weight:700; color:#2563eb; }
.star { font-size:16px; color:#f59e0b; }

/* Rating bars */
.bar { display:flex; align-items:center; gap:12px; margin-bottom:10px; }
.line { flex:1; height:8px; background:#eef2f6; border-radius:999px; overflow:hidden; }
.fill { height:100%; background: linear-gradient(90deg,#f59e0b,#f97316); }

/* Toast: top-right small, non-blocking */
.toast-overlay {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  pointer-events: none;
}
.toast-box {
  min-width: 300px;
  background: #0f172a;
  color: #fff;
  padding: 14px 16px;
  border-radius: 10px;
  display:flex;
  gap:12px;
  align-items:center;
  pointer-events: auto;
  box-shadow: 0 12px 30px rgba(2,6,23,0.4);
}
.toast-icon {
  width:40px;
  height:40px;
  border-radius:50%;
  display:flex;
  align-items:center;
  justify-content:center;
  font-size:18px;
}
.toast-box.success .toast-icon { background:#10b981; }
.toast-box.error .toast-icon { background:#ef4444; }
.toast-text { font-weight:600; }

/* Responsive */
@media (max-width: 900px) {
  .row { flex-direction: column; }
  .col-md-5, .col-md-7 { width: 100%; }
  .image-box img { max-height: 420px; }
  .info-layout { grid-template-columns: 1fr; }
  .review-summary { grid-template-columns: 1fr; text-align: center; }
  .thumb-list { justify-content: flex-start; }
  .search-box { max-width: 100%; }
}
/* ===== DARK MODE ===== */

.dark .book-detail {
  background: #0f172a;
  color: #e2e8f0;
}

.dark .breadcrumb-box {
  background: #1e293b;
  border-color: #334155;
  color: #cbd5f5;
}

.dark .breadcrumb-link {
  color: #cbd5f5;
}

.dark .breadcrumb-box .active {
  color: #ffffff;
}

.dark .detail-wrapper {
  background: #1e293b;
  border-color: #334155;
}

.dark .image-box {
  background: #0f172a;
  border-color: #334155;
}

.dark .thumbnail {
  border-color: #334155;
}

.dark .thumbnail:hover {
  box-shadow: 0 8px 18px rgba(0,0,0,0.5);
}

.dark .right-box {
  background: #1e293b;
  border-color: #334155;
}

.dark .book-title {
  color: #f1f5f9;
}

.dark .meta {
  color: #cbd5f5;
}

.dark .stock-text {
  background: #334155;
  color: #e2e8f0;
}

.dark .stock-text.out {
  background: #7f1d1d;
  color: #fecaca;
}

.dark .qty-btn,
.dark .qty-number {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .qty-btn:hover {
  background: #1e293b;
}

.dark .info-card,
.dark .review-card {
  background: #1e293b;
  border-color: #334155;
}

.dark .info-table td {
  border-bottom: 1px solid #334155;
}

.dark .info-table td:first-child {
  color: #cbd5f5;
}

.dark .line {
  background: #334155;
}

.dark .empty-review {
  color: #94a3b8;
}
</style>
