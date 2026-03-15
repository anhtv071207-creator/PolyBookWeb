<template>
<footer class="app-footer">

  <div class="container footer-grid">

    <div class="footer-col">
      <h3>Polybook</h3>
      <p>Hệ thống bán sách trực tuyến.</p>
    </div>

    <div class="footer-col">
      <h3>Giới thiệu</h3>
      <RouterLink to="/about">Về chúng tôi</RouterLink>
      <RouterLink to="/stores">Cửa hàng</RouterLink>
      <RouterLink to="/recruitment">Tuyển dụng</RouterLink>
    </div>

    <div class="footer-col">
      <h3>Chính sách</h3>
      <RouterLink to="/policy/privacy">Bảo mật</RouterLink>
      <RouterLink to="/policy/return">Đổi trả</RouterLink>
      <RouterLink to="/policy/payment">Thanh toán</RouterLink>
    </div>

    <div class="footer-col">
      <h3>Liên hệ</h3>
      <p>Hà Nội</p>
      <p>info@polybook.vn</p>
    </div>

  </div>

  <div class="footer-bottom">
    © 2025 Polybook
  </div>

</footer>
</template>
<script setup>
import { RouterLink } from "vue-router";
</script>

<style scoped>
.app-footer{
  background:#0f172a;
  color:#cbd5e1;
  margin-top:60px;
}

.footer-grid{
  display:grid;
  grid-template-columns:repeat(4,1fr);
  gap:40px;
  padding:40px 0;
}

.footer-col h3{
  color:white;
  margin-bottom:12px;
}

.footer-col a{
  display:block;
  margin-bottom:6px;
  color:#cbd5e1;
  text-decoration:none;
}

.footer-col a:hover{
  color:white;
}

.footer-bottom{
  text-align:center;
  padding:14px;
  border-top:1px solid rgba(255,255,255,0.1);
}
</style>
