<script setup>
import { reactive, ref, onMounted } from "vue";
import api from "@/services/api";

const books = ref([]);
const previewUrl = ref(null);
const editPreviewUrl = ref(null);

const form = reactive({
  tieuDe: "",
  tacGia: "",
  isbn: "",
  gia: "",
  hangTon: "",
  moTa: "",
  coverImageUrl: "",
});

const editForm = reactive({
  id: null,
  tieuDe: "",
  tacGia: "",
  isbn: "",
  gia: "",
  hangTon: "",
  moTa: "",
  coverImageUrl: "",
});

const fetchBooks = async () => {
  const res = await api.get("/books");
  books.value = res.data;
};

onMounted(fetchBooks);

const onSelectImage = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  form.coverImageUrl = `/books/${file.name}`;
  previewUrl.value = URL.createObjectURL(file);
};

const onSelectEditImage = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  editForm.coverImageUrl = `/books/${file.name}`;
  editPreviewUrl.value = URL.createObjectURL(file);
};

const submitForm = async () => {
  const normalizedIsbn = form.isbn.replace(/-/g, "");

  await api.post("/books", {
    tieuDe: form.tieuDe,
    tacGia: form.tacGia,
    isbn: normalizedIsbn,
    gia: Number(form.gia),
    hangTon: Number(form.hangTon),
    moTa: form.moTa,
    coverImageUrl: form.coverImageUrl,
  });

  Object.keys(form).forEach((k) => (form[k] = ""));
  previewUrl.value = null;
  fetchBooks();
};

const startEdit = (book) => {
  Object.assign(editForm, {
    id: book.id,
    tieuDe: book.tieuDe,
    tacGia: book.tacGia,
    isbn: book.isbn,
    gia: book.gia,
    hangTon: book.hangTon,
    moTa: book.moTa,
    coverImageUrl: book.images?.find((img) => img.biaSach)?.url || "",
  });

  editPreviewUrl.value = editForm.coverImageUrl
    ? `http://localhost:8080${editForm.coverImageUrl}`
    : null;
};

const updateBook = async () => {
  if (!editForm.id) return;
  await api.put(`/books/${editForm.id}`, {
    tieuDe: editForm.tieuDe,
    tacGia: editForm.tacGia,
    isbn: editForm.isbn,
    gia: Number(editForm.gia),
    hangTon: Number(editForm.hangTon),
    moTa: editForm.moTa,
    coverImageUrl: editForm.coverImageUrl,
  });

  Object.keys(editForm).forEach((k) => (editForm[k] = ""));
  editPreviewUrl.value = null;
  fetchBooks();
};

const deleteBook = async (id) => {
  await api.delete(`/books/${id}`);
  fetchBooks();
};
</script>

<template>
  <div class="page">
    <div class="form-wrapper">
      <div class="card add-form">
        <h2>Thêm sách</h2>
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label>Tiêu đề</label>
            <input type="text" v-model="form.tieuDe" required />
          </div>

          <div class="form-group">
            <label>Tác giả</label>
            <input type="text" v-model="form.tacGia" required />
          </div>

          <div class="form-group">
            <label>ISBN</label>
            <input type="text" v-model="form.isbn" required />
          </div>

          <div class="form-group">
            <label>Giá</label>
            <input type="number" v-model="form.gia" required />
          </div>

          <div class="form-group">
            <label>Hàng tồn</label>
            <input type="number" v-model="form.hangTon" required />
          </div>

          <div class="form-group">
            <label>Mô tả</label>
            <textarea v-model="form.moTa"></textarea>
          </div>

          <div class="form-group">
            <label>Ảnh bìa</label>
            <input
              type="file"
              accept="image/*"
              @change="onSelectImage"
              required
            />
          </div>

          <div v-if="previewUrl" class="preview-box">
            <img :src="previewUrl" />
          </div>

          <button class="btn primary">Thêm sách</button>
        </form>
      </div>

      <div class="card edit-form">
        <h2>Sửa sách</h2>
        <form @submit.prevent="updateBook">
          <div class="form-group">
            <label>Tiêu đề</label>
            <input type="text" v-model="editForm.tieuDe" required />
          </div>

          <div class="form-group">
            <label>Tác giả</label>
            <input type="text" v-model="editForm.tacGia" required />
          </div>

          <div class="form-group">
            <label>ISBN</label>
            <input type="text" v-model="editForm.isbn" required />
          </div>

          <div class="form-group">
            <label>Giá</label>
            <input type="number" v-model="editForm.gia" required />
          </div>

          <div class="form-group">
            <label>Hàng tồn</label>
            <input type="number" v-model="editForm.hangTon" required />
          </div>

          <div class="form-group">
            <label>Mô tả</label>
            <textarea v-model="editForm.moTa"></textarea>
          </div>

          <div class="form-group">
            <label>Ảnh bìa</label>
            <input type="file" accept="image/*" @change="onSelectEditImage" />
          </div>

          <div v-if="editPreviewUrl" class="preview-box">
            <img :src="editPreviewUrl" />
          </div>

          <button class="btn primary" :disabled="!editForm.id">Cập nhật</button>
        </form>
      </div>
    </div>

    <div class="card list-card">
      <h2>Danh sách sách</h2>

      <div v-for="book in books" :key="book.id" class="book-item">
        <img
          v-if="book.images && book.images.length"
          :src="
            book.images.find((img) => img.biaSach)?.url || book.images[0].url
          "
          class="book-thumb"
        />

        <div class="book-content">
          <h3 class="title">{{ book.tieuDe }}</h3>
          <p class="author">{{ book.tacGia }}</p>
          <div class="book-meta">
            <span class="price">
              {{ Number(book.gia).toLocaleString("vi-VN") }} VND
            </span>
            <span class="stock">Hàng tồn: {{ book.hangTon }}</span>
          </div>
        </div>

        <div class="actions">
          <button class="btn small primary" @click="startEdit(book)">
            Sửa
          </button>
          <button class="btn small primary" @click="deleteBook(book.id)">
            Xóa
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 40px 5%;
}

.form-wrapper {
  display: flex;
  gap: 30px;
  align-items: stretch;
  margin-bottom: 40px;
}

.card {
  flex: 1;
  background: #ffffff;
  padding: 30px;
  border-radius: 14px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
}

.card h2 {
  margin-bottom: 25px;
  font-size: 22px;
}

/* ================= ADD FORM ================= */
.add-form .form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 18px;
}

.add-form .form-group label {
  margin-bottom: 6px;
  font-weight: 600;
}

.add-form .form-group input,
.add-form .form-group textarea {
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
}

.add-form .form-group input:focus,
.add-form .form-group textarea:focus {
  outline: none;
  border-color: #007bff;
}

.add-form textarea {
  min-height: 90px;
  resize: vertical;
}

.add-form .preview-box img {
  width: 160px;
  border-radius: 8px;
  margin-top: 10px;
}

/* ================= EDIT FORM ================= */
.edit-form .form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 18px;
}

.edit-form .form-group label {
  margin-bottom: 6px;
  font-weight: 600;
}

.edit-form .form-group input,
.edit-form .form-group textarea {
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
}

.edit-form .form-group input:focus,
.edit-form .form-group textarea:focus {
  outline: none;
  border-color: #007bff;
}

.edit-form textarea {
  min-height: 90px;
  resize: vertical;
}

.edit-form .preview-box img {
  width: 160px;
  border-radius: 8px;
  margin-top: 10px;
}

.btn {
  width: 100%;
  padding: 12px;
  border-radius: 10px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}

.primary {
  background: #007bff;
  color: white;
}

.warning {
  background: orange;
  color: white;
}

.danger {
  background: crimson;
  color: white;
}

.small {
  width: auto;
  padding: 6px 12px;
}

.list-card {
  margin-top: 10px;
}

.book-item {
  display: flex;
  align-items: flex-start;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
  gap: 20px;
}

.actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

@media (max-width: 900px) {
  .form-wrapper {
    flex-direction: column;
  }
}
.book-thumb {
  width: 90px;
  height: 130px;
  object-fit: cover;
  border-radius: 6px;
}
.book-item {
  display: flex;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
  gap: 24px;
}

.price {
  font-weight: 600;
  color: #007bff;
}

.stock {
  color: #777;
}
.title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.author {
  margin: 2px 0;
  color: #666;
  font-size: 14px;
}

.book-meta {
  display: flex;
  gap: 24px;
  margin-top: 8px;
  font-size: 14px;
}
</style>
