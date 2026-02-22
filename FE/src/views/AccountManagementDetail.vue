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
  padding: 40px 5%;
  background: #eef4ff;
  min-height: 100vh;
}

.card {
  background: white;
  border-radius: 20px;
  padding: 40px;
  border: none;
  box-shadow: 0 15px 35px rgba(0, 123, 255, 0.15);
}

h2 {
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  color: #007bff;
  margin-bottom: 40px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 25px;
}

.info-item {
  background: #f1f6ff;
  padding: 20px;
  border-radius: 16px;
  transition: 0.2s;
}

.info-item:hover {
  background: #e2edff;
}

.info-item span {
  display: block;
  font-size: 12px;
  font-weight: 700;
  color: #007bff;
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.info-item p {
  margin: 0;
  font-size: 15px;
  font-weight: 500;
}

.info-item.full {
  grid-column: span 2;
}

/* Badge */
.badge {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.badge.active {
  background: #d4f4ff;
  color: #007bff;
}

.badge.locked {
  background: #ffe5e5;
  color: #dc2626;
}

.badge.role {
  background: #e0f2ff;
  color: #007bff;
}

/* Buttons */
.action-row {
  margin-top: 40px;
  display: flex;
  gap: 16px;
  justify-content: center;
}

.btn {
  padding: 14px 24px;
  border-radius: 14px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: 0.2s;
}

.btn.danger {
  background: #dc2626;
  color: white;
}

.btn.danger:hover {
  background: #b91c1c;
}

.btn.secondary {
  background: linear-gradient(135deg, #007bff, #00c6ff);
  color: white;
}

.btn.secondary:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

.loading {
  text-align: center;
  padding: 60px;
  font-size: 18px;
}
</style>