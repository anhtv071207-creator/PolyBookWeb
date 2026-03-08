<template>
  <div class="page" :class="{ 'edit-mode': currentMode === 'edit' }">
    <div class="page-header">
      <button class="btn-back" @click="goBackManagement">← Quay lại</button>
      <h2>Quản lý sản phẩm</h2>
    </div>
    <div class="form-wrapper">
      <div class="side-tabs">
        <div
          class="tab-item"
          :class="{ 'active-add': currentMode === 'add' }"
          @click="setMode('add')"
        >
          Thêm
        </div>

        <div
          class="tab-item"
          :class="{ 'active-edit': currentMode === 'edit' }"
          @click="setMode('edit')"
        >
          Sửa
        </div>
      </div>
      <div class="tab-content">
        <div v-if="currentMode === 'add'">
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

              <button type="button" class="btn-category" @click="openAddPopup">
                Chọn danh mục
              </button>

              <div class="selected-tags">
                <span
                  v-for="cat in categories.filter((c) =>
                    selectedAddCategories.includes(c.id),
                  )"
                  :key="cat.id"
                  class="tag selected"
                >
                  {{ cat.tenDanhMuc }}
                </span>
              </div>

              <button class="btn primary" :disabled="showPopup">
                Thêm sách
              </button>
            </form>
          </div>
        </div>

        <div v-if="currentMode === 'edit'">
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
                <input
                  type="file"
                  accept="image/*"
                  @change="onSelectEditImage"
                />
              </div>

              <div v-if="editPreviewUrl" class="preview-box">
                <img :src="editPreviewUrl" />
              </div>

              <button type="button" class="btn-category" @click="openEditPopup">
                Chọn danh mục
              </button>

              <div class="selected-tags">
                <span
                  v-for="cat in categories.filter((c) =>
                    selectedEditCategories.includes(c.id),
                  )"
                  :key="cat.id"
                  class="tag selected"
                >
                  {{ cat.tenDanhMuc }}
                </span>
              </div>

              <button class="btn warning" :disabled="!editForm.id || showPopup">
                Cập nhật
              </button>
            </form>
          </div>
        </div>
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
            <span class="stock"> Còn {{ book.hangTon }} cuốn </span>
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
      <!-- Loading -->
      <div v-if="loading" style="text-align: center; padding: 10px">
        Đang tải...
      </div>

      <!-- Pagination -->
      <div class="pagination" v-if="totalPages > 1">
        <button @click="prevPage" :disabled="page === 0">←</button>

        <button
          v-for="p in totalPages"
          :key="p"
          @click="goToPage(p - 1)"
          :class="{ active: page === p - 1 }"
        >
          {{ p }}
        </button>

        <button @click="nextPage" :disabled="page >= totalPages - 1">→</button>
      </div>
    </div>
  </div>
  <div
    v-if="showPopup"
    class="popup-overlay"
    :class="{ 'edit-popup': currentMode === 'edit' }"
  >
    <div class="popup-wrapper">
      <div class="popup-header">
        <h3>Chọn danh mục</h3>

        <button class="btn-confirm" @click="showPopup = false">Xác nhận</button>
      </div>

      <div class="popup-box">
        <div class="box-title">Danh mục đã chọn</div>
        <div class="box-content selected-content">
          <span v-for="id in activeSelected" :key="id" class="selected-item">
            {{ categories.find((c) => c.id === id)?.tenDanhMuc }}
          </span>
        </div>
      </div>

      <div class="popup-box">
        <div class="box-title">Danh sách danh mục</div>
        <div class="box-content tag-container">
          <span
            v-for="cat in categories"
            :key="cat.id"
            class="tag-item"
            :class="{ active: activeSelected.includes(cat.id) }"
            @click="toggleCategory(cat.id)"
          >
            {{ cat.tenDanhMuc }}
          </span>
        </div>
      </div>
    </div>
  </div>
  <div v-if="showDeleteConfirm" class="delete-overlay">
    <div class="delete-box">
      <h3>Xác nhận xoá</h3>
      <p>Bạn chắc chắn muốn xoá sách này?</p>

      <div class="delete-actions">
        <button class="btn-cancel" @click="cancelDeleteBook">Hủy</button>
        <button class="btn-danger" @click="confirmDeleteBook">Xóa</button>
      </div>
    </div>
  </div>
</template>
<script setup>
import { reactive, ref, onMounted, computed } from "vue";
import api from "@/services/api";
import { useRoute, useRouter } from "vue-router";
import { watch } from "vue";

const currentMode = ref("add");
const books = ref([]);
const previewUrl = ref(null);
const editPreviewUrl = ref(null);
const categories = ref([]);
const showPopup = ref(false);
const selectedAddCategories = ref([]);
const selectedEditCategories = ref([]);
const page = ref(0);
const size = ref(5);
const totalPages = ref(0);
const totalElements = ref(0);
const loading = ref(false);
const route = useRoute();
const router = useRouter();
const showDeleteConfirm = ref(false);
const deleteTargetId = ref(null);

const setMode = (mode) => {
  currentMode.value = mode;
};

const revokePreview = (refUrl) => {
  if (refUrl && typeof refUrl.value === "string") {
    try {
      URL.revokeObjectURL(refUrl.value);
    } catch (e) {}
    refUrl.value = null;
  }
};

const resetAddForm = (form) => {
  form.tieuDe = "";
  form.tacGia = "";
  form.isbn = "";
  form.gia = "";
  form.hangTon = "";
  form.moTa = "";
  form.coverImageUrl = "";
  selectedAddCategories.value = [];
  revokePreview(previewUrl);
};

const resetEditForm = (editForm) => {
  editForm.id = null;
  editForm.tieuDe = "";
  editForm.tacGia = "";
  editForm.isbn = "";
  editForm.gia = "";
  editForm.hangTon = "";
  editForm.moTa = "";
  editForm.coverImageUrl = "";
  selectedEditCategories.value = [];
  revokePreview(editPreviewUrl);
};

const fetchCategories = async () => {
  try {
    const res = await api.get("/categories");
    const onlyChildren = [];

    res.data.forEach((parent) => {
      if (parent.danhMucCon && parent.danhMucCon.length > 0) {
        parent.danhMucCon.forEach((child) => {
          onlyChildren.push({
            ...child,
            id: Number(child.id),
          });
        });
      }
    });

    categories.value = onlyChildren;
  } catch (err) {
    console.error("Lỗi tải danh mục:", err);
    categories.value = [];
  }
};

const fetchBooks = async () => {
  try {
    loading.value = true;

    const res = await api.get("/books", {
      params: {
        page: page.value,
        size: size.value,
        sortBy: "id",
        direction: "desc",
      },
    });

    books.value = res.data.content;
    totalPages.value = res.data.totalPages;
    totalElements.value = res.data.totalElements;
  } catch (err) {
    console.error("Lỗi tải danh sách:", err);
    books.value = [];
  } finally {
    loading.value = false;
  }
};
const nextPage = () => {
  if (page.value < totalPages.value - 1) {
    page.value++;
  }
};

const prevPage = () => {
  if (page.value > 0) {
    page.value--;
  }
};
const goBackManagement = () => {
  router.push("/managements");
};
const goToPage = (p) => {
  if (p >= 0 && p < totalPages.value) {
    page.value = p;
  }
};

onMounted(() => {
  page.value = Number(route.query.page) || 0;
  console.log("books:", books.value);
  console.log("totalPages:", totalPages.value);

  fetchBooks();
  fetchCategories();
});

watch(page, (newPage) => {
  router.replace({
    query: { ...route.query, page: newPage },
  });

  fetchBooks(); // 🔥 fetch ở đây luôn
});

const onSelectImage = (event) => {
  const file = event.target.files?.[0];
  if (!file) return;

  if (previewUrl.value) URL.revokeObjectURL(previewUrl.value);

  form.coverImageUrl = `/books/${file.name}`;
  previewUrl.value = URL.createObjectURL(file);
};

const onSelectEditImage = (event) => {
  const file = event.target.files?.[0];
  if (!file) return;

  if (editPreviewUrl.value) URL.revokeObjectURL(editPreviewUrl.value);

  editForm.coverImageUrl = `/books/${file.name}`;
  editPreviewUrl.value = URL.createObjectURL(file);
};

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

const openAddPopup = () => {
  currentMode.value = "add";
  showPopup.value = true;
};

const openEditPopup = () => {
  currentMode.value = "edit";
  showPopup.value = true;
};

const activeSelected = computed(() =>
  currentMode.value === "add"
    ? selectedAddCategories.value
    : selectedEditCategories.value,
);

const toggleCategory = (id) => {
  const numericId = Number(id);
  const target =
    currentMode.value === "add"
      ? selectedAddCategories.value
      : selectedEditCategories.value;

  const index = target.indexOf(numericId);
  if (index !== -1) target.splice(index, 1);
  else target.push(numericId);
};

const submitForm = async () => {
  try {
    const normalizedIsbn = (form.isbn || "").replace(/-/g, "");

    await api.post("/books", {
      tieuDe: form.tieuDe,
      tacGia: form.tacGia,
      isbn: normalizedIsbn,
      gia: Number(form.gia),
      hangTon: Number(form.hangTon),
      moTa: form.moTa,
      coverImageUrl: form.coverImageUrl,
      categoryIds: selectedAddCategories.value,
    });

    resetAddForm(form);
    page.value = 0;
    await fetchBooks();
  } catch (err) {
    console.error("Lỗi thêm sách:", err);
  }
};

const startEdit = async (book) => {
  try {
    currentMode.value = "edit";

    const res = await api.get(`/books/${book.id}`);
    const detail = res.data;

    Object.assign(editForm, {
      id: detail.id,
      tieuDe: detail.tieuDe,
      tacGia: detail.tacGia,
      isbn: book.isbn || "",
      gia: detail.gia,
      hangTon: detail.hangTon,
      moTa: detail.moTa,
      coverImageUrl: detail.mainImage || "",
    });
    selectedEditCategories.value = detail.categories
      ? detail.categories.map((c) => Number(c.id))
      : [];
    if (editPreviewUrl.value) URL.revokeObjectURL(editPreviewUrl.value);
    editPreviewUrl.value = detail.mainImage || null;
  } catch (err) {
    console.error("Lỗi tải chi tiết:", err);
  }
};

const updateBook = async () => {
  if (!editForm.id) return;

  try {
    await api.put(`/books/${editForm.id}`, {
      tieuDe: editForm.tieuDe,
      tacGia: editForm.tacGia,
      isbn: editForm.isbn,
      gia: Number(editForm.gia),
      hangTon: Number(editForm.hangTon),
      moTa: editForm.moTa,
      coverImageUrl: editForm.coverImageUrl,
      categoryIds: selectedEditCategories.value,
    });

    resetEditForm(editForm);
    await fetchBooks();
  } catch (err) {
    console.error("Lỗi cập nhật:", err);
  }
};

const deleteBook = (id) => {
  deleteTargetId.value = id;
  showDeleteConfirm.value = true;
};
const confirmDeleteBook = async () => {
  try {
    await api.delete(`/books/${deleteTargetId.value}`);

    if (books.value.length === 1 && page.value > 0) {
      page.value--;
    }

    await fetchBooks();
  } catch (err) {
    console.error("Lỗi xoá:", err);
  } finally {
    showDeleteConfirm.value = false;
    deleteTargetId.value = null;
  }
};

const cancelDeleteBook = () => {
  showDeleteConfirm.value = false;
  deleteTargetId.value = null;
};
</script>

<style scoped>
.page {
  padding: 40px 5%;
  background: #eef4ff;
  min-height: 100vh;
}
h2 {
  text-align: center;
  font-size: 26px;
  font-weight: 700;
  color: #007bff;
  margin-bottom: 35px;
}

.form-wrapper {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 30px;
  margin-bottom: 40px;
}

.card {
  background: linear-gradient(145deg, #ffffff, #f4f9ff);
  border-radius: 20px;
  padding: 35px;
  border: 2px solid #007bff;
  box-shadow: 0 15px 35px rgba(0, 123, 255, 0.15);
}

.card h2 {
  text-align: center;
  font-size: 24px;
  color: #007bff;
  margin-bottom: 25px;
}

.side-tabs {
  background: white;
  padding: 25px 20px;
  border-radius: 18px;
  border: 2px solid #007bff;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.tab-item {
  padding: 14px;
  text-align: center;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  border: 2px solid #e5e7eb;
  transition: 0.25s;
}

.tab-item.active-add,
.tab-item.active-edit {
  background: linear-gradient(135deg, #007bff, #00c6ff);
  color: white;
  border: none;
}

.tab-item:hover {
  transform: translateY(-2px);
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #007bff;
  margin-bottom: 6px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

input,
textarea {
  width: 100%;
  padding: 14px 16px;
  border-radius: 12px;
  border: none;
  background: #f1f6ff;
  font-size: 14px;
  transition: 0.2s;
}

input:focus,
textarea:focus {
  background: white;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.2);
  outline: none;
}

textarea {
  min-height: 110px;
  resize: none;
}

.preview-box img {
  width: 180px;
  border-radius: 14px;
  margin-top: 12px;
}

.btn {
  width: 100%;
  padding: 14px;
  border-radius: 14px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: 0.25s;
}

.primary,
.warning {
  background: linear-gradient(135deg, #007bff, #00c6ff);
  color: white;
}

.primary:hover,
.warning:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(0, 123, 255, 0.35);
}

.small {
  width: auto;
  padding: 6px 14px;
}

.danger {
  background: crimson;
  color: white;
}

.danger:hover {
  background: #b3002d;
}

.btn-category {
  width: 100%;
  margin-top: 10px;
  padding: 12px;
  border-radius: 12px;
  border: 2px dashed #007bff;
  background: white;
  color: #007bff;
  font-weight: 600;
  cursor: pointer;
  transition: 0.25s;
}

.btn-category:hover {
  background: #007bff;
  color: white;
}

.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 12px 0 20px;
}

.tag.selected {
  background: white;
  border: 1px solid #007bff;
  padding: 6px 12px;
  border-radius: 20px;
}

.list-card {
  margin-top: 30px;
}

.book-item {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 20px;
  border-bottom: 1px solid #e6eefc;
  border-radius: 12px;
  transition: 0.25s;
}

.book-item:hover {
  background: #f1f6ff;
  transform: translateX(4px);
  box-shadow: 0 8px 20px rgba(0, 123, 255, 0.2);
}

.book-thumb {
  width: 90px;
  height: 130px;
  object-fit: cover;
  border-radius: 8px;
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
  margin-top: 8px;
  display: flex;
  gap: 12px;
  font-size: 14px;
}

.price {
  font-weight: 600;
  color: #007bff;
}

.stock {
  color: #666;
}

.actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

.popup-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.popup-wrapper {
  width: 900px;
  background: white;
  padding: 25px;
  border-radius: 20px;
  border: 2px solid #007bff;
  max-height: 80vh;
  overflow-y: auto;
}

.box-title {
  font-weight: 600;
  margin-bottom: 10px;
  color: #007bff;
}

.tag-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  min-height: 60px;
  padding: 12px;
  border: 1px dashed #cfe2ff;
  border-radius: 12px;
  background: #f8fbff;
  align-items: flex-start;
}

.tag-item {
  padding: 6px 12px;
  border: 1px solid #007bff;
  border-radius: 20px;
  cursor: pointer;
  transition: 0.2s;
}

.tag-item.active {
  background: #007bff;
  color: white;
}

.pagination {
  margin-top: 25px;
  display: flex;
  justify-content: center;
  gap: 8px;
}

.pagination button {
  padding: 6px 12px;
  border-radius: 10px;
  border: none;
  background: #f1f6ff;
  cursor: pointer;
  transition: 0.2s;
}

.pagination button.active {
  background: linear-gradient(135deg, #007bff, #00c6ff);
  color: white;
}

.pagination button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.delete-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  justify-content: center;
  align-items: center;
}

.delete-box {
  width: 380px;
  background: white;
  padding: 25px;
  border-radius: 16px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
}

.delete-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-cancel {
  background: #6c757d;
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
}

.btn-danger {
  background: crimson;
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
}
.page-header {
  position: relative;
  margin-bottom: 35px;
}

.page-header h2 {
  text-align: center;
  margin: 0;
}

.btn-back {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  padding: 10px 18px;
  border-radius: 14px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  background: linear-gradient(135deg, #007bff, #00c6ff);
  color: white;
  transition: 0.25s;
}

.btn-back:hover {
  transform: translateY(-50%) translateY(-2px);
  box-shadow: 0 10px 25px rgba(0, 123, 255, 0.35);
}
.popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.popup-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #007bff;
  margin: 0;
}
.selected-content {
  min-height: 60px;
  padding: 12px;
  border: 1px dashed #cfe2ff;
  border-radius: 12px;
  background: #f8fbff;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: flex-start;
}
</style>
