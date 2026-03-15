<template>
  <div class="page" v-if="user">
    <div class="card">
      <h2>Chi tiết người dùng</h2>

      <div class="info-grid">
        <div class="info-item">
          <span>Email</span>
          <p>{{ user.email }}</p>
        </div>

        <div class="info-item">
          <span>Họ tên</span>
          <p>{{ user.hoTen }}</p>
        </div>

        <div class="info-item">
          <span>Số điện thoại</span>
          <p>{{ user.phone }}</p>
        </div>

        <div class="info-item">
          <span>Role</span>
          <p class="badge role">{{ user.role }}</p>
        </div>

        <div class="info-item">
          <span>Trạng thái</span>
          <p :class="user.trangThai ? 'badge active' : 'badge locked'">
            {{ user.trangThai ? "Hoạt động" : "Đã khóa" }}
          </p>
        </div>

        <div class="info-item">
          <span>Ngày tạo</span>
          <p>{{ formatDate(user.ngayTao) }}</p>
        </div>

        <div class="info-item">
          <span>Quốc gia</span>
          <p>{{ user.quocGia }}</p>
        </div>

        <div class="info-item">
          <span>Tỉnh / Thành</span>
          <p>{{ user.tinhThanh }}</p>
        </div>

        <div class="info-item">
          <span>Quận / Huyện</span>
          <p>{{ user.quanHuyen }}</p>
        </div>

        <div class="info-item">
          <span>Phường / Xã</span>
          <p>{{ user.phuongXa }}</p>
        </div>

        <div class="info-item full">
          <span>Địa chỉ</span>
          <p>{{ user.diaChi }}</p>
        </div>
      </div>

      <div class="action-row">
        <button
          v-if="user.role !== 'ROLE_ADMIN'"
          class="btn danger"
          @click="toggleStatus"
        >
          {{ user.trangThai ? "Khóa tài khoản" : "Mở khóa" }}
        </button>

        <button class="btn secondary" @click="$router.back()">
          Quay lại
        </button>
      </div>
    </div>
  </div>

  <div class="loading" v-else>Đang tải dữ liệu...</div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();

const route = useRoute();
const user = ref(null);

const fetchUserDetail = async () => {
  const res = await api.get(
    `/account-management/admin/users/${route.params.id}/detail`,
  );
  user.value = res.data;
};

const formatDate = (date) => {
  if (!date) return "";
  return new Date(date).toLocaleString();
};

onMounted(() => {
  fetchUserDetail();
});
const toggleStatus = async () => {
  await api.put(
    `/account-management/admin/users/${user.value.id}/status`,
    {
      trangThai: !user.value.trangThai,
    }
  );

  user.value.trangThai = !user.value.trangThai;
};

</script>

<style scoped>
.page {
  padding: 32px 5%;
  background: #f6f7f9;
  min-height: 100vh;
}

/* card */

.card {
  background: white;
  border-radius: 8px;
  padding: 28px;
  border: 1px solid #e5e5e5;
  max-width: 900px;
  margin: auto;
}

h2 {
  text-align: center;
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin-bottom: 30px;
}

/* info grid */

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

.info-item {
  border: 1px solid #eee;
  border-radius: 6px;
  padding: 14px;
  background: #fafafa;
}

.info-item span {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #666;
  margin-bottom: 4px;
}

.info-item p {
  margin: 0;
  font-size: 14px;
  color: #333;
}

.info-item.full {
  grid-column: span 2;
}

/* badge */

.badge {
  display: inline-block;
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

.badge.role {
  background: #f1f1f1;
  color: #444;
}

/* actions */

.action-row {
  margin-top: 28px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border-radius: 6px;
  border: 1px solid #ccc;
  font-size: 13px;
  cursor: pointer;
  background: white;
}

.btn:hover {
  background: #f2f2f2;
}

.btn.danger {
  background: #dc2626;
  color: white;
  border: none;
}

.btn.danger:hover {
  background: #b91c1c;
}

.btn.secondary {
  background: #333;
  color: white;
  border: none;
}

.btn.secondary:hover {
  opacity: 0.9;
}

/* loading */

.loading {
  text-align: center;
  padding: 60px;
  font-size: 15px;
  color: #666;
}
.dark .page {
  background: #0f172a;
}

.dark .card {
  background: #1e293b;
  border-color: #334155;
}

.dark .info-item {
  background: #0f172a;
  border-color: #334155;
}

.dark .info-item span {
  color: #94a3b8;
}

.dark .info-item p {
  color: #e2e8f0;
}
</style>