<template>
  <div class="profile-wrapper">
    <div class="profile-card">
      <h2>Thông tin cá nhân</h2>

      <form class="profile-form" @submit.prevent="updateProfile">
        <div class="form-group">
          <label>Họ tên</label>
          <input type="text" v-model="form.hoTen" class="form-control" />
        </div>

        <div class="form-group">
          <label>Số điện thoại</label>
          <input type="text" v-model="form.phone" class="form-control" />
        </div>

        <div class="form-group">
          <label>Quốc gia</label>
          <select class="form-select">
            <option>Việt Nam</option>
          </select>
        </div>

        <div class="form-group">
          <label>Tỉnh / Thành</label>
          <select
            class="form-select"
            v-model="selectedProvince"
            @change="onProvinceChange"
          >
            <option :value="null" disabled hidden>
              -- Chọn tỉnh / thành --
            </option>

            <option v-for="p in provinces" :key="p.code" :value="p">
              {{ p.name }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>Quận / Huyện</label>
          <select
            class="form-select"
            v-model="selectedDistrict"
            @change="onDistrictChange"
            :disabled="!districts.length"
          >
            <option :value="null">-- Chọn quận / huyện --</option>

            <option v-for="d in districts" :key="d.code" :value="d">
              {{ d.name }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>Phường / Xã</label>
          <select
            class="form-select"
            v-model="selectedWard"
            :disabled="!wards.length"
          >
            <option :value="null">-- Chọn phường / xã --</option>

            <option v-for="w in wards" :key="w.code" :value="w">
              {{ w.name }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>Địa chỉ chi tiết</label>
          <input type="text" v-model="form.diaChi" class="form-control" />
        </div>

        <button class="btn-save" :disabled="loading">
          {{ loading ? "Đang lưu..." : "Lưu thay đổi" }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "@/services/api";
import { useAuthStore } from "@/stores/auth";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();

const authStore = useAuthStore();
const loading = ref(false);

const form = ref({
  hoTen: "",
  phone: "",
  quocGia: "",
  tinhThanh: "",
  quanHuyen: "",
  phuongXa: "",
  diaChi: "",
});

const loadProfile = async () => {
  try {
    const userId = authStore.user.id;
    const res = await api.get(`/users/${userId}/info`);

    const data = res.data;
    form.value.hoTen = data.hoTenNguoiNhan;
    form.value.phone = data.phone;
    form.value.quocGia = data.quocGia;
    form.value.tinhThanh = data.tinhThanh;
    form.value.quanHuyen = data.quanHuyen;
    form.value.phuongXa = data.phuongXa;
    form.value.diaChi = data.diaChi;
    await mapAddressToSelect({
      tinhThanh: data.tinhThanh,
      quanHuyen: data.quanHuyen,
      phuongXa: data.phuongXa,
    });
  } catch (err) {
    console.error(err);
    alert("Không tải được thông tin người dùng");
  }
};

const provinces = ref([]);
const districts = ref([]);
const wards = ref([]);

const selectedProvince = ref(null);
const selectedDistrict = ref(null);
const selectedWard = ref(null);

const loadProvinces = async () => {
  const res = await fetch("https://provinces.open-api.vn/api/p/");
  provinces.value = await res.json();
};

const updateProfile = async () => {
  loading.value = true;
  try {
    await api.put("/users/profile", {
      hoTen: form.value.hoTen,
      phone: form.value.phone,
      quocGia: form.value.quocGia,
      tinhThanh: form.value.tinhThanh,
      quanHuyen: form.value.quanHuyen,
      phuongXa: form.value.phuongXa,
      diaChi: form.value.diaChi,
    });

    await loadProfile();
    alert("Cập nhật thành công");
  } catch (err) {
    console.error(err);
    alert("Cập nhật thất bại");
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  await loadProvinces();
  await loadProfile();
});

const onProvinceChange = async () => {
  districts.value = [];
  wards.value = [];
  selectedDistrict.value = null;
  selectedWard.value = null;

  if (!selectedProvince.value) return;

  form.value.tinhThanh = selectedProvince.value.name;

  const res = await fetch(
    `https://provinces.open-api.vn/api/p/${selectedProvince.value.code}?depth=2`,
  );
  const data = await res.json();
  districts.value = data.districts;
};

const onDistrictChange = async () => {
  wards.value = [];
  selectedWard.value = null;

  if (!selectedDistrict.value) return;

  form.value.quanHuyen = selectedDistrict.value.name;

  const res = await fetch(
    `https://provinces.open-api.vn/api/d/${selectedDistrict.value.code}?depth=2`,
  );
  const data = await res.json();
  wards.value = data.wards;
};

import { watch } from "vue";

watch(selectedWard, (v) => {
  if (v) form.value.phuongXa = v.name;
});
const mapAddressToSelect = async (data) => {
  selectedProvince.value = provinces.value.find(
    (p) => p.name === data.tinhThanh,
  );

  if (selectedProvince.value) {
    await onProvinceChange();
    selectedDistrict.value = districts.value.find(
      (d) => d.name === data.quanHuyen,
    );
  }

  if (selectedDistrict.value) {
    await onDistrictChange();
    selectedWard.value = wards.value.find((w) => w.name === data.phuongXa);
  }
};
</script>

<style scoped>
.profile-wrapper {
  min-height: 100vh;
  background: #f4f6f8;
  display: flex;
  justify-content: center;
  padding: 40px 16px;
}

.profile-card {
  width: 100%;
  max-width: 560px;
  background: #fff;
  padding: 28px 32px;
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}

.profile-card h2 {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 24px;
  color: #222;
  text-align: left;
}
.profile-form label {
  display: block;
  text-align: left;
  margin-bottom: 4px;
}

.label-form label {
  font-weight: 600;
  font-size: 14px;
  color: #444;
}
.form-control,
.form-select {
  height: 44px;
  padding: 0 14px;
  font-size: 15px;
  border-radius: 8px;
  border: 1px solid #cfcfcf;
  background-color: #fff;
  box-shadow: none;
}

.form-select {
  appearance: none;
  -webkit-appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg width='12' height='12' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M6 9L12 15L18 9' stroke='%23666' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
  background-size: 14px;
}

.form-control:focus,
.form-select:focus {
  border-color: #1677ff;
  box-shadow: 0 0 0 3px rgba(22, 119, 255, 0.15);
  outline: none;
}

.row {
  margin-bottom: 14px;
}

.btn-save {
  width: 100%;
  height: 42px;
  margin-top: 18px;
  background: #1890ff;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  font-size: 15px;
  transition:
    background 0.2s ease,
    transform 0.1s ease;
}

.btn-save:hover:not(:disabled) {
  background: #1677cc;
  transform: translateY(-1px);
}

.btn-save:disabled {
  background: #bfbfbf;
  cursor: not-allowed;
}

@media (max-width: 576px) {
  .profile-card {
    padding: 20px;
  }

  .label-form {
    margin-bottom: 4px;
  }

  .row {
    margin-bottom: 16px;
  }

  .label-form,
  .col-9 {
    flex: 0 0 100%;
    max-width: 100%;
  }
}
.profile-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.form-control,
.form-select {
  width: 100%;
}

.form-group {
  display: grid;
  grid-template-columns: 140px 1fr;
  align-items: center;
  gap: 10px;
}
.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  text-align: left;
}
/* ===== DARK MODE PROFILE ===== */

.dark .profile-wrapper {
  background: #0f172a;
}

.dark .profile-card {
  background: #1e293b;
  box-shadow: none;
  color: #e2e8f0;
}

/* title */

.dark .profile-card h2 {
  color: #f1f5f9;
}

/* label */

.dark .form-group label {
  color: #cbd5f5;
}

/* input + select */

.dark .form-control,
.dark .form-select {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}

.dark .form-control::placeholder {
  color: #94a3b8;
}

.dark .form-control:focus,
.dark .form-select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.25);
}

/* select arrow */

.dark .form-select {
  background-image: url("data:image/svg+xml,%3Csvg width='12' height='12' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M6 9L12 15L18 9' stroke='%23cbd5f5' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
  background-size: 14px;
}

/* button */

.dark .btn-save {
  background: #2563eb;
}

.dark .btn-save:hover:not(:disabled) {
  background: #1d4ed8;
}

.dark .btn-save:disabled {
  background: #475569;
}
</style>
