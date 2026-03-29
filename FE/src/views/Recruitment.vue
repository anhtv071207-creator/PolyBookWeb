<script setup>
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();
</script>

<template>
  <section class="career-page" :class="{ dark: theme.darkMode }">
    <div class="container">

      <div class="career-header">
        <h1>Tuyển dụng</h1>
        <p>
          PolyBook luôn tìm kiếm những cá nhân năng động, sáng tạo và yêu thích
          công nghệ để cùng xây dựng nền tảng bán sách trực tuyến dành cho sinh viên.
        </p>
      </div>

      <div class="career-section">
        <h2>Tại sao nên làm việc tại PolyBook?</h2>
        <ul>
          <li>Môi trường làm việc trẻ trung và năng động.</li>
          <li>Cơ hội học hỏi và phát triển kỹ năng công nghệ.</li>
          <li>Tham gia vào các dự án thực tế.</li>
          <li>Văn hóa làm việc thân thiện và hỗ trợ lẫn nhau.</li>
        </ul>
      </div>

      <div class="career-section">
        <h2>Vị trí đang tuyển</h2>

        <div class="job-card">
          <h3>Frontend Developer</h3>
          <p>
            Phát triển giao diện website bằng Vue.js, cải thiện trải nghiệm người dùng
            và tối ưu hiệu năng hiển thị.
          </p>
        </div>

        <div class="job-card">
          <h3>Backend Developer</h3>
          <p>
            Xây dựng và phát triển API, xử lý logic hệ thống và quản lý cơ sở dữ liệu.
          </p>
        </div>

        <div class="job-card">
          <h3>Customer Support</h3>
          <p>
            Hỗ trợ khách hàng, xử lý các vấn đề liên quan đến đơn hàng và trải nghiệm mua sắm.
          </p>
        </div>
      </div>

      <div class="career-section contact">
        <h2>Cách ứng tuyển</h2>
        <p>
          Nếu bạn quan tâm đến các vị trí trên, vui lòng gửi CV của bạn về email
          bên dưới. Chúng tôi sẽ liên hệ lại trong thời gian sớm nhất.
        </p>

        <p>Email: careers@polybook.com</p>
        <p>Hotline: 0123 456 789</p>
      </div>

    </div>
  </section>
</template>

<style scoped>
.career-page {
  padding: 40px 0 60px;
}

.career-header {
  text-align: center;
  max-width: 800px;
  margin: auto;
  margin-bottom: 40px;
}

.career-header h1 {
  font-size: 34px;
  font-weight: 700;
  margin-bottom: 10px;
}

.career-header p {
  color: #64748b;
}

.career-section {
  max-width: 900px;
  margin: auto;
  margin-bottom: 28px;
}

.career-section h2 {
  font-size: 22px;
  margin-bottom: 10px;
}

.career-section ul {
  padding-left: 20px;
}

.career-section li {
  margin-bottom: 6px;
}

.job-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 14px;
}

.job-card h3 {
  margin-bottom: 6px;
}

.job-card p {
  color: #475569;
}

.contact {
  text-align: center;
  margin-top: 40px;
}

/* DARK MODE */

.career-page.dark {
  background: #0f172a;
}

.career-page.dark .job-card {
  background: #1e293b;
  border-color: #334155;
}

.career-page.dark p,
.career-page.dark li {
  color: #cbd5f5;
}
</style>