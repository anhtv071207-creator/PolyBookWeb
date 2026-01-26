<template>
  <section class="container book-detail">
    <!-- Breadcrumb -->
    <div class="breadcrumb-box">
      Trang chủ &nbsp;&gt;&nbsp; Kỹ năng &nbsp;&gt;&nbsp;
      <span class="active">Tâm lý</span>
    </div>

    <!-- Main detail -->
    <div class="detail-wrapper">
      <div class="row">
        <!-- LEFT -->
        <div class="col-md-5">
          <div class="image-box">
            <img :src="book.imageUrl || '/books/dac-nhan-tam.jpg'" />
          </div>

          <div class="thumb-list">
            <img v-for="i in 6" :key="i" src="/books/dac-nhan-tam.jpg" />
            <div class="more-thumb">+11</div>
          </div>
        </div>

        <div class="col-md-7">
          <div class="right-wrapper">
            <!-- BOX TRÊN: thông tin -->
            <div class="right-box top-box">
              <h3 class="book-title">{{ book.tieuDe }}</h3>
              <div class="meta">
                Tác giả: <b>{{ book.tacGia }}</b>
              </div>
              <div class="meta">Hình thức: Bìa mềm</div>

              <div class="rating-line">★★★★★ <span>(2 đánh giá)</span></div>

              <div class="price">{{ formatPrice(book.gia) }} đ</div>

              <div class="stock-text">Hàng tồn: {{ book.hangTon ?? 100 }}</div>
            </div>

            <!-- BOX DƯỚI: mua hàng -->
            <div class="right-box bottom-box">
              <div class="option-row">
                <span class="label">Phân loại</span>
                <div class="opts">
                  <button class="opt active">Đắc Nhân Tâm 2021</button>
                  <button class="opt">ĐNT 2023</button>
                  <button class="opt">ĐNT Bìa cứng 2023</button>
                </div>
              </div>

              <div class="option-row">
                <span class="label">Số lượng</span>
                <div class="qty">
                  <button @click="quantity--" :disabled="quantity <= 1">
                    -
                  </button>
                  <input type="text" :value="quantity" readonly />
                  <button @click="quantity++">+</button>
                </div>
              </div>

              <div class="actions">
                <button class="btn-cart">🛒 Thêm vào giỏ hàng</button>
                <button class="btn-buy">Mua ngay</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Tabs -->
    <div class="info-wrapper">
      <div class="tabs">
        <div class="tab active">THÔNG TIN SẢN PHẨM</div>
        <div class="tab">MÔ TẢ SẢN PHẨM</div>
      </div>

      <div class="tab-content">
        <div class="row">
          <div class="col-md-4">
            <table class="info-table">
              <tr>
                <td>ISBN</td>
                <td>{{ book.isbn }}</td>
              </tr>
              <tr>
                <td>Tác giả</td>
                <td>{{ book.tacGia }}</td>
              </tr>
              <tr>
                <td>Năm XB</td>
                <td>2023</td>
              </tr>
              <tr>
                <td>Ngôn ngữ</td>
                <td>Tiếng Việt</td>
              </tr>
            </table>
          </div>

          <div class="col-md-8">
            <div class="desc">
              {{ book.moTa }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Rating -->
    <div class="rating-wrapper">
      <h5>Đánh giá sản phẩm</h5>

      <div class="rating-box">
        <div class="score">
          <div class="big">{{ book.avgRating ?? 5 }}</div>
          <div class="small">/5</div>
        </div>

        <div class="bars">
          <div v-for="n in 5" :key="n" class="bar-row">
            <span>{{ 6 - n }} sao</span>
            <div class="bar">
              <div class="fill"></div>
            </div>
            <span>0%</span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

const route = useRoute();
const book = ref({});
const quantity = ref(1);

onMounted(async () => {
  const res = await axios.get(
    `http://localhost:8080/api/books/${route.params.id}`,
  );
  book.value = res.data;
});

const formatPrice = (v) => (v ? Number(v).toLocaleString("vi-VN") : "0");
</script>

<style scoped>
.right-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;         
}

.right-box {
  border: 1px solid #000; /* tím để debug, xong đổi #ddd */
  padding: 16px;
  background: #fff;
}
/* box trên căn trái toàn bộ */
.top-box {
  text-align: left;
}

/* riêng dòng hàng tồn */
.stock-text {
  margin-top: 10px;
  padding: 6px 0;
  background: #e5e5e5;
  text-align: center;     /* căn giữa */
  font-size: 13px;
  color: #333;
}

/* box dưới */
.bottom-box {
  background: #fafafa;
}

/* tồn kho giống thiết kế */
.stock-text {
  margin-top: 8px;
  font-size: 13px;
  color: #666;
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

.breadcrumb-box .active {
  font-weight: 600;
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
  margin-bottom: 4px;
}

.rating-line {
  color: #f59e0b;
  margin: 6px 0;
}

.price {
  font-size: 22px;
  color: #d32f2f;
  font-weight: 700;
  margin: 10px 0;
}

.stock {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.stock-bar {
  width: 200px;
  height: 6px;
  background: #eee;
}

.stock-fill {
  width: 100%;
  height: 100%;
  background: #ccc;
}

.option-row {
  display: flex;
  align-items: center;
  margin-bottom: 14px;
}

.label {
  width: 90px;
  font-weight: 600;
}

.opts {
  display: flex;
  gap: 8px;
}

.opt {
  border: 1px solid #ccc;
  padding: 6px 12px;
  background: #fff;
}

.opt.active {
  border-color: #d32f2f;
  color: #d32f2f;
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
  border: 1px solid #ccc;
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

.info-wrapper {
  border: 1px solid #ddd;
  margin-top: 20px;
}

.tabs {
  display: flex;
  border-bottom: 1px solid #ddd;
}

.tab {
  padding: 10px 20px;
  font-weight: 600;
}

.tab.active {
  color: #d32f2f;
  border-bottom: 3px solid #d32f2f;
}

.tab-content {
  padding: 20px;
}

.info-table {
  width: 100%;
  border-collapse: collapse;
}

.info-table td {
  border: 1px solid #ddd;
  padding: 6px 10px;
}

.desc {
  line-height: 1.7;
}

.rating-wrapper {
  border: 1px solid #ddd;
  margin-top: 20px;
  padding: 16px;
}

.rating-box {
  display: flex;
  gap: 40px;
  margin-top: 10px;
}

.score {
  text-align: center;
}

.score .big {
  font-size: 36px;
  color: #f59e0b;
  font-weight: 700;
}

.bar-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.bar {
  width: 180px;
  height: 8px;
  background: #eee;
}

.fill {
  width: 100%;
  height: 100%;
  background: #f59e0b;
}

</style>
