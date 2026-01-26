<template>
  <header class="header">
    <!-- Left -->
    <div class="header-left">
      <img src="/logo.png" alt="Logo" class="logo" />
    </div>

    <!-- Center -->
    <div class="header-center">
      <div class="search-box">
        <span class="icon">☰</span>
        <input type="text" placeholder="Tìm sách" />
        <span class="icon">🔍</span>
      </div>
    </div>

    <!-- Right -->
    <div class="header-right">
      <div class="menu">
        Danh mục
        <span class="arrow">▼</span>
      </div>

      <button class="cart-btn">
        🛒 Giỏ Hàng
      </button>

      <div class="user-box">
        👤 ho_ten
        <span class="arrow">▼</span>
      </div>
    </div>
  </header>
</template>

<script setup>
// chưa xử lý logic
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
}

.header-left {
  width: 180px;
}

.logo {
  height: 40px;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.search-box {
  display: flex;
  align-items: center;
  width: 420px;
  background: #eee;
  border-radius: 999px;
  padding: 6px 12px;
  gap: 8px;
}

.search-box input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
}

.icon {
  font-size: 14px;
  cursor: pointer;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.menu {
  cursor: pointer;
}

.cart-btn {
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 20px;
  padding: 6px 14px;
  cursor: pointer;
}

.user-box {
  background: #111;
  color: #fff;
  padding: 6px 14px;
  border-radius: 20px;
  cursor: pointer;
}

.arrow {
  margin-left: 6px;
  font-size: 12px;
}
</style>

