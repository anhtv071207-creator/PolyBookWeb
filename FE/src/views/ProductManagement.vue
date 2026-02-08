<script setup>
import { reactive, ref } from "vue";
import api from "@/services/api";

const form = reactive({
  tieuDe: "",
  tacGia: "",
  isbn: "",
  gia: "",
  hangTon: "",
  moTa: "",
  coverImageUrl: "",
});

const previewUrl = ref(null);

const onSelectImage = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  form.coverImageUrl = `/books/${file.name}`;

  previewUrl.value = URL.createObjectURL(file);
};

const submitForm = async () => {
  if (!form.coverImageUrl) {
    alert("Vui lòng chọn ảnh bìa");
    return;
  }

  const normalizedIsbn = form.isbn.replace(/-/g, "");

  try {
    await api.post("/books", {
      tieuDe: form.tieuDe,
      tacGia: form.tacGia,
      isbn: Number(normalizedIsbn),
      gia: form.gia,
      hangTon: form.hangTon,
      moTa: form.moTa,
      coverImageUrl: form.coverImageUrl,
    });

    alert("Thêm sách thành công");

    Object.keys(form).forEach((k) => (form[k] = ""));
    previewUrl.value = null;
  } catch (e) {
    console.error(e);
    alert("Thêm sách thất bại");
  }
};
</script>

<template>
  <div class="create-book">
    <h2>Thêm sách</h2>

    <form @submit.prevent="submitForm">
      <div>
        <label>Tiêu đề</label>
        <input v-model="form.tieuDe" required />
      </div>

      <div>
        <label>Tác giả</label>
        <input v-model="form.tacGia" required />
      </div>

      <div>
        <label>ISBN</label>
        <input
          v-model="form.isbn"
          placeholder="VD: 978-604-1-23456-7"
          required
        />
      </div>

      <div>
        <label>Giá</label>
        <input type="number" v-model="form.gia" required />
      </div>

      <div>
        <label>Hàng tồn</label>
        <input type="number" v-model="form.hangTon" required />
      </div>

      <div>
        <label>Mô tả</label>
        <textarea v-model="form.moTa"></textarea>
      </div>

      <div>
        <label>Ảnh bìa</label>
        <input type="file" accept="image/*" @change="onSelectImage" required />
      </div>

      <!-- preview -->
      <div v-if="previewUrl">
        <p>Xem trước ảnh bìa</p>
        <img :src="previewUrl" class="preview" />
      </div>

      <button type="submit">Thêm sách</button>
    </form>
  </div>
</template>

<style scoped>
.create-book {
  max-width: 500px;
}

form div {
  margin-bottom: 12px;
}

.preview {
  width: 150px;
  border: 1px solid #ccc;
}
</style>
