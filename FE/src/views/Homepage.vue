<template>
  <section class="home-books">
    <div class="container">

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

                <span class="price-old">
                  {{ formatPrice(b.gia) }} đ
                </span>

              </div>

              <div v-else class="price-normal">
                {{ formatPrice(b.gia) }} đ
              </div>

            </div>

          </router-link>

        </div>
      </div>

    </div>
  </section>
</template>

<script setup>

import { ref, onMounted } from "vue"
import api from "@/services/api";

const books = ref([])

function formatPrice(v) {
  return Number(v).toLocaleString("vi-VN")
}

onMounted(async () => {
  try {

    const res = await api.get("/books/home")

    books.value = res.data

  } catch (e) {
    console.error("Load homepage books failed", e)
  }
})

</script>

<style scoped>
.banner {
  position: relative;

  width: 100%;
  max-width: 600px;

  margin-left: auto;
  margin-right: auto;

  border-radius: 18px;
  overflow: hidden;
}

.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.cta-btn {
  position: absolute;
  right: 2.5rem;
  bottom: 2rem;
  width: auto;
  display: inline-block;
  padding: 0.7rem 1.8rem;
  border-radius: 999px;
  background: #d12e28;
  color: #fff;
  font-weight: 600;
  border: none;
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
  position: relative;
  height: 250px;
  background: #f8f8f8;
}

.book-img img {
  height: 100%;
  width: 100%;
  object-fit: contain;
}

.book-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: #000;
}
.book-author {
  font-size: 0.85rem;
  color: #666;
}


.price-sale {
  font-weight: 600;
  color: #d12e28;
}

.price-normal {
  font-weight: 600;
  color: #d12e28;
}

.price-old {
  color: #666;
  text-decoration: line-through;
  margin-left: 6px;
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

.img-wrapper {
  position: relative;
}

.discount-ribbon {
  position: absolute;
  top: 10px;
  left: -6px;

  background: #e53935;
  color: white;

  font-size: 13px;
  font-weight: 700;

  padding: 5px 12px;

  transform: rotate(-15deg);

  box-shadow: 0 3px 8px rgba(0,0,0,0.2);
}
.grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
</style>
