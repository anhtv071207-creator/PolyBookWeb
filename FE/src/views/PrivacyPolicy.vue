<script setup>
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();
</script>

<template>
  <section class="privacy-page" :class="{ dark: theme.darkMode }">
    <div class="container">

      <div class="privacy-header">
        <h1>Chính sách bảo mật</h1>
        <p>
          Chính sách này giải thích cách PolyBook thu thập, sử dụng và bảo vệ
          thông tin cá nhân của người dùng khi sử dụng website.
        </p>
      </div>

      <div class="privacy-section">
        <h2>1. Thông tin chúng tôi thu thập</h2>
        <p>
          Khi sử dụng hệ thống PolyBook, chúng tôi có thể thu thập một số thông
          tin cá nhân như họ tên, email, số điện thoại, địa chỉ giao hàng và
          thông tin liên quan đến đơn hàng.
        </p>
      </div>

      <div class="privacy-section">
        <h2>2. Mục đích sử dụng thông tin</h2>
        <ul>
          <li>Xử lý đơn hàng và giao hàng cho khách.</li>
          <li>Cải thiện trải nghiệm người dùng trên website.</li>
          <li>Hỗ trợ khách hàng khi có vấn đề phát sinh.</li>
          <li>Gửi thông báo liên quan đến đơn hàng hoặc dịch vụ.</li>
        </ul>
      </div>

      <div class="privacy-section">
        <h2>3. Bảo mật thông tin</h2>
        <p>
          PolyBook cam kết bảo vệ thông tin cá nhân của người dùng. Chúng tôi
          áp dụng các biện pháp kỹ thuật và quản lý nhằm đảm bảo dữ liệu không
          bị truy cập trái phép, mất mát hoặc tiết lộ.
        </p>
      </div>

      <div class="privacy-section">
        <h2>4. Chia sẻ thông tin</h2>
        <p>
          Chúng tôi không bán hoặc chia sẻ thông tin cá nhân của người dùng cho
          bên thứ ba, ngoại trừ các trường hợp cần thiết để thực hiện việc giao
          hàng, xử lý thanh toán hoặc theo yêu cầu của pháp luật.
        </p>
      </div>

      <div class="privacy-section">
        <h2>5. Quyền của người dùng</h2>
        <p>
          Người dùng có quyền yêu cầu xem, chỉnh sửa hoặc xóa thông tin cá nhân
          của mình trong hệ thống. Nếu có yêu cầu, vui lòng liên hệ với chúng
          tôi qua thông tin bên dưới.
        </p>
      </div>

      <div class="privacy-section contact">
        <h2>6. Liên hệ</h2>
        <p>Email: polybook@gmail.com</p>
        <p>Hotline: 0123 456 789</p>
        <p>Địa chỉ: Hà Nội, Việt Nam</p>
      </div>

    </div>
  </section>
</template>

<style scoped>
.privacy-page {
  padding: 40px 0 60px;
}

/* HEADER */

.privacy-header {
  text-align: center;
  max-width: 800px;
  margin: auto;
  margin-bottom: 40px;
}

.privacy-header h1 {
  font-size: 34px;
  font-weight: 700;
  margin-bottom: 10px;
}

.privacy-header p {
  color: #64748b;
}

/* SECTION */

.privacy-section {
  max-width: 900px;
  margin: auto;
  margin-bottom: 28px;
}

.privacy-section h2 {
  font-size: 20px;
  margin-bottom: 8px;
}

.privacy-section p {
  color: #475569;
  line-height: 1.7;
}

.privacy-section ul {
  padding-left: 20px;
}

.privacy-section li {
  margin-bottom: 6px;
}

/* CONTACT */

.contact {
  text-align: center;
  margin-top: 40px;
}

/* DARK MODE */

.privacy-page.dark {
  background: #0f172a;
}

.privacy-page.dark p,
.privacy-page.dark li {
  color: #cbd5f5;
}
</style>