<template>
  <div class="container py-4">
    <nav class="breadcrumb-custom mb-3">
      <span>Trang chủ</span>
      <span class="divider">/</span>
      <span>Danh mục</span>
      <span class="divider">/</span>
      <span class="current">{{ categoryName }}</span>
    </nav>

    <div class="category-header mb-4">
      <h4 class="fw-bold mb-1">Sách theo danh mục: {{ categoryName }}</h4>
      <p class="text-muted small mb-0">{{ books.length }} sản phẩm</p>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-danger"></div>
    </div>

    <div v-else-if="error" class="text-danger text-center py-5">
      {{ error }}
    </div>

    <div v-else-if="books.length === 0" class="empty-box">
      Không có sách trong danh mục này
    </div>

    <div v-else class="row g-4">
      <div v-for="book in books" :key="book.id" class="col-6 col-md-3">
        <router-link
          :to="`/book/${book.id}`"
          class="text-decoration-none text-dark"
        >
          <div class="card h-100 border-0 book-card">
            <div class="img-wrapper">
              <div v-if="book.discount > 0" class="sale-badge">
                -{{ book.discount }}%
              </div>

              <img
                :src="
                  book.images?.find((i) => i.biaSach)?.url ||
                  '/books/default.jpg'
                "
                class="book-img"
              />
            </div>

            <div class="card-body px-2 pt-3 pb-3">
              <h6 class="book-title">
                {{ book.tieuDe }}
              </h6>

              <p class="book-author mb-1">
                {{ book.tacGia }}
              </p>

              <div v-if="book.discount > 0" class="price-box">
                <span class="old-price">
                  {{ book.gia?.toLocaleString() }} đ
                </span>

                <span class="new-price">
                  {{ book.salePrice?.toLocaleString() }} đ
                </span>
              </div>

              <div v-else class="book-price">
                {{ book.gia?.toLocaleString() }} đ
              </div>
            </div>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { useRoute } from "vue-router";
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();

const route = useRoute();

const books = ref([]);
const loading = ref(false);
const error = ref(null);
const categoryName = ref("");
const fetchCategory = async (id) => {
  try {
    const res = await api.get(`/categories/${id}`);
    categoryName.value = res.data.tenDanhMuc;
  } catch (err) {
    categoryName.value = "Danh mục";
  }
};
const categoryId = computed(() => route.params.id);

const fetchBooks = async (id) => {
  try {
    loading.value = true;
    error.value = null;

    const res = await api.get(`/books/category/${id}?page=0&size=12`);

    books.value = res.data.content;
  } catch (err) {
    error.value = "Không tải được dữ liệu";
  } finally {
    loading.value = false;
  }
};

watch(
  categoryId,
  (newId) => {
    if (newId) fetchBooks(newId);
    fetchCategory(newId);
  },
  { immediate: true },
);
</script>

<style scoped>
.breadcrumb-custom {
  font-size: 0.9rem;
  color: #888;
}

.breadcrumb-custom .divider {
  margin: 0 6px;
}

.breadcrumb-custom .current {
  color: #333;
  font-weight: 500;
}

.category-header h4 {
  font-size: 1.4rem;
  font-weight: 600;
}

.empty-box {
  padding: 60px 0;
  text-align: center;
  color: #888;
}

/* CARD */

.book-card {
  border-radius: 10px;
  background: #fff;
  border: 1px solid #eee;
  transition: 0.2s;
}

.book-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
}

/* IMAGE */

.img-wrapper {
  overflow: hidden;
  border-radius: 10px 10px 0 0;
  background: #fafafa;
}

.book-img {
  width: 100%;
  aspect-ratio: 2 / 3;
  object-fit: cover;
  transition: 0.25s;
}

.book-card:hover .book-img {
  transform: scale(1.04);
}

/* TITLE */

.book-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: #222;
  line-height: 1.4;
  min-height: 40px;
}

/* AUTHOR */

.book-author {
  font-size: 0.85rem;
  color: #777;
}

/* PRICE */

.book-price {
  font-weight: 600;
  color: #222;
  font-size: 0.95rem;
}

.price-box {
  display: flex;
  align-items: center;
  gap: 6px;
}

.old-price {
  text-decoration: line-through;
  color: #aaa;
  font-size: 0.8rem;
}

.new-price {
  font-weight: 600;
  color: #111;
  font-size: 0.95rem;
}
.sale-badge {
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
.book-title {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
/* ===== DARK MODE CATEGORY PAGE ===== */

.dark .container {
  color: #e2e8f0;
}

/* breadcrumb */

.dark .breadcrumb-custom {
  color: #94a3b8;
}

.dark .breadcrumb-custom .current {
  color: #e2e8f0;
}

/* header */

.dark .category-header h4 {
  color: #f1f5f9;
}

.dark .text-muted {
  color: #94a3b8 !important;
}

/* empty */

.dark .empty-box {
  color: #94a3b8;
}

/* card */

.dark .book-card {
  background: #1e293b;
  border-color: #334155;
}

.dark .book-card:hover {
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.5);
}

/* image */

.dark .img-wrapper {
  background: #0f172a;
}

/* title */

.dark .book-title {
  color: #f1f5f9;
}

/* author */

.dark .book-author {
  color: #94a3b8;
}

/* price */

.dark .book-price {
  color: #e2e8f0;
}

.dark .old-price {
  color: #64748b;
}

.dark .new-price {
  color: #f87171;
}

/* router link text */

.dark a {
  color: inherit;
}
</style>
