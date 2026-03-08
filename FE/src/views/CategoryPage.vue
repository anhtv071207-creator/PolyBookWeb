<template>
  <div class="container py-4">
    <nav class="breadcrumb-custom mb-3">
      <span>Trang chủ</span>
      <span class="divider">/</span>
      <span class="current">Danh mục</span>
    </nav>

    <div class="category-header mb-4">
      <h4 class="fw-bold mb-1">Sách theo danh mục</h4>
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

const route = useRoute();

const books = ref([]);
const loading = ref(false);
const error = ref(null);

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
  color: #d12e28;
  font-weight: 600;
}

.category-header h4 {
  font-size: 1.4rem;
}

.empty-box {
  padding: 60px 0;
  text-align: center;
  color: #888;
}

.img-wrapper {
  overflow: hidden;
  border-radius: 12px;
}

.book-img {
  width: 100%;
  aspect-ratio: 2 / 3;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.book-card {
  border-radius: 14px;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.book-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.08);
}

.book-card:hover .book-img {
  transform: scale(1.05);
}

.book-title {
  font-size: 0.95rem;
  font-weight: 600;
  min-height: 42px;
  line-height: 1.4;
}

.book-author {
  font-size: 0.85rem;
  color: #777;
}

.book-price {
  font-weight: 700;
  color: #d12e28;
  font-size: 1rem;
}
.price-box {
  display: flex;
  align-items: center;
  gap: 6px;
}

.old-price {
  text-decoration: line-through;
  color: #999;
  font-size: 0.85rem;
}

.new-price {
  font-weight: 700;
  color: #d12e28;
  font-size: 1rem;
}
</style>
