<script setup>
import { ref, onMounted, computed } from "vue";
import api from "@/services/api";
import { useRouter } from "vue-router";

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
  if (!form.value.tenDanhMuc) return;

  const payload = {
    tenDanhMuc: form.value.tenDanhMuc,
    danhMucCha: form.value.parentId ? { id: form.value.parentId } : null,
  };

  await api.post(API, payload);
  await fetchCategories();
  clearSelection();
};

const submitEdit = async () => {
  if (!selectedCategory.value) return;

  const payload = {
    tenDanhMuc: form.value.tenDanhMuc,
    danhMucCha: form.value.parentId ? { id: form.value.parentId } : null,
  };

  await api.put(`${API}/${form.value.id}`, payload);
  await fetchCategories();
  clearSelection();
};
const onlyRootCategories = rootCategories;

const deleteCategory = async () => {
  if (!selectedCategory.value) return;
  showConfirm.value = true;
};

const confirmDelete = async () => {
  await api.delete(`${API}/${selectedCategory.value.id}`);
  await fetchCategories();
  clearSelection();
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
  <button class="btn-back" @click="goBackManagement">
    ← Quay lại
  </button>
  <h2>Quản lý danh mục</h2>
</div>
    <div class="form-box">
      <input v-model="form.tenDanhMuc" placeholder="Tên danh mục" />

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
</template>

<style scoped>
.container {
  max-width: 1100px;
  margin: auto;
  padding: 40px;
  background: #eef4ff;
  border-radius: 24px;
  min-height: 100vh;
}

h2 {
  text-align: center;
  font-size: 26px;
  font-weight: 700;
  color: #007bff;
  margin-bottom: 35px;
}

.form-box {
  background: linear-gradient(145deg, #ffffff, #f4f9ff);
  padding: 35px;
  border-radius: 22px;
  margin-bottom: 50px;
  border: 2px solid #007bff;
  box-shadow: 0 15px 35px rgba(0, 123, 255, 0.15);
  transition: 0.25s;
}

.form-box:hover {
  transform: translateY(-6px);
}

input,
select {
  width: 100%;
  padding: 14px 16px;
  margin-bottom: 18px;
  border-radius: 12px;
  border: none;
  background: #f1f6ff;
  font-size: 14px;
  transition: 0.2s;
}

input:focus,
select:focus {
  background: white;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.2);
  outline: none;
}

.form-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

button {
  padding: 10px 20px;
  border-radius: 14px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: 0.25s;
  background: linear-gradient(135deg, #007bff, #00c6ff);
  color: white;
}

button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(0, 123, 255, 0.35);
}

button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-delete {
  background: crimson;
}

.btn-delete:hover:not(:disabled) {
  background: #b3002d;
  box-shadow: 0 10px 25px rgba(220, 20, 60, 0.4);
}

.form-actions button:not(.btn-add):not(.btn-edit):not(.btn-delete) {
  background: #6c757d;
}

.form-actions button:not(.btn-add):not(.btn-edit):not(.btn-delete):hover {
  background: #5a6268;
  box-shadow: none;
}

.list-box {
  background: white;
  padding: 35px;
  border-radius: 22px;
  border: 2px solid #007bff;
  box-shadow: 0 15px 35px rgba(0, 123, 255, 0.15);
}

.root-box {
  background: #ffffff;
  padding: 24px;
  border-radius: 18px;
  margin-bottom: 28px;
  border: 1px solid #e6eefc;
  transition: 0.25s;
}

.root-box:hover {
  background: #f4f9ff;
  transform: translateY(-4px);
}

.root-title {
  font-size: 18px;
  font-weight: 600;
  padding: 10px 14px;
  border-radius: 12px;
  cursor: pointer;
  transition: 0.2s;
}

.root-title:hover {
  background: #eaf2ff;
  transform: translateX(6px);
}

.child-tags {
  margin-top: 15px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.tag {
  padding: 8px 16px;
  border-radius: 999px;
  background: #f1f6ff;
  border: 1px solid #007bff;
  cursor: pointer;
  transition: 0.2s;
  font-size: 14px;
}

.tag:hover {
  background: #007bff;
  color: white;
  transform: scale(1.05);
}

.active {
  background: linear-gradient(135deg, #007bff, #00c6ff) !important;
  color: white !important;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.confirm-box {
  width: 420px;
  background: white;
  padding: 30px;
  border-radius: 20px;
  border: 2px solid #007bff;
  box-shadow: 0 25px 60px rgba(0, 123, 255, 0.25);
  animation: popIn 0.2s ease;
}

.confirm-box h4 {
  color: #007bff;
  margin-bottom: 10px;
}

.confirm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 25px;
}

.btn-cancel {
  background: #6c757d;
}

.btn-confirm {
  background: crimson;
}

.btn-confirm:hover {
  background: #b3002d;
}

@keyframes popIn {
  from {
    transform: scale(0.9);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
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
</style>