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
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();

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
  padding: 30px 5%;
  background: #f7f7f7;
  min-height: 100vh;
}

.page-header {
  position: relative;
  margin-bottom: 25px;
}

.page-header h2 {
  text-align: center;
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.btn-back {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  padding: 8px 14px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
}

.btn-back:hover {
  background: #f2f2f2;
}

.form-wrapper {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 24px;
  margin-bottom: 35px;
}

.card {
  background: white;
  border-radius: 10px;
  padding: 24px;
  border: 1px solid #e2e2e2;
}

.card h2 {
  text-align: left;
  font-size: 20px;
  margin-bottom: 18px;
  color: #333;
}

.side-tabs {
  background: white;
  border-radius: 10px;
  border: 1px solid #e2e2e2;
  display: flex;
  flex-direction: column;
}

.tab-item {
  padding: 12px;
  text-align: center;
  cursor: pointer;
  border-bottom: 1px solid #eee;
  font-weight: 500;
}

.tab-item:hover {
  background: #f5f5f5;
}

.tab-item.active-add,
.tab-item.active-edit {
  background: #333;
  color: white;
}

.form-group {
  margin-bottom: 16px;
}

label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #555;
}

input,
textarea {
  width: 100%;
  padding: 10px 12px;
  border-radius: 6px;
  border: 1px solid #dcdcdc;
  font-size: 14px;
}

input:focus,
textarea:focus {
  outline: none;
  border-color: #888;
}

textarea {
  min-height: 90px;
}

.preview-box img {
  width: 140px;
  border-radius: 6px;
  margin-top: 10px;
}

.btn {
  width: 100%;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
  font-weight: 500;
}

.btn:hover {
  background: #f2f2f2;
}

.small {
  width: auto;
  padding: 6px 12px;
}

.btn-category {
  margin-top: 10px;
  border: 1px dashed #ccc;
  background: #fafafa;
}

.btn-category:hover {
  background: #f2f2f2;
}

.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin: 10px 0;
}

.tag.selected {
  background: #eee;
  border: 1px solid #ddd;
  padding: 5px 10px;
  border-radius: 14px;
  font-size: 12px;
}

.book-item {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.book-item:hover {
  background: #fafafa;
}

.book-thumb {
  width: 70px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.author {
  margin: 2px 0;
  font-size: 13px;
  color: #666;
}

.book-meta {
  margin-top: 6px;
  display: flex;
  gap: 10px;
  font-size: 13px;
}

.price {
  font-weight: 600;
}

.stock {
  color: #777;
}

.actions {
  margin-left: auto;
  display: flex;
  gap: 6px;
}

.popup-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 140px;
}

.popup-wrapper {
  width: 800px;
  background: white;
  padding: 20px;
  border-radius: 10px;
  max-height: 70vh;
  overflow-y: auto;
}

.popup-header {
  position: relative;
  background: white;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.tag-container,
.selected-content {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  border: 1px solid #eee;
  padding: 10px;
  border-radius: 6px;
  background: #fafafa;
}

.tag-item {
  padding: 5px 10px;
  border: 1px solid #ccc;
  border-radius: 14px;
  cursor: pointer;
  font-size: 12px;
}

.tag-item.active {
  background: #333;
  color: white;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 6px;
}

.pagination button {
  padding: 5px 10px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background: white;
}

.pagination button.active {
  background: #333;
  color: white;
}

.delete-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: center;
}

.delete-box {
  width: 360px;
  background: white;
  padding: 20px;
  border-radius: 10px;
}

.delete-actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-cancel {
  background: white;
  border: 1px solid #ccc;
}

.btn-danger {
  background: #e74c3c;
  color: white;
  border: none;
}
/* ===== DARK MODE ===== */

.dark .page {
  background: #0f172a;
}

.dark .page-header h2 {
  color: #f1f5f9;
}

.dark .btn-back {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .btn-back:hover {
  background: #334155;
}

/* card */

.dark .card {
  background: #1e293b;
  border-color: #334155;
}

.dark .card h2 {
  color: #f1f5f9;
}

/* side tabs */

.dark .side-tabs {
  background: #1e293b;
  border-color: #334155;
}

.dark .tab-item {
  color: #e2e8f0;
  border-bottom: 1px solid #334155;
}

.dark .tab-item:hover {
  background: #334155;
}

.dark .tab-item.active-add,
.dark .tab-item.active-edit {
  background: #475569;
  color: white;
}

/* form */

.dark label {
  color: #cbd5f5;
}

.dark input,
.dark textarea {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark input:focus,
.dark textarea:focus {
  border-color: #64748b;
}

/* button */

.dark .btn {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .btn:hover {
  background: #334155;
}

/* category tags */

.dark .tag.selected {
  background: #334155;
  border-color: #475569;
  color: #e2e8f0;
}

/* book list */

.dark .book-item {
  border-bottom: 1px solid #334155;
}

.dark .book-item:hover {
  background: #1e293b;
}

.dark .title {
  color: #f1f5f9;
}

.dark .author {
  color: #94a3b8;
}

.dark .stock {
  color: #94a3b8;
}

/* popup */

.dark .popup-wrapper {
  background: #1e293b;
  color: #e2e8f0;
}

.dark .popup-header {
  background: #1e293b;
  border-bottom: 1px solid #334155;
}

.dark .tag-container,
.dark .selected-content {
  background: #0f172a;
  border-color: #334155;
}

.dark .tag-item {
  border-color: #475569;
  color: #e2e8f0;
}

.dark .tag-item.active {
  background: #475569;
}

/* pagination */

.dark .pagination button {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .pagination button.active {
  background: #475569;
}

/* delete confirm */

.dark .delete-box {
  background: #1e293b;
  color: #e2e8f0;
}

.dark .btn-cancel {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}
/* dark mode button category */

.dark .btn-category {
  background: #1e293b;
  border: 1px dashed #475569;
  color: #e2e8f0;
}

.dark .btn-category:hover {
  background: #334155;
}
</style>
