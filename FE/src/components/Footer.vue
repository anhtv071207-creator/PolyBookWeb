<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3>GIỚI THIỆU</h3>
          <ul>
            <li><RouterLink to="/about">Về An Nam</RouterLink></li>
            <li><RouterLink to="/stores">Hệ thống hiệu sách</RouterLink></li>
            <li>
              <RouterLink to="/distribution">Hệ thống phát hành</RouterLink>
            </li>
            <li><RouterLink to="/recruitment">Tuyển dụng</RouterLink></li>
            <li>
              <RouterLink to="/contact">Liên hệ với chúng tôi</RouterLink>
            </li>
          </ul>
        </div>

        <div class="footer-section">
          <h3>CHÍNH SÁCH</h3>
          <ul>
            <li>
              <RouterLink to="/policy/privacy">Chính sách bảo mật</RouterLink>
            </li>
            <li>
              <RouterLink to="/policy/return">Chính sách đổi trả</RouterLink>
            </li>
            <li>
              <RouterLink to="/policy/payment"
                >Chính sách thanh toán</RouterLink
              >
            </li>
            <li>
              <RouterLink to="/policy/terms">Điều khoản sử dụng</RouterLink>
            </li>
          </ul>
        </div>

        <div class="footer-section">
          <h3>LIÊN HỆ</h3>
          <p>1 Vũ Tông Phan, Khương Đình, Hà Nội</p>
          <p>0243 XXXX XXX - 0243 YYYY YYY</p>
          <p>info@annam.vn</p>
        </div>

        <div class="footer-section">
          <h3>THEO DÕI CHÚNG TÔI</h3>
          <div class="social-links">
            <a href="#" title="Facebook" target="_blank" rel="noopener">f</a>
            <a href="#" title="Instagram" target="_blank" rel="noopener">📷</a>
            <a href="#" title="TikTok" target="_blank" rel="noopener">♫</a>
            <a href="#" title="YouTube" target="_blank" rel="noopener">▶</a>
          </div>
        </div>
      </div>

      <div class="footer-bottom">
        <p>&copy; 2025 Polybook Store.</p>
        <p>Bởi vì sách là thế giới 🌍</p>
      </div>
    </div>
  </footer>
</template>
<script setup>
import { RouterLink } from "vue-router";
</script>

<style scoped>
.footer {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 40px 0 20px;
  margin-top: 60px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 30px;
  margin-bottom: 30px;
}

.footer-section h3 {
  font-size: 16px;
  margin-bottom: 16px;
  font-weight: 600;

  color: #ffffff;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section li {
  margin-bottom: 10px;
}

.footer-section a {
  color: #bdc3c7;
  text-decoration: none;
  font-size: 14px;
}

.footer-section a:hover {
  color: #ffffff;
}

.footer-section p {
  font-size: 14px;
  color: #bdc3c7;
  text-align: left;
  margin-bottom: 8px;
}

.social-links {
  display: flex;
  gap: 12px;
}

.social-links a {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 16px;
  text-decoration: none;
  transition:
    background 0.3s,
    transform 0.3s;
}

.social-links a:hover {
  background: #667eea;
  transform: translateY(-3px);
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 15px;
  text-align: center;
  font-size: 13px;
  color: #bdc3c7;
}

.footer-bottom p {
  margin: 4px 0;
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .social-links {
    justify-content: center;
  }
}
</style>
