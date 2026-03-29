<template>
  <section class="home-books" :class="{ dark: theme.darkMode }">
    <div class="container">
      <div class="home-banner">
        <img src="/images/banner2.jpg" alt="Banner" />
      </div>
      <div class="grid">
        <div v-for="b in books" :key="b.id" class="book-card">
          <router-link :to="`/book/${b.id}`">
            <div class="book-img">
              <div v-if="b.discount" class="discount-ribbon">
                -{{ b.discount }}%
              </div>

              <img :src="b.url" alt="" />
            </div>

            <h3 class="book-title">
              {{ b.tieuDe }}
            </h3>

            <div class="price">
              <div v-if="b.discount" class="price-box">
                <span class="price-sale">
                  {{ formatPrice(b.salePrice) }} đ
                </span>

                <span class="price-old"> {{ formatPrice(b.gia) }} đ </span>
              </div>

              <div v-else class="price-normal">{{ formatPrice(b.gia) }} đ</div>
            </div>
          </router-link>
        </div>
      </div>
      <div class="pagination" v-if="totalPages > 1">
        <button
          class="page-btn"
          :disabled="page === 0"
          @click="changePage(page - 1)"
        >
          ←
        </button>

        <button
          v-for="p in totalPages"
          :key="p"
          class="page-btn"
          :class="{ active: page === p - 1 }"
          @click="changePage(p - 1)"
        >
          {{ p }}
        </button>

        <button
          class="page-btn"
          :disabled="page === totalPages - 1"
          @click="changePage(page + 1)"
        >
          →
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();

const books = ref([]);

const page = ref(0);
const size = 12;

const totalPages = ref(0);

function formatPrice(v) {
  return Number(v).toLocaleString("vi-VN");
}

async function loadBooks() {
  try {
    const res = await api.get(`/books/home?page=${page.value}&size=${size}`);

    books.value = res.data.content;
    totalPages.value = res.data.totalPages;
  } catch (e) {
    console.error("Load homepage books failed", e);
  }
}

function changePage(p) {
  page.value = p;
  loadBooks();

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}

onMounted(loadBooks);
</script>

<style scoped>
.home-books {
  padding: 32px 0 40px;
}

/* GRID LAYOUT */

.grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

@media (max-width: 1200px) {
  .grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .grid {
    grid-template-columns: repeat(1, 1fr);
  }
}

/* BOOK CARD */

.book-card {
  background: #fff;
  border-radius: 12px;
  padding: 14px;

  border: 1px solid #eee;

  transition: all 0.25s ease;
}

.book-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
}

/* LINK */

.book-card a {
  text-decoration: none;
  color: inherit;
}

/* IMAGE */

.book-img {
  position: relative;

  height: 220px;

  display: flex;
  align-items: center;
  justify-content: center;

  background: #fafafa;

  border-radius: 8px;

  margin-bottom: 12px;
}

.book-img img {
  max-height: 200px;
  max-width: 100%;
  object-fit: contain;
}

/* DISCOUNT RIBBON */

.discount-ribbon {
  position: absolute;
  top: 10px;
  left: -8px;

  background: linear-gradient(135deg, #ff4d4f, #d12e28);
  color: white;

  font-size: 12px;
  font-weight: 700;

  padding: 4px 10px;

  transform: rotate(-12deg);

  border-radius: 4px;

  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

/* TITLE */

.book-title {
  font-size: 15px;
  font-weight: 600;

  color: #222;

  margin-bottom: 8px;

  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;

  overflow: hidden;

  min-height: 42px;
}

/* PRICE */

.price {
  margin-top: 6px;
}

.price-box {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-sale {
  font-weight: 700;
  font-size: 16px;
  color: #d12e28;
}

.price-normal {
  font-weight: 700;
  font-size: 16px;
  color: #d12e28;
}

.price-old {
  font-size: 13px;
  color: #888;
  text-decoration: line-through;
}
.home-books.dark .book-card {
  background: #1e293b;
  border-color: #334155;
}

.home-books.dark .book-img {
  background: #0f172a;
}

.home-books.dark .book-title {
  color: #e2e8f0;
}

.home-books.dark .price-old {
  color: #94a3b8;
}
.pagination {
  margin-top: 32px;

  display: flex;
  justify-content: center;
  gap: 8px;
}

.page-btn {
  border: 1px solid #ddd;
  background: #fff;

  padding: 6px 12px;

  border-radius: 6px;

  cursor: pointer;

  transition: 0.2s;
}

.page-btn:hover {
  background: #f5f5f5;
}

.page-btn.active {
  background: #2563eb;
  color: white;
  border-color: #2830d1;
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.home-books.dark .page-btn {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.home-books.dark .page-btn.active {
  background: #2563eb;
  border-color: #2563eb;
}
.home-banner {
  margin-bottom: 28px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #eee;
}

.home-banner img {
  width: 100%;
  height: 280px;
  object-fit: cover;
  display: block;
}
</style>
