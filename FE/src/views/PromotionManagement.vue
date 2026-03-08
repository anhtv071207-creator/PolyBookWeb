<template>
  <div class="container">
    <h2>Quản lý khuyến mãi</h2>

    <div class="form-box">
      <h3>{{ editing ? "Sửa khuyến mãi" : "Thêm khuyến mãi" }}</h3>

      <select v-model="form.bookId">
        <option disabled value="">Chọn sách</option>
        <option v-for="b in books" :key="b.id" :value="b.id">
          {{ b.tieuDe }}
        </option>
      </select>

      <input
        type="number"
        v-model="form.chietKhau"
        placeholder="Chiết khấu (%)"
      />

      <input type="datetime-local" v-model="form.batDau" />

      <input type="datetime-local" v-model="form.ketThuc" />

      <button @click="savePromotion">
        {{ editing ? "Cập nhật" : "Thêm" }}
      </button>

      <button v-if="editing" @click="resetForm">Hủy</button>
    </div>

    <table class="promo-table">
      <thead>
        <tr>
          <th>Sách</th>
          <th>Chiết khấu</th>
          <th>Bắt đầu</th>
          <th>Kết thúc</th>
          <th>Thao tác</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="p in promotions" :key="p.id">
          <td>{{ p.book?.tieuDe }}</td>
          <td>{{ p.chietKhau }}%</td>
          <td>{{ formatDate(p.batDau) }}</td>
          <td>{{ formatDate(p.ketThuc) }}</td>

          <td>
            <button @click="editPromotion(p)">Sửa</button>

            <button @click="deletePromotion(p.id)">Xóa</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "@/services/api";

const promotions = ref([]);
const books = ref([]);

const editing = ref(false);

const form = ref({
  id: null,
  bookId: "",
  chietKhau: "",
  batDau: "",
  ketThuc: "",
});

onMounted(async () => {
  const promoRes = await api.get("/promotions");
  promotions.value = promoRes.data;

  const bookRes = await api.get("/books", {
    params: {
      page: 0,
      size: 1000,
    },
  });

  books.value = bookRes.data.content;
});

function getBookName(bookId) {
  const b = books.value.find((x) => x.id === bookId);
  return b ? b.tieuDe : "";
}

function savePromotion() {
  const payload = {
    bookId: form.value.bookId,
    chietKhau: form.value.chietKhau,
    batDau: form.value.batDau,
    ketThuc: form.value.ketThuc,
    active: true
  };

  if (editing.value) {
    api.put(`/promotions/${form.value.bookId}`, payload).then(loadPromotions);
  } else {
    api.post("/promotions", payload).then(loadPromotions);
  }

  resetForm();
}

function editPromotion(p) {
  editing.value = true;

  form.value = {
    id: p.id,
    bookId: p.book?.id,
    chietKhau: p.chietKhau,
    batDau: p.batDau?.slice(0, 16),
    ketThuc: p.ketThuc?.slice(0, 16),
  };
}

function deletePromotion(id) {
  if (!confirm("Xóa khuyến mãi?")) return;

  api.delete(`/promotions/${id}`).then(loadPromotions);
}

function resetForm() {
  editing.value = false;

  form.value = {
    id: null,
    bookId: "",
    chietKhau: "",
    batDau: "",
    ketThuc: "",
  };
}

async function loadPromotions() {
  const res = await api.get("/promotions");
  promotions.value = res.data;
}

function formatDate(date) {
  if (!date) return "";

  return new Date(date).toLocaleString();
}
</script>

<style scoped>
.container {
  max-width: 1000px;
  margin: auto;
  padding: 40px;
  background: #eef4ff;
  min-height: 100vh;
}

h2 {
  font-size: 24px;
  font-weight: 700;
  color: #007bff;
  margin-bottom: 30px;
}

.form-box {
  background: linear-gradient(145deg, #ffffff, #f4f9ff);
  border-radius: 22px;
  border: 2px solid #007bff;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 15px 35px rgba(0, 123, 255, 0.15);

  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-box h3 {
  color: #007bff;
  font-size: 18px;
  font-weight: 600;
}

.form-box input,
.form-box select {
  padding: 10px 12px;
  border-radius: 12px;
  border: 2px solid #cfe2ff;
  font-size: 14px;
  transition: 0.2s;
}

.form-box input:focus,
.form-box select:focus {
  border-color: #007bff;
  outline: none;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.15);
}

.form-box button {
  padding: 10px 18px;
  border-radius: 14px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: 0.25s;
  width: fit-content;
}

.form-box button:first-of-type {
  background: linear-gradient(135deg, #007bff, #00c6ff);
  color: white;
}

.form-box button:first-of-type:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(0, 123, 255, 0.35);
}

.form-box button:last-of-type {
  background: #e2e8f0;
}

.form-box button:last-of-type:hover {
  background: #cbd5e1;
}

.promo-table {
  width: 100%;
  border-collapse: collapse;
  background: linear-gradient(145deg, #ffffff, #f4f9ff);
  border-radius: 22px;
  overflow: hidden;
  border: 2px solid #007bff;
  box-shadow: 0 15px 35px rgba(0, 123, 255, 0.15);
}

.promo-table th {
  text-align: left;
  font-weight: 600;
  font-size: 15px;
  color: #007bff;
  padding: 16px;
  border-bottom: 2px solid #e6eefc;
}

.promo-table td {
  padding: 16px;
  border-bottom: 1px solid #e6eefc;
  font-size: 14px;
}

.promo-table tbody tr:hover {
  background: #f1f6ff;
  transition: 0.2s;
}

.promo-table button {
  padding: 6px 12px;
  border-radius: 10px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  margin-right: 6px;
  transition: 0.2s;
}

.promo-table button:first-child {
  background: #cffafe;
  color: #0891b2;
}

.promo-table button:first-child:hover {
  background: #0891b2;
  color: white;
}

.promo-table button:last-child {
  background: #fee2e2;
  color: #dc2626;
}

.promo-table button:last-child:hover {
  background: #dc2626;
  color: white;
}
</style>
