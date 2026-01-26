<template>
  <div>
    <h2>Danh sách sách</h2>

    <ul>
      <li v-for="book in books" :key="book.id">
        <b>{{ book.tieuDe }}</b> - {{ book.tacGia }} - {{ book.gia }} đ
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "@/services/api";

const books = ref([]);

onMounted(async () => {
  try {
    const res = await api.get("/books");
    books.value = res.data;
  } catch (err) {
    console.error(err);
  }
});
</script>
