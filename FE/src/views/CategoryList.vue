<template>
  <div>
    <h2>Thể loại</h2>

    <ul>
      <li v-for="c in categories" :key="c.id">
        {{ c.tenDanhMuc }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "@/services/api";

const categories = ref([]);

onMounted(async () => {
  const res = await api.get("/categories");
  categories.value = res.data;
});
</script>
