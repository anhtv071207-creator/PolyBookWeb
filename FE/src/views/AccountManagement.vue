<template>
  <div>
    <h2>Quản Lý Người Dùng</h2>
    <div class="row g-2">
      <div class="col-6">
        <h4>Tạo tài khoản nhân viên</h4>
        <form @submit.prevent="createStaff" class="staff-form">
          <div>
            <input v-model="staff.email" placeholder="Email" />
          </div>
          <div>
            <input
              v-model="staff.password"
              type="password"
              placeholder="Password"
            />
          </div>
          <div>
            <input v-model="staff.hoTen" placeholder="Họ tên" />
          </div>
          <div>
            <input v-model="staff.phone" placeholder="Số điện thoại" />
          </div>
          <button type="submit">Tạo STAFF</button>
        </form>
      </div>
      <div class="col-6">
        <h4 class="form-title">Tìm kiếm & lọc tài khoản</h4>

        <form class="filter-box" @submit.prevent="onSearch">
          <div class="filter-row">
            <input
              v-model="filters.keyword"
              type="text"
              placeholder="Tìm theo họ tên / email / số điện thoại"
              class="search-input"
            />
          </div>

          <div class="filter-row two-cols">
            <select v-model="filters.role">
              <option value="">Tất cả role</option>
              <option value="USER">User</option>
              <option value="STAFF">Staff</option>
              <option value="ADMIN">Admin</option>
            </select>

            <select v-model="filters.status">
              <option value="">Tất cả trạng thái</option>
              <option value="active">Hoạt động</option>
              <option value="locked">Đã khóa</option>
            </select>
          </div>

          <div class="filter-row actions">
            <button type="submit" class="btn-secondary">Tìm kiếm</button>
            <button type="button" class="btn-secondary" @click="resetFilters">
              Reset
            </button>
          </div>
        </form>
      </div>
    </div>

    <hr />
    <div>
      <h4>Danh sách người dùng</h4>

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
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.hoTen }}</td>
            <td>{{ user.role }}</td>
            <td>
              {{ user.trangThai ? "Hoạt động" : "Đã khóa" }}
            </td>
            <td>
              <router-link :to="`/admin/users/${user.id}`">
                <button>Chi tiết</button>
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

const users = ref([]);

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
  await api.post("/account-management/admin/staff", staff.value);
  alert("Tạo staff thành công");

  staff.value = {
    email: "",
    password: "",
    hoTen: "",
    phone: "",
  };

  fetchAllUsers();
};

onMounted(() => {
  fetchAllUsers();
});
</script>

<style scoped>
h2 {
  margin: 16px 0;
  font-size: 22px;
  font-weight: 600;
}

hr {
  margin: 24px 0;
}

.staff-form {
  max-width: 100%;
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #fafafa;
}

.staff-form div {
  margin-bottom: 12px;
}

input {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  outline: none;
}

input:focus {
  border-color: #409eff;
}

button {
  padding: 8px 14px;
  border: none;
  border-radius: 4px;
  background: #409eff;
  color: white;
  cursor: pointer;
  font-weight: 500;
}

button:hover {
  background: #337ecc;
}

tr:hover {
  background: #f9f9f9;
}

td span {
  font-weight: 600;
}

td span.active {
  color: green;
}

td span.locked {
  color: red;
}
td button {
  background: #000000;
}

td button:hover {
  background: #c9c9cc;
}
.user-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 16px;
  background: white;
}

.user-table,
.user-table th,
.user-table td {
  border: 1px solid #000;
}

.user-table th,
.user-table td {
  padding: 5px;
  text-align: center;
}

.user-table th {
  background: #f5f7fa;
  font-weight: 600;
}
.filter-box {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #fafafa;
}

.filter-row {
  width: 100%;
}

.filter-row.two-cols {
  display: flex;
  gap: 10px;
}

.filter-row.two-cols select {
  flex: 1;
}

.search-input {
  width: 100%;
}

.filter-row.actions {
  display: flex;
  gap: 8px;
}
</style>
