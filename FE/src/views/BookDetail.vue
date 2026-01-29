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
              v-for="(img, index) in book.thumbnails"
              :key="index"
              :src="img"
            />
            <div
              v-if="book.thumbnails && book.thumbnails.length > 6"
              class="more-thumb"
            >
              +{{ book.thumbnails.length - 6 }}
            </div>
          </div>
        </div>

        <div class="col-md-7">
          <div class="right-wrapper">
            <div class="right-box top-box">
              <h3 class="book-title">{{ book.tieuDe }}</h3>
              <div class="meta">Tác giả: <b>{{ book.tacGia }}</b></div>
              <div class="meta">Hình thức: Bìa mềm</div>

              <div class="rating-line">★★★★★ <span>(2 đánh giá)</span></div>

              <div class="price">{{ formatPrice(book.gia) }} đ</div>
              <div class="stock-text">Hàng tồn: {{ book.hangTon ?? 100 }}</div>
            </div>

            <div class="right-box bottom-box">
              <div class="option-row">
                <span class="label">Số lượng</span>
                <div class="qty">
                  <button @click="quantity--" :disabled="quantity <= 1">-</button>
                  <input type="text" :value="quantity" readonly />
                  <button @click="quantity++">+</button>
                </div>
              </div>

              <div class="actions">
                <button class="btn-cart" @click="addToCart">
                  🛒 Thêm vào giỏ hàng
                </button>

                <button class="btn-buy" @click="buyNow">
                  Mua ngay
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/services/api";
import { addItem } from "@/utils/cart";

const route = useRoute();
const router = useRouter();

const book = ref({});
const quantity = ref(1);

onMounted(async () => {
  const res = await api.get(`/books/${route.params.id}`);
  book.value = res.data;
});

const formatPrice = v =>
  v ? Number(v).toLocaleString("vi-VN") : "0";

const addToCart = () => {
  addItem(book.value, quantity.value);
};

const buyNow = () => {
  addItem(book.value, quantity.value);
  router.push("/cart");
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

.qty {
  display: flex;
  align-items: center;
}

.qty button {
  width: 32px;
  height: 32px;
}

.qty input {
  width: 40px;
  text-align: center;
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
</style>
