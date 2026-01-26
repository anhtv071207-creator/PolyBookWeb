<template>
  <section class="container book-detail">
    <!-- Breadcrumb -->
    <div class="breadcrumb-box">
      Trang chủ &nbsp;>&nbsp; Kỹ năng &nbsp;>&nbsp;
      <span class="active">Tâm lý</span>
    </div>

    <!-- Top box -->
    <div class="detail-box">
      <div class="row">
        <!-- Left -->
        <div class="col-md-4">
          <div class="image-box">
            <img :src="book.imageUrl || '/books/dac-nhan-tam.jpg'" alt="" />
          </div>

          <div class="thumb-list">
            <img v-for="i in 4" :key="i" src="/books/dac-nhan-tam.jpg" />
            <div class="more-thumb">+11</div>
          </div>
        </div>

        <div class="col-md-8">
          <!-- BOX 1: Thông tin -->
          <div class="right-box info-box">
            <h4 class="title">{{ book.tieuDe }}</h4>
            <p class="muted">Tác giả: {{ book.tacGia }}</p>
            <p class="muted">Hình thức: Bìa mềm</p>

            <div class="rating">☆☆☆☆☆ (2 đánh giá)</div>

            <div class="price">{{ formatPrice(book.gia) }} đ</div>

            <div class="stock-box">Hàng tồn: {{ book.hangTon ?? 0 }}</div>
          </div>

          <!-- BOX 2: Mua hàng -->
          <div class="right-box buy-box">
            <div class="row-item">
              <span class="label">Phân loại:</span>
              <div class="options">
                <button class="opt active">ĐNT 2021</button>
                <button class="opt">ĐNT 2023</button>
                <button class="opt">ĐNT-BC 2023</button>
              </div>
            </div>

            <div class="row-item">
              <span class="label">Số lượng:</span>
              <div class="qty">
                <button>-</button>
                <span>1</span>
                <button>+</button>
              </div>
            </div>

            <div class="actions">
              <button class="btn-outline">🛒 Thêm vào giỏ hàng</button>
              <button class="btn-primary">Mua ngay</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Tabs -->
    <div class="info-box">
      <div class="tabs">
        <span class="tab active">THÔNG TIN SẢN PHẨM</span>
        <span class="tab">MÔ TẢ SẢN PHẨM</span>
      </div>

      <div class="content">
        <table>
          <tr>
            <td>ISBN</td>
            <td>{{ book.isbn }}</td>
          </tr>
          <tr>
            <td>Tác giả</td>
            <td>{{ book.tacGia }}</td>
          </tr>
          <tr>
            <td>Giá</td>
            <td>{{ formatPrice(book.gia) }} đ</td>
          </tr>
          <tr>
            <td>Đánh giá</td>
            <td>{{ book.avgRating ?? 5 }}/5</td>
          </tr>
        </table>

        <p class="desc">
          {{ book.moTa }}
        </p>
      </div>
    </div>

    <!-- Rating -->
    <div class="rating-box">
      <h5>Đánh giá sản phẩm</h5>

      <div class="rating-row">
        <div class="rating-score">
          <div class="big">{{ book.avgRating ?? 5 }}</div>
          <div>/5</div>
        </div>

        <div class="rating-bars">
          <div v-for="n in 5" :key="n" class="bar-row">
            <span>{{ 6 - n }} sao</span>
            <div class="bar">
              <div class="fill" style="width: 100%"></div>
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
  const id = route.params.id;
  const res = await axios.get(`http://localhost:8080/api/books/${id}`);
  book.value = res.data;
});

const formatPrice = (price) => {
  if (!price) return "0";
  return Number(price).toLocaleString("vi-VN");
};
</script>

<style scoped>
.book-detail {
  margin-top: 20px;
}

.breadcrumb-box {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.breadcrumb-box .active {
  font-weight: 600;
  color: #000;
}

.detail-box {
  border: 1px solid #ddd;
  padding: 20px;
}

.image-box {
  border: 1px solid #ddd;
  padding: 10px;
}

.image-box img {
  width: 100%;
  height: 420px;
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
  object-fit: cover;
}

.more-thumb {
  background: #333;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
}

.title {
  font-weight: 700;
}

.muted {
  color: #777;
  font-size: 14px;
}

.price {
  font-size: 22px;
  font-weight: 700;
  color: #d12e28;
  margin: 10px 0;
}

.stock {
  color: #666;
  margin-bottom: 15px;
}

.buy-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.buy-row input {
  width: 70px;
  padding: 6px;
}

.btn-outline {
  border: 1px solid #d12e28;
  color: #d12e28;
  background: #fff;
  padding: 8px 16px;
}

.btn-primary {
  background: #d12e28;
  color: #fff;
  border: none;
  padding: 8px 20px;
}

.info-box {
  margin-top: 30px;
  border: 1px solid #ddd;
}

.tabs {
  display: flex;
  border-bottom: 1px solid #ddd;
}

.tab {
  padding: 10px 20px;
  font-weight: 600;
  cursor: pointer;
}

.tab.active {
  border-bottom: 3px solid #d12e28;
  color: #d12e28;
}

.content {
  padding: 20px;
}

.content table {
  width: 100%;
  border-collapse: collapse;
}

.content td {
  border: 1px solid #ddd;
  padding: 8px;
}

.desc {
  margin-top: 20px;
  line-height: 1.7;
}

.rating-box {
  margin-top: 30px;
  border: 1px solid #ddd;
  padding: 20px;
}

.rating-row {
  display: flex;
  gap: 40px;
  margin-top: 10px;
}

.rating-score {
  text-align: center;
}

.rating-score .big {
  font-size: 40px;
  font-weight: 700;
  color: #f59e0b;
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
  height: 100%;
  background: #f59e0b;
}
</style>
