<template>
  <div v-if="user">
    <h2>Chi tiết người dùng</h2>

    <h3>Thông tin tài khoản</h3>
    <p><b>Email:</b> {{ user.email }}</p>
    <p><b>Họ tên:</b> {{ user.hoTen }}</p>
    <p><b>Số điện thoại:</b> {{ user.phone }}</p>
    <p><b>Role:</b> {{ user.role }}</p>
    <p><b>Trạng thái:</b> {{ user.trangThai ? "Hoạt động" : "Đã khóa" }}</p>
    <p><b>Ngày tạo:</b> {{ formatDate(user.ngayTao) }}</p>
    <p><b>Quốc gia:</b> {{ user.quocGia }}</p>
    <p><b>Tỉnh / Thành:</b> {{ user.tinhThanh }}</p>
    <p><b>Quận / Huyện:</b> {{ user.quanHuyen }}</p>
    <p><b>Phường / Xã:</b> {{ user.phuongXa }}</p>
    <p><b>Địa chỉ:</b> {{ user.diaChi }}</p>
    <div style="display: flex; gap: 12px">
      <button v-if="user.role !== 'ROLE_ADMIN'" @click="toggleStatus">
        {{ user.trangThai ? "Khóa tài khoản" : "Mở khóa" }}
      </button>
      <button @click="$router.back()">Quay lại</button>
    </div>
  </div>

  <div v-else>Đang tải dữ liệu...</div>
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
