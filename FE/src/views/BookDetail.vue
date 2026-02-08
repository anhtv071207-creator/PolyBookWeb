<template>
  <section class="container book-detail">
    <div class="breadcrumb-box">
      Trang chủ &nbsp;&gt;&nbsp; Kỹ năng &nbsp;&gt;&nbsp;
      <span class="active">Tâm lý</span>
    </div>

    <div class="detail-wrapper">
      <div class="row">
        <div class="col-md-5">
          <div class="image-box">
            <img :src="book.mainImage || '/books/dac-nhan-tam.jpg'" />
          </div>

          <div class="thumb-list">
            <img
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
              <div class="stock-text">Hàng tồn: {{ book.hangTon }}</div>
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
      <div class="tab-header">
        <div
          class="tab-item"
          :class="{ active: activeTab === 'info' }"
          @click="activeTab = 'info'"
        >
          THÔNG TIN SẢN PHẨM
        </div>
        <div
          class="tab-item"
          :class="{ active: activeTab === 'review' }"
          @click="activeTab = 'review'"
        >
          ĐÁNH GIÁ SẢN PHẨM
        </div>
      </div>

      <div class="tab-content">
        <!-- INFO -->
        <div v-if="activeTab === 'info'" class="info-box">
          <div>
            <h4>THÔNG TIN SẢN PHẨM</h4>
            <table class="info-table">
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
                <td>Hình thức</td>
                <td>Bìa mềm</td>
              </tr>
              <tr>
                <td>Số trang</td>
                <td>320</td>
              </tr>
            </table>
          </div>

          <div>
            <h4>MÔ TẢ SẢN PHẨM</h4>
            <p class="desc-text">{{ book.moTa }}</p>
          </div>
        </div>

        <!-- REVIEW -->
        <div v-if="activeTab === 'review'" class="review-box">
          <div class="review-summary">
            <div class="score">
              <div class="point">
                {{ book.avgRating || 0 }}
              </div>
              <div class="star">★★★★★</div>
              <div>({{ book.totalReviews || 0 }} đánh giá)</div>
            </div>

            <div class="rating-bars">
              <div class="bar" v-for="i in 5" :key="i">
                <span>{{ 6 - i }} sao</span>
                <div class="line">
                  <div class="fill" style="width: 0%"></div>
                </div>
                <span>0%</span>
              </div>
            </div>
          </div>

          <div class="empty-review">Chưa có đánh giá nào cho sản phẩm này</div>
        </div>
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

const auth = useAuthStore();
const route = useRoute();
const router = useRouter();

const book = ref({});
const quantity = ref(1);

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
        }
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
  border: 1px solid #000;
  padding: 16px;
  background: #fff;
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
  width: 28px;
  height: 28px;
  border: 1px solid #ccc;
  background: #f5f5f5;
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
  padding: 0;
}

.qty-btn:hover {
  background: #e0e0e0;
}

.qty-number {
  min-width: 40px;
  height: 28px;
  border: 1px solid #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
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

.tab-header {
  display: flex;
  border-bottom: 1px solid #ddd;
}

.tab-item {
  padding: 12px 20px;
  cursor: pointer;
  font-weight: 600;
  color: #666;
  border-bottom: 3px solid transparent;
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

/* ===== Responsive ===== */
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

</style>
