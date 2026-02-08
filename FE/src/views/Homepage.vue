<template>
  <section class="container my-4">
    <div class="banner position-relative rounded overflow-hidden">
      <img src="/images/banner.jpg" class="banner-img" alt="Banner" />
      <button class="btn cta-btn position-absolute">Mua ngay</button>
    </div>
  </section>

  <section class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4 class="fw-bold mb-0">Sách bán chạy</h4>
      <a href="#" class="text-muted text-decoration-none">Xem tất cả</a>
    </div>

    <div class="row g-4">
      <div
        v-for="b in books"
        :key="b.id"
        class="col-6 col-md-3"
      >
        <router-link
          :to="`/book/${b.id}`"
          class="text-decoration-none text-dark"
        >
          <div class="card h-100 border-0 book-card">
            <img
              :src="b.url || '/books/default.jpg'"
              class="card-img-top book-img rounded"
              alt=""
            />

            <div class="card-body px-0">
              <h5 class="book-title mb-1">
                {{ b.tieuDe }}
              </h5>

              <div class="book-price">
                {{ formatPrice(b.gia) }} đ
              </div>
            </div>
          </div>
        </router-link>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "@/services/api";

const books = ref([]);

onMounted(async () => {
  try {
    const res = await api.get("/books/home");
    books.value = res.data;
  } catch (e) {
    console.error("Load homepage books failed", e);
  }
});

const formatPrice = (price) =>
  price ? Number(price).toLocaleString("vi-VN") : "0";
</script>


<style scoped>
.banner {
  position: relative;
  width: 100%;
}

.banner-img {
  width: 100%;
  height: auto;
  display: block;
}

.cta-btn {
  right: 2.25rem;
  bottom: 1.25rem;
  padding: 0.55rem 1.4rem;
  border-radius: 30px;
  z-index: 3;
  background-color: #d12e28;
  border: 1px;
  color: #fff;
  font-weight: 600;
  box-shadow: 0 6px 14px rgba(217, 48, 42, 0.18);
  transition: all 0.2s ease;
}

.cta-btn:hover {
  background-color: #ff3b30;
  color: #fff;
  box-shadow:
    0 8px 20px rgba(217, 48, 42, 0.35),
    0 0 0 6px rgba(217, 48, 42, 0.15);
}

.book-card {
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.book-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
}

.book-img {
  height: 250px;
  width: auto;
  object-fit: contain;
  background: #f8f8f8;
}

.book-title {
  font-size: 0.95rem;
  font-weight: 600;
}

.book-author {
  font-size: 0.85rem;
  color: #666;
}

.book-price {
  font-weight: 600;
  color: #d12e28;
}

@media (max-width: 767.98px) {
  .banner {
    height: 260px;
  }

  .cta-btn {
    right: 1rem;
    bottom: 1rem;
    padding: 0.45rem 1rem;
  }
}
</style>