<script setup>
import { ref, computed, watch } from "vue";
import { useRoute } from "vue-router";
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();
const route = useRoute();

const books = ref([]);
const loading = ref(false);

const keyword = computed(() => route.query.keyword);

const fetchBooks = async () => {
  if (!keyword.value) return;

  loading.value = true;

  const res = await api.get("/books/search", {
    params: {
      keyword: keyword.value,
      page: 0,  
      size: 12
    }
  });

  books.value = res.data.content;
  loading.value = false;
};

watch(keyword, fetchBooks, { immediate: true });
</script>

<template>
  <div class="container py-4">
    <h4>Kết quả tìm kiếm: "{{ keyword }}"</h4>

    <div v-if="loading">Đang tìm...</div>

    <div v-else class="row">
      <div v-for="book in books" :key="book.id" class="col-md-3 mb-4">
        <div class="card h-100">
          <img
            :src="book.images?.[0]?.url"
            class="card-img-top"
          />
          <div class="card-body">
            <h6>{{ book.tieuDe }}</h6>
            <div>{{ book.salePrice }} đ</div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="books.length === 0">
      Không tìm thấy sách.
    </div>
  </div>
</template>