<script setup>
import { ref, onMounted, computed } from "vue";
import api from "@/services/api";
import { useRouter } from "vue-router";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();
const errors = ref({});
const showToast = ref(false);
const toastMessage = ref("");
const toastType = ref("success"); // success | error
const triggerToast = (message, type = "success") => {
  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;

  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

const router = useRouter();

const API = "/categories";

const categories = ref([]);
const selectedCategory = ref(null);
const showConfirm = ref(false);

const form = ref({
  id: null,
  tenDanhMuc: "",
  parentId: null,
});
const goBackManagement = () => {
  router.push("/managements");
};

const rootCategories = computed(() => {
  const childIds = new Set();

  categories.value.forEach((cat) => {
    cat.danhMucCon?.forEach((child) => {
      childIds.add(child.id);
    });
  });

  return categories.value.filter((cat) => !childIds.has(cat.id));
});

const sortTree = (data) => {
  return data
    .sort((a, b) => a.id - b.id)
    .map((item) => ({
      ...item,
      danhMucCon: item.danhMucCon ? sortTree(item.danhMucCon) : [],
    }));
};

const fetchCategories = async () => {
  const res = await api.get(API);
  categories.value = sortTree(res.data);
};

onMounted(fetchCategories);

const selectCategory = (cat, parentId = null) => {
  selectedCategory.value = cat;

  form.value = {
    id: cat.id,
    tenDanhMuc: cat.tenDanhMuc,
    parentId: parentId,
  };
};

const clearSelection = () => {
  selectedCategory.value = null;
  form.value = {
    id: null,
    tenDanhMuc: "",
    parentId: null,
  };
};

const isAddDisabled = computed(() => selectedCategory.value !== null);
const isEditDisabled = computed(() => selectedCategory.value === null);
const isDeleteDisabled = computed(() => selectedCategory.value === null);
const submitAdd = async () => {
  errors.value = {};

  if (!form.value.tenDanhMuc || !form.value.tenDanhMuc.trim()) {
    errors.value.tenDanhMuc = "Tên danh mục không được để trống";
    return;
  }

  const payload = {
    tenDanhMuc: form.value.tenDanhMuc.trim(),
    danhMucCha: form.value.parentId ? { id: form.value.parentId } : null,
  };

  try {
    await api.post(API, payload);
    await fetchCategories();
    clearSelection();

    triggerToast("Thêm danh mục thành công");
  } catch (e) {
    triggerToast("Thêm danh mục thất bại", "error");
  }
};
const submitEdit = async () => {
  errors.value = {};

  if (!form.value.tenDanhMuc || !form.value.tenDanhMuc.trim()) {
    errors.value.tenDanhMuc = "Tên danh mục không được để trống";
    return;
  }

  const payload = {
    tenDanhMuc: form.value.tenDanhMuc.trim(),
    danhMucCha: form.value.parentId ? { id: form.value.parentId } : null,
  };

  try {
    await api.put(`${API}/${form.value.id}`, payload);
    await fetchCategories();
    clearSelection();

    triggerToast("Cập nhật danh mục thành công");
  } catch (e) {
    triggerToast("Cập nhật danh mục thất bại", "error");
  }
};
const onlyRootCategories = rootCategories;

const deleteCategory = async () => {
  if (!selectedCategory.value) return;
  showConfirm.value = true;
};
const confirmDelete = async () => {
  try {
    await api.delete(`${API}/${selectedCategory.value.id}`);
    await fetchCategories();
    clearSelection();

    triggerToast("Xóa danh mục thành công");
  } catch (e) {
    triggerToast("Xóa thất bại", "error");
  }

  showConfirm.value = false;
};

const cancelDelete = () => {
  showConfirm.value = false;
};

// const getAllCategories = (data, parentId = null) => {
//   let result = [];

//   data.forEach((cat) => {
//     result.push({
//       id: cat.id,
//       tenDanhMuc: cat.tenDanhMuc,
//       parentId: parentId,
//     });

//     if (cat.danhMucCon && cat.danhMucCon.length > 0) {
//       result = result.concat(getAllCategories(cat.danhMucCon, cat.id));
//     }
//   });

//   return result;
// };
</script>

<template>
  <div class="container">
    <div class="page-header">
      <button class="btn-back" @click="goBackManagement">← Quay lại</button>
      <h2>Quản lý danh mục</h2>
    </div>
    <div class="form-box">
      <input
        v-model="form.tenDanhMuc"
        placeholder="Tên danh mục"
        :class="{ 'input-error': errors.tenDanhMuc }"
      />

      <p v-if="errors.tenDanhMuc" class="error-text">
        {{ errors.tenDanhMuc }}
      </p>
      <select v-model="form.parentId">
        <option :value="null">-- Không có danh mục cha --</option>
        <option
          v-for="cat in onlyRootCategories"
          :key="cat.id"
          :value="cat.id"
          :disabled="cat.id === form.id"
        >
          {{ cat.tenDanhMuc }}
        </option>
      </select>

      <div class="form-actions">
        <button class="btn-add" @click="submitAdd" :disabled="isAddDisabled">
          Thêm
        </button>

        <button class="btn-edit" @click="submitEdit" :disabled="isEditDisabled">
          Sửa
        </button>

        <button
          class="btn-delete"
          @click="deleteCategory"
          :disabled="isDeleteDisabled"
        >
          Xóa
        </button>

        <button @click="clearSelection">Bỏ chọn</button>
      </div>
    </div>
    <div class="list-box">
      <div v-for="cat in rootCategories" :key="cat.id" class="root-box">
        <div
          class="root-title"
          :class="{ active: selectedCategory?.id === cat.id }"
          @click="selectCategory(cat, null)"
        >
          {{ cat.tenDanhMuc }}
        </div>

        <div class="child-tags">
          <div
            v-for="child in cat.danhMucCon"
            :key="child.id"
            class="tag"
            :class="{ active: selectedCategory?.id === child.id }"
            @click="selectCategory(child, cat.id)"
          >
            {{ child.tenDanhMuc }}
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-if="showConfirm" class="modal-overlay">
    <div class="confirm-box">
      <h4>Xác nhận xóa</h4>
      <p>
        Bạn có chắc muốn xóa danh mục
        <strong>{{ selectedCategory?.tenDanhMuc }}</strong>
        không?
      </p>

      <div class="confirm-actions">
        <button class="btn-cancel" @click="cancelDelete">Hủy</button>
        <button class="btn-confirm" @click="confirmDelete">Xóa</button>
      </div>
    </div>
  </div>
  <div v-if="showToast" class="toast-overlay">
    <div class="toast-box" :class="toastType">
      <div class="toast-icon">
        <span v-if="toastType === 'success'">✔</span>
        <span v-else>✕</span>
      </div>
      <div class="toast-text">{{ toastMessage }}</div>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 1000px;
  margin: auto;
  padding: 30px;
  background: #f7f7f7;
  border-radius: 12px;
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
  background: #f1f1f1;
}

.form-box {
  background: white;
  padding: 25px;
  border-radius: 10px;
  margin-bottom: 30px;
  border: 1px solid #e2e2e2;
}

input,
select {
  width: 100%;
  padding: 10px 12px;
  margin-bottom: 14px;
  border-radius: 6px;
  border: 1px solid #dcdcdc;
  font-size: 14px;
}

input:focus,
select:focus {
  outline: none;
  border-color: #888;
}

.form-actions {
  display: flex;
  gap: 10px;
}

button {
  padding: 8px 16px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
  font-weight: 500;
}

button:hover:not(:disabled) {
  background: #f2f2f2;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-delete {
  border-color: #e74c3c;
  color: #e74c3c;
}

.btn-delete:hover:not(:disabled) {
  background: #fdeaea;
}

.list-box {
  background: white;
  padding: 25px;
  border-radius: 10px;
  border: 1px solid #e2e2e2;
}

.root-box {
  margin-bottom: 18px;
}

.root-title {
  font-size: 16px;
  font-weight: 600;
  padding: 8px 10px;
  border-radius: 6px;
  cursor: pointer;
}

.root-title:hover {
  background: #f5f5f5;
}

.child-tags {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 6px 12px;
  border-radius: 20px;
  background: #f3f3f3;
  border: 1px solid #ddd;
  cursor: pointer;
  font-size: 13px;
}

.tag:hover {
  background: #e9e9e9;
}

.active {
  background: #333;
  color: white;
  border-color: #333;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
}

.confirm-box {
  width: 360px;
  background: white;
  padding: 24px;
  border-radius: 10px;
}

.confirm-box h4 {
  margin-bottom: 10px;
}

.confirm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn-cancel {
  background: white;
}

.btn-confirm {
  background: #e74c3c;
  color: white;
  border: none;
}
/* ===== DARK MODE ===== */

.dark .container {
  background: #0f172a;
}

.dark .page-header h2 {
  color: #f1f5f9;
}

/* back button */

.dark .btn-back {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .btn-back:hover {
  background: #334155;
}

/* form box */

.dark .form-box {
  background: #1e293b;
  border-color: #334155;
}

.dark input,
.dark select {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark input:focus,
.dark select:focus {
  border-color: #64748b;
}

/* buttons */

.dark button {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark button:hover:not(:disabled) {
  background: #334155;
}

/* delete button */

.dark .btn-delete {
  border-color: #ef4444;
  color: #ef4444;
}

.dark .btn-delete:hover:not(:disabled) {
  background: rgba(239, 68, 68, 0.15);
}

/* list */

.dark .list-box {
  background: #1e293b;
  border-color: #334155;
}

.dark .root-title {
  color: #e2e8f0;
}

.dark .root-title:hover {
  background: #334155;
}

/* child tags */

.dark .tag {
  background: #334155;
  border-color: #475569;
  color: #e2e8f0;
}

.dark .tag:hover {
  background: #475569;
}

/* active tag */

.dark .active {
  background: #64748b;
  border-color: #64748b;
  color: white;
}

/* modal */

.dark .confirm-box {
  background: #1e293b;
  color: #e2e8f0;
}

.dark .btn-cancel {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}
.toast-overlay {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
}

.toast-box {
  min-width: 300px;
  max-width: 380px;
  padding: 14px 16px;
  border-radius: 12px;

  display: flex;
  align-items: center;
  gap: 12px;

  font-size: 14px;
  font-weight: 500;

  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);

  animation: slideIn 0.3s ease;
}

.toast-icon {
  width: 38px;
  height: 38px;
  border-radius: 50%;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;
  font-size: 16px;
  font-weight: bold;
}
.toast-box.success {
  background: #ecfdf5;
  color: #065f46;
}
.toast-box.success .toast-icon {
  background: #10b981;
  color: white;
}
.toast-box.error {
  background: #fef2f2;
  color: #7f1d1d;
}
.toast-box.error .toast-icon {
  background: #ef4444;
  color: white;
}
.dark .toast-box.success {
  background: #064e3b;
  color: #d1fae5;
}

.dark .toast-box.error {
  background: #7f1d1d;
  color: #fee2e2;
}
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}
.input-error {
  border-color: #ff4d4f !important;
  background-color: #fff1f0;
}

.error-text {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 4px;
}
</style>
