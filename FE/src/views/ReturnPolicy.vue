<script setup>
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();
</script>

<template>
  <section class="return-page" :class="{ dark: theme.darkMode }">
    <div class="container">

      <div class="return-header">
        <h1>Chính sách đổi trả</h1>
        <p>
          PolyBook cam kết mang đến trải nghiệm mua sắm tốt nhất cho khách hàng.
          Nếu sản phẩm gặp vấn đề, bạn có thể yêu cầu đổi hoặc trả theo các điều
          kiện dưới đây.
        </p>
      </div>

      <div class="return-section">
        <h2>1. Điều kiện đổi trả</h2>
        <ul>
          <li>Sách bị lỗi in ấn, rách, thiếu trang hoặc sai nội dung.</li>
          <li>Sách bị hư hỏng trong quá trình vận chuyển.</li>
          <li>Khách hàng nhận nhầm sản phẩm so với đơn hàng.</li>
          <li>Sản phẩm còn nguyên trạng, chưa qua sử dụng.</li>
        </ul>
      </div>

      <div class="return-section">
        <h2>2. Thời gian đổi trả</h2>
        <p>
          Khách hàng có thể yêu cầu đổi hoặc trả sản phẩm trong vòng
          <strong>7 ngày</strong> kể từ ngày nhận hàng. Sau thời gian này,
          hệ thống sẽ không tiếp nhận yêu cầu đổi trả.
        </p>
      </div>

      <div class="return-section">
        <h2>3. Quy trình đổi trả</h2>
        <ol>
          <li>Liên hệ bộ phận hỗ trợ của PolyBook.</li>
          <li>Cung cấp mã đơn hàng và hình ảnh sản phẩm bị lỗi.</li>
          <li>Nhân viên xác nhận yêu cầu đổi trả.</li>
          <li>Khách hàng gửi lại sản phẩm theo hướng dẫn.</li>
          <li>PolyBook tiến hành đổi sản phẩm mới hoặc hoàn tiền.</li>
        </ol>
      </div>

      <div class="return-section">
        <h2>4. Phương thức hoàn tiền</h2>
        <p>
          Sau khi xác nhận sản phẩm hợp lệ để hoàn trả, PolyBook sẽ hoàn tiền
          thông qua phương thức thanh toán ban đầu hoặc chuyển khoản ngân hàng.
          Thời gian xử lý hoàn tiền từ <strong>3 - 7 ngày làm việc</strong>.
        </p>
      </div>

      <div class="return-section contact">
        <h2>5. Thông tin liên hệ</h2>
        <p>Email: polybook@gmail.com</p>
        <p>Hotline: 0123 456 789</p>
        <p>Địa chỉ: Hà Nội, Việt Nam</p>
      </div>

    </div>
  </section>
</template>

<style scoped>
.return-page {
  padding: 40px 0 60px;
}

/* HEADER */

.return-header {
  text-align: center;
  max-width: 800px;
  margin: auto;
  margin-bottom: 40px;
}

.return-header h1 {
  font-size: 34px;
  font-weight: 700;
  margin-bottom: 10px;
}

.return-header p {
  color: #64748b;
}

/* SECTION */

.return-section {
  max-width: 900px;
  margin: auto;
  margin-bottom: 28px;
}

.return-section h2 {
  font-size: 20px;
  margin-bottom: 8px;
}

.return-section p {
  color: #475569;
  line-height: 1.7;
}

.return-section ul,
.return-section ol {
  padding-left: 20px;
}

.return-section li {
  margin-bottom: 6px;
}

/* CONTACT */

.contact {
  text-align: center;
  margin-top: 40px;
}

/* DARK MODE */

.return-page.dark {
  background: #0f172a;
}

.return-page.dark p,
.return-page.dark li {
  color: #cbd5f5;
}
</style>