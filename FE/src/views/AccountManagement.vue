<template>
  <div class="page">
    <div class="page-header">
      <button class="btn-back" @click="goBackManagement">← Quay lại</button>
      <h2>Quản lý người dùng</h2>
    </div>
    <div class="form-wrapper">
      <div class="side-tabs">
        <div
          class="tab-item"
          :class="{ 'active-tab': currentMode === 'add' }"
          @click="setMode('add')"
        >
          Thêm nhân viên
        </div>

        <div
          class="tab-item"
          :class="{ 'active-tab': currentMode === 'search' }"
          @click="setMode('search')"
        >
          Tìm tài khoản
        </div>
      </div>

      <div class="tab-content">
        <div v-if="currentMode === 'add'">
          <div class="card">
            <h2>Tạo tài khoản nhân viên</h2>
            <form @submit.prevent="createStaff">
              <div class="form-group">
                <label>Email</label>
                <input
                  v-model="staff.email"
                  :class="{ 'input-error': staffErrors.email }"
                />
                <p v-if="staffErrors.email" class="error-text">
                  {{ staffErrors.email }}
                </p>
              </div>

              <div class="form-group">
                <label>Mật khẩu</label>
                <input
                  type="password"
                  v-model="staff.password"
                  :class="{ 'input-error': staffErrors.password }"
                />
                <p v-if="staffErrors.password" class="error-text">
                  {{ staffErrors.password }}
                </p>
              </div>

              <div class="form-group">
                <label>Họ tên</label>
                <input
                  v-model="staff.hoTen"
                  :class="{ 'input-error': staffErrors.hoTen }"
                />
                <p v-if="staffErrors.hoTen" class="error-text">
                  {{ staffErrors.hoTen }}
                </p>
              </div>

              <div class="form-group">
                <label>Số điện thoại</label>
                <input
                  v-model="staff.phone"
                  :class="{ 'input-error': staffErrors.phone }"
                />
                <p v-if="staffErrors.phone" class="error-text">
                  {{ staffErrors.phone }}
                </p>
              </div>

              <button class="btn primary">Tạo STAFF</button>
            </form>
          </div>
        </div>

        <div v-if="currentMode === 'search'">
          <div class="card">
            <h2>Tìm kiếm & lọc tài khoản</h2>

            <form @submit.prevent="onSearch">
              <div class="form-group">
                <label>Từ khóa</label>
                <input
                  v-model="filters.keyword"
                  placeholder="Họ tên / Email / SĐT"
                />
              </div>

              <div class="form-group">
                <label>Role</label>
                <select v-model="filters.role">
                  <option value="">Tất cả</option>
                  <option value="USER">User</option>
                  <option value="STAFF">Staff</option>
                </select>
              </div>

              <div class="form-group">
                <label>Trạng thái</label>
                <select v-model="filters.status">
                  <option value="">Tất cả</option>
                  <option value="active">Hoạt động</option>
                  <option value="locked">Đã khóa</option>
                </select>
              </div>

              <button class="btn primary">Tìm kiếm</button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <div class="card list-card">
      <h2>Danh sách người dùng</h2>

      <table class="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Email</th>
            <th>Họ tên</th>
            <th>Role</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="(user, index) in users.filter((u) => u.role !== 'ADMIN')"
            :key="user.id"
          >
            <td>{{ index + 1 }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.hoTen }}</td>
            <td>{{ user.role }}</td>
            <td>
              <span :class="user.trangThai ? 'badge active' : 'badge locked'">
                {{ user.trangThai ? "Hoạt động" : "Đã khóa" }}
              </span>
            </td>
            <td>
              <router-link :to="`/admin/users/${user.id}`">
                <button class="btn small primary">Chi tiết</button>
              </router-link>
            </td>
          </tr>
        </tbody>
      </table>
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
import api from "@/services/api";
import { useRouter } from "vue-router";
const router = useRouter();
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();
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

const staffErrors = ref({});
const staffServerError = ref("");
const validateStaff = () => {
  staffErrors.value = {};
  staffServerError.value = "";

  const isEmpty = (val) => !val || !val.toString().trim();

  // ===== HỌ TÊN =====
  if (isEmpty(staff.value.hoTen)) {
    staffErrors.value.hoTen = "Họ tên không được để trống";
  } else if (staff.value.hoTen.trim().length < 3) {
    staffErrors.value.hoTen = "Họ tên phải ít nhất 3 ký tự";
  }

  // ===== EMAIL =====
  if (isEmpty(staff.value.email)) {
    staffErrors.value.email = "Email không được để trống";
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(staff.value.email.trim())) {
    staffErrors.value.email = "Email không hợp lệ";
  }

  // ===== SĐT =====
  if (isEmpty(staff.value.phone)) {
    staffErrors.value.phone = "Số điện thoại không được để trống";
  } else if (!/^\d{9,11}$/.test(staff.value.phone.trim())) {
    staffErrors.value.phone = "Số điện thoại phải từ 9-11 số";
  }

  // ===== PASSWORD =====
  if (isEmpty(staff.value.password)) {
    staffErrors.value.password = "Mật khẩu không được để trống";
  } else if (staff.value.password.length < 6) {
    staffErrors.value.password = "Mật khẩu phải ít nhất 6 ký tự";
  }

  return Object.keys(staffErrors.value).length === 0;
};
const users = ref([]);
const currentMode = ref("add");

const setMode = (mode) => {
  currentMode.value = mode;
};

const staff = ref({
  email: "",
  password: "",
  hoTen: "",
  phone: "",
});

const filters = ref({
  keyword: "",
  role: "",
  status: "",
});

const fetchAllUsers = async () => {
  const res = await api.get("/account-management/admin/users");
  users.value = res.data;
};

const searchUsers = async () => {
  const res = await api.get("/account-management/search", {
    params: {
      keyword: filters.value.keyword || null,
      role: filters.value.role || null,
      status: filters.value.status || null,
    },
  });
  users.value = res.data;
};

const onSearch = () => {
  const { keyword, role, status } = filters.value;

  if (!keyword && !role && !status) {
    fetchAllUsers();
  } else {
    searchUsers();
  }
};

const resetFilters = () => {
  filters.value = {
    keyword: "",
    role: "",
    status: "",
  };
  fetchAllUsers();
};

const createStaff = async () => {
  if (!validateStaff()) return;

  try {
    await api.post("/account-management/admin/staff", staff.value);

    showToastMsg("success", "Tạo staff thành công");

    staff.value = {
      email: "",
      password: "",
      hoTen: "",
      phone: "",
    };

    staffErrors.value = {}; 

    fetchAllUsers();
  } catch (err) {
    const msg = err.response?.data?.message || "Tạo staff thất bại";

    staffServerError.value = msg;
    showToastMsg("error", msg);
  }
};
const goBackManagement = () => {
  router.push("/managements");
};

onMounted(() => {
  fetchAllUsers();
});
</script>

<style scoped>
.page {
  padding: 32px 5%;
  background: #f6f7f9;
  min-height: 100vh;
}

/* header */

.page-header {
  position: relative;
  margin-bottom: 24px;
}

.page-header h2 {
  text-align: center;
  margin: 0;
  font-size: 22px;
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
  border: 1px solid #dcdcdc;
  background: white;
  cursor: pointer;
}

.btn-back:hover {
  background: #f2f2f2;
}

/* layout */

.form-wrapper {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 24px;
  margin-bottom: 30px;
}

/* card */

.card {
  background: white;
  border-radius: 8px;
  padding: 24px;
  border: 1px solid #e5e5e5;
}

.card h2 {
  font-size: 18px;
  margin-bottom: 16px;
  color: #333;
}

/* tabs */

.side-tabs {
  background: white;
  border-radius: 8px;
  border: 1px solid #e5e5e5;
  display: flex;
  flex-direction: column;
}

.tab-item {
  padding: 12px;
  text-align: center;
  cursor: pointer;
  font-weight: 500;
  border-bottom: 1px solid #eee;
}

.tab-item:hover {
  background: #f5f5f5;
}

.tab-item.active-tab {
  background: #333;
  color: white;
}

/* form */

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #444;
}

input,
select {
  width: 100%;
  padding: 10px 12px;
  border-radius: 6px;
  border: 1px solid #dcdcdc;
  font-size: 14px;
  background: white;
}

input:focus,
select:focus {
  outline: none;
  border-color: #888;
}

.input-error {
  border-color: #dc2626;
}

.error-text {
  font-size: 12px;
  color: #dc2626;
  margin-top: 4px;
}

/* buttons */

.btn.primary {
  width: 100%;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #333;
  background: #333;
  color: white;
  cursor: pointer;
}

.btn.primary:hover {
  opacity: 0.9;
}

.btn.small {
  padding: 6px 12px;
  font-size: 13px;
}

/* user table */

.list-card {
  margin-top: 20px;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

.user-table thead {
  background: #f4f4f4;
}

.user-table th {
  padding: 12px;
  font-size: 13px;
  font-weight: 600;
  color: #444;
  border-bottom: 1px solid #e5e5e5;
}

.user-table td {
  padding: 12px;
  font-size: 13px;
  border-bottom: 1px solid #f0f0f0;
}

.user-table tbody tr:hover {
  background: #fafafa;
}

/* status badge */

.badge {
  padding: 4px 10px;
  border-radius: 14px;
  font-size: 12px;
  font-weight: 500;
}

.badge.active {
  background: #e8f8ee;
  color: #15803d;
}

.badge.locked {
  background: #ffecec;
  color: #dc2626;
}
/* ===== DARK MODE ===== */

.dark .page {
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

/* cards */

.dark .card {
  background: #1e293b;
  border-color: #334155;
}

.dark .card h2 {
  color: #f1f5f9;
}

/* tabs */

.dark .side-tabs {
  background: #1e293b;
  border-color: #334155;
}

.dark .tab-item {
  border-bottom: 1px solid #334155;
  color: #e2e8f0;
}

.dark .tab-item:hover {
  background: #334155;
}

.dark .tab-item.active-tab {
  background: #475569;
  color: white;
}

/* form */

.dark .form-group label {
  color: #cbd5f5;
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

/* button */

.dark .btn.primary {
  background: #475569;
  border-color: #475569;
}

/* table */

.dark .user-table thead {
  background: #0f172a;
}

.dark .user-table th {
  color: #e2e8f0;
  border-bottom: 1px solid #334155;
}

.dark .user-table td {
  color: #e2e8f0;
  border-bottom: 1px solid #334155;
}

.dark .user-table tbody tr:hover {
  background: #334155;
}

/* badge */

.dark .badge.active {
  background: rgba(34, 197, 94, 0.2);
  color: #4ade80;
}

.dark .badge.locked {
  background: rgba(239, 68, 68, 0.2);
  color: #f87171;
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
</style>
