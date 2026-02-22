<template>
  <div class="page">
    <div class="page-header">
      <button class="btn-back" @click="goBackManagement">← Quay lại</button>
      <h2>Quản lý sản phẩm</h2>
    </div>
    <div class="form-wrapper">
      <!-- SIDE TABS -->
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

      <!-- TAB CONTENT -->
      <div class="tab-content">
        <!-- ADD STAFF -->
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

        <!-- SEARCH USER -->
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
            v-for="user in users.filter((u) => u.role !== 'ADMIN')"
            :key="user.id"
          >
            <td>{{ user.id }}</td>
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
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "@/services/api";
import { useRouter } from "vue-router";
const router = useRouter();

const staffErrors = ref({});
const staffServerError = ref("");
const validateStaff = () => {
  staffErrors.value = {};
  staffServerError.value = "";

  if (!staff.value.hoTen) {
    staffErrors.value.hoTen = "Họ tên không được để trống";
  }

  if (!staff.value.email) {
    staffErrors.value.email = "Email không được để trống";
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(staff.value.email)) {
    staffErrors.value.email = "Email không hợp lệ";
  }

  if (!staff.value.phone) {
    staffErrors.value.phone = "Số điện thoại không được để trống";
  }

  if (!staff.value.password) {
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
    alert("Tạo staff thành công");

    staff.value = {
      email: "",
      password: "",
      hoTen: "",
      phone: "",
    };

    fetchAllUsers();
  } catch (err) {
    staffServerError.value =
      err.response?.data?.message || "Tạo staff thất bại";
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
  padding: 40px 5%;
  background: #eef4ff;
}

h2 {
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  color: #007bff;
  margin-bottom: 30px;
}

.form-wrapper {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 30px;
  margin-bottom: 40px;
}

.card {
  background: white;
  border-radius: 20px;
  padding: 40px;
  border: none;
  box-shadow: 0 15px 35px rgba(0, 123, 255, 0.15);
}

.side-tabs {
  background: white;
  padding: 25px;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(0, 123, 255, 0.15);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.tab-item {
  padding: 14px;
  text-align: center;
  font-weight: 600;
  border-radius: 14px;
  cursor: pointer;
  background: #f1f6ff;
  transition: 0.2s;
}

.tab-item:hover {
  background: #e2edff;
}

.tab-item.active-tab {
  background: linear-gradient(135deg, #007bff, #00c6ff);
  color: white;
}

.form-group {
  margin-bottom: 22px;
}

.form-group label {
  display: block;
  font-weight: 600;
  color: #007bff;
  margin-bottom: 8px;
}
/* ===== PAGE HEADER ===== */
.page-header {
  position: relative;
  margin-top: 40px; /* thêm dòng này */
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

input,
select {
  width: 100%;
  padding: 14px 16px;
  border-radius: 14px;
  border: none;
  background: #f1f6ff;
  font-size: 14px;
  transition: 0.2s;
}

input:focus,
select:focus {
  outline: none;
  background: white;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.2);
}

.btn.primary {
  width: 100%;
  padding: 16px;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #007bff, #00c6ff);
  color: white;
  border: none;
  transition: 0.2s;
}

.btn.primary:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

.user-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 123, 255, 0.1);
}

.user-table thead {
  background: #f1f6ff;
}

.user-table th {
  padding: 16px;
  font-weight: 600;
  color: #007bff;
}

.user-table td {
  padding: 16px;
}

.user-table tr:hover {
  background: #f4f9ff;
}

.badge.active {
  background: #d4f4ff;
  color: #007bff;
}

.badge.locked {
  background: #ffe5e5;
  color: #dc2626;
}
</style>
