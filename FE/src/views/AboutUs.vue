<script setup>
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();
</script>

<template>
  <section class="about-page" :class="{ dark: theme.darkMode }">
    <div class="container">

      <!-- BANNER -->
      <div class="about-banner">
        <h1>About PolyBook</h1>
        <p>Nền tảng mua sách trực tuyến dành cho sinh viên</p>
      </div>

      <!-- GIỚI THIỆU -->
      <div class="about-section">
        <h2>Giới thiệu</h2>
        <p>
          PolyBook là một hệ thống bán sách trực tuyến được xây dựng nhằm giúp
          sinh viên dễ dàng tìm kiếm, mua và khám phá nhiều đầu sách khác nhau.
          Website cung cấp nhiều thể loại sách như công nghệ, kinh tế, kỹ năng,
          và tiểu thuyết.
        </p>

        <p>
          Hệ thống được thiết kế với mục tiêu mang lại trải nghiệm mua sách
          nhanh chóng, giao diện thân thiện và quản lý đơn hàng hiệu quả.
        </p>
      </div>

      <!-- MỤC TIÊU -->
      <div class="about-grid">
        <div class="about-card">
          <h3>Sứ mệnh</h3>
          <p>
            Giúp sinh viên tiếp cận tri thức dễ dàng hơn thông qua nền tảng mua
            sách trực tuyến tiện lợi.
          </p>
        </div>

        <div class="about-card">
          <h3>Tầm nhìn</h3>
          <p>
            Trở thành một trong những hệ thống bán sách trực tuyến thân thiện
            và hữu ích cho sinh viên.
          </p>
        </div>

        <div class="about-card">
          <h3>Giá trị</h3>
          <p>
            Chất lượng sách, trải nghiệm người dùng và dịch vụ khách hàng luôn
            được đặt lên hàng đầu.
          </p>
        </div>
      </div>

      <!-- CONTACT -->
      <div class="about-contact">
        <h2>Liên hệ</h2>
        <p>Email: polybook@gmail.com</p>
        <p>Hotline: 0123 456 789</p>
        <p>Địa chỉ: Hà Nội, Việt Nam</p>
      </div>

    </div>
  </section>
</template>

<style scoped>
.about-page {
  padding: 40px 0 60px;
}

/* BANNER */

.about-banner {
  text-align: center;
  margin-bottom: 40px;
}

.about-banner h1 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 10px;
}

.about-banner p {
  color: #64748b;
}

/* SECTION */

.about-section {
  max-width: 800px;
  margin: auto;
  text-align: center;
  margin-bottom: 50px;
}

.about-section h2 {
  font-size: 26px;
  margin-bottom: 14px;
}

.about-section p {
  line-height: 1.7;
  color: #475569;
}

/* GRID */

.about-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 50px;
}

.about-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
}

.about-card h3 {
  margin-bottom: 10px;
}

.about-card p {
  color: #64748b;
}

/* CONTACT */

.about-contact {
  text-align: center;
}

.about-contact h2 {
  margin-bottom: 12px;
}

/* RESPONSIVE */

@media (max-width: 900px) {
  .about-grid {
    grid-template-columns: 1fr;
  }
}

/* DARK MODE */

.about-page.dark {
  background: #0f172a;
}

.about-page.dark .about-card {
  background: #1e293b;
  border-color: #334155;
}

.about-page.dark p {
  color: #cbd5f5;
}
</style>