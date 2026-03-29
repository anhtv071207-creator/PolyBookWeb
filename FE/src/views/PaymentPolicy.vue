<script setup>
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();
</script>

<template>
  <section class="payment-page" :class="{ dark: theme.darkMode }">
    <div class="container">

      <div class="payment-header">
        <h1>Chính sách thanh toán</h1>
        <p>
          PolyBook cung cấp nhiều phương thức thanh toán nhằm mang lại sự thuận
          tiện và an toàn cho khách hàng khi mua sách trên hệ thống.
        </p>
      </div>

      <div class="payment-section">
        <h2>1. Các phương thức thanh toán</h2>
        <ul>
          <li>Thanh toán khi nhận hàng (COD).</li>
          <li>Chuyển khoản ngân hàng.</li>
          <li>Thanh toán qua ví điện tử hoặc cổng thanh toán trực tuyến (nếu có).</li>
        </ul>
      </div>

      <div class="payment-section">
        <h2>2. Thanh toán khi nhận hàng (COD)</h2>
        <p>
          Khách hàng có thể thanh toán trực tiếp cho nhân viên giao hàng khi
          nhận sản phẩm. Vui lòng kiểm tra sản phẩm trước khi thanh toán để
          đảm bảo đơn hàng chính xác.
        </p>
      </div>

      <div class="payment-section">
        <h2>3. Thanh toán chuyển khoản</h2>
        <p>
          Khách hàng có thể thanh toán bằng cách chuyển khoản vào tài khoản
          ngân hàng của PolyBook. Sau khi chuyển khoản, vui lòng cung cấp
          mã đơn hàng để chúng tôi xác nhận thanh toán và tiến hành xử lý đơn hàng.
        </p>

        <p><strong>Thông tin tài khoản:</strong></p>
        <p>Ngân hàng: ABC Bank</p>
        <p>Chủ tài khoản: PolyBook</p>
        <p>Số tài khoản: 123456789</p>
      </div>

      <div class="payment-section">
        <h2>4. Xác nhận thanh toán</h2>
        <p>
          Sau khi thanh toán thành công, hệ thống sẽ ghi nhận và gửi thông báo
          xác nhận đơn hàng. Trong trường hợp có sự cố thanh toán, khách hàng
          có thể liên hệ bộ phận hỗ trợ để được giải quyết.
        </p>
      </div>

      <div class="payment-section">
        <h2>5. Bảo mật thông tin thanh toán</h2>
        <p>
          PolyBook cam kết bảo mật thông tin thanh toán của khách hàng và không
          lưu trữ các dữ liệu nhạy cảm liên quan đến tài khoản ngân hàng hoặc
          phương thức thanh toán của người dùng.
        </p>
      </div>

      <div class="payment-section contact">
        <h2>6. Thông tin liên hệ</h2>
        <p>Email: polybook@gmail.com</p>
        <p>Hotline: 0123 456 789</p>
        <p>Địa chỉ: Hà Nội, Việt Nam</p>
      </div>

    </div>
  </section>
</template>

<style scoped>
.payment-page {
  padding: 40px 0 60px;
}

.payment-header {
  text-align: center;
  max-width: 800px;
  margin: auto;
  margin-bottom: 40px;
}

.payment-header h1 {
  font-size: 34px;
  font-weight: 700;
  margin-bottom: 10px;
}

.payment-header p {
  color: #64748b;
}

.payment-section {
  max-width: 900px;
  margin: auto;
  margin-bottom: 28px;
}

.payment-section h2 {
  font-size: 20px;
  margin-bottom: 8px;
}

.payment-section p {
  color: #475569;
  line-height: 1.7;
}

.payment-section ul {
  padding-left: 20px;
}

.payment-section li {
  margin-bottom: 6px;
}

.contact {
  text-align: center;
  margin-top: 40px;
}

/* DARK MODE */

.payment-page.dark {
  background: #0f172a;
}

.payment-page.dark p,
.payment-page.dark li {
  color: #cbd5f5;
}
</style>