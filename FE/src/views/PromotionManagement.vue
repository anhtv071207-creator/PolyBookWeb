<template>
  <div class="container">
    <div class="page-header">
      <button class="btn-back" @click="goBackManagement">← Quay lại</button>
      <h2>Quản lý khuyến mãi</h2>
    </div>

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

            <button @click="openDeleteConfirm(p.id)">Xóa</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="showDeleteConfirm" class="delete-overlay">
  <div class="delete-box">
    <h3>Xác nhận xoá</h3>
    <p>Bạn chắc chắn muốn xoá khuyến mãi này?</p>

    <div class="delete-actions">
      <button class="btn-cancel" @click="cancelDelete">Hủy</button>
      <button class="btn-danger" @click="confirmDelete">Xóa</button>
    </div>
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

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router"; // Thêm dòng này
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";
const showToast = ref(false);
const toastMessage = ref("");
const toastType = ref("success");

const showToastMsg = (type, message) => {
  toastType.value = type;
  toastMessage.value = message;
  showToast.value = true;

  setTimeout(() => {
    showToast.value = false;
  }, 1800);
};
const theme = useThemeStore();
const showDeleteConfirm = ref(false);
const deleteTargetId = ref(null);
const router = useRouter(); // Khởi tạo router
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

// Chuyển logic tải dữ liệu vào một hàm để tái sử dụng
async function loadPromotions() {
  try {
    const res = await api.get("/promotions");
    promotions.value = res.data;
  } catch (error) {
    console.error("Lỗi khi tải danh sách khuyến mãi:", error);
  }
}

async function loadBooks() {
  try {
    const res = await api.get("/books", {
      params: { page: 0, size: 1000 },
    });
    books.value = res.data.content;
  } catch (error) {
    console.error("Lỗi khi tải danh sách sách:", error);
  }
}

onMounted(() => {
  loadPromotions();
  loadBooks();
});

const goBackManagement = () => {
  router.push("/managements");
};

async function savePromotion() {
  if (!form.value.bookId || !form.value.chietKhau) {
    showToastMsg("error", "Vui lòng nhập đầy đủ thông tin");
    return;
  }

  const payload = {
    bookId: form.value.bookId,
    chietKhau: form.value.chietKhau,
    batDau: form.value.batDau,
    ketThuc: form.value.ketThuc,
    active: true,
  };

  try {
    let res;

    if (editing.value) {
      await api.put(`/promotions/${form.value.id}`, payload);
      showToastMsg("success", "Cập nhật thành công");
    } else {
      await api.post("/promotions", payload);
      showToastMsg("success", "Thêm thành công");
    }

    await loadPromotions();
    resetForm();
  } catch (error) {
    const msg =
      error.response?.data?.message || error.response?.data || "Có lỗi xảy ra";

    showToastMsg("error", msg);
  }
}

function editPromotion(p) {
  editing.value = true;
  form.value = {
    id: p.id,
    bookId: p.book?.id || "",
    chietKhau: p.chietKhau,
    // Cắt chuỗi để phù hợp với định dạng của input datetime-local (YYYY-MM-DDTHH:mm)
    batDau: p.batDau ? p.batDau.slice(0, 16) : "",
    ketThuc: p.ketThuc ? p.ketThuc.slice(0, 16) : "",
  };
}

function openDeleteConfirm(id) {
  deleteTargetId.value = id;
  showDeleteConfirm.value = true;
}

function cancelDelete() {
  showDeleteConfirm.value = false;
  deleteTargetId.value = null;
}

async function confirmDelete() {
  try {
    await api.delete(`/promotions/${deleteTargetId.value}`);
    showToastMsg("success", "Xóa thành công");

    await loadPromotions();
  } catch (error) {
    const msg =
      error.response?.data?.message ||
      error.response?.data ||
      "Xóa thất bại";

    showToastMsg("error", msg);
  } finally {
    showDeleteConfirm.value = false;
    deleteTargetId.value = null;
  }
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

function formatDate(date) {
  if (!date) return "";
  return new Date(date).toLocaleString("vi-VN"); // Thêm locale tiếng Việt
}
</script>

<style scoped>
.container {
  max-width: 1000px;
  margin: auto;
  padding: 30px;
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
  background: #f1f1f1;
}

.form-box {
  background: white;
  border-radius: 10px;
  border: 1px solid #e2e2e2;
  padding: 24px;
  margin-bottom: 30px;

  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-box h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.form-box input,
.form-box select {
  padding: 10px 12px;
  border-radius: 6px;
  border: 1px solid #dcdcdc;
  font-size: 14px;
}

.form-box input:focus,
.form-box select:focus {
  outline: none;
  border-color: #888;
}

.form-box button {
  padding: 8px 16px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
  font-weight: 500;
  width: fit-content;
}

.form-box button:hover {
  background: #f2f2f2;
}

.promo-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border: 1px solid #e2e2e2;
  border-radius: 10px;
  overflow: hidden;
}

.promo-table th {
  text-align: left;
  font-weight: 600;
  font-size: 14px;
  padding: 14px;
  border-bottom: 1px solid #e2e2e2;
  background: #f5f5f5;
}

.promo-table td {
  padding: 14px;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}

.promo-table tbody tr:hover {
  background: #fafafa;
}

.promo-table button {
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
  margin-right: 6px;
}

.promo-table button:hover {
  background: #f2f2f2;
}

.promo-table button:last-child {
  border-color: #e74c3c;
  color: #e74c3c;
}

.promo-table button:last-child:hover {
  background: #fdeaea;
}
/* ===== DARK MODE ===== */

.dark .container {
  background: #0f172a;
}

/* header */

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

/* form */

.dark .form-box {
  background: #1e293b;
  border-color: #334155;
}

.dark .form-box h3 {
  color: #f1f5f9;
}

.dark .form-box input,
.dark .form-box select {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .form-box input:focus,
.dark .form-box select:focus {
  border-color: #64748b;
}

.dark .form-box button {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .form-box button:hover {
  background: #334155;
}

/* table */

.dark .promo-table {
  background: #1e293b;
  border-color: #334155;
}

.dark .promo-table th {
  background: #0f172a;
  border-bottom: 1px solid #334155;
  color: #e2e8f0;
}

.dark .promo-table td {
  border-bottom: 1px solid #334155;
  color: #e2e8f0;
}

.dark .promo-table tbody tr:hover {
  background: #334155;
}

/* buttons */

.dark .promo-table button {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .promo-table button:hover {
  background: #334155;
}

/* delete button */

.dark .promo-table button:last-child {
  border-color: #ef4444;
  color: #f87171;
}

.dark .promo-table button:last-child:hover {
  background: rgba(239, 68, 68, 0.15);
}
.toast-overlay {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
}

/* BOX */
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

/* ICON */
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

/* TEXT */
.toast-text {
  flex: 1;
  line-height: 1.4;
}

/* ===== LIGHT MODE ===== */
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

/* ===== DARK MODE ===== */
.dark .toast-box.success {
  background: #064e3b;
  color: #d1fae5;
}

.dark .toast-box.error {
  background: #7f1d1d;
  color: #fee2e2;
}

/* ===== ANIMATION ===== */
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
.delete-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
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

/* dark */
.dark .delete-box {
  background: #1e293b;
  color: #e2e8f0;
}

.dark .btn-cancel {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}
</style>
