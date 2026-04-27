<template>
  <div class="profile-wrapper">
    <div class="profile-card">
      <!-- TAB -->
      <div class="side-tabs">
        <div
          class="tab-item"
          :class="{ active: activeTab === 'profile' }"
          @click="activeTab = 'profile'"
        >
          Thông tin
        </div>

        <div
          class="tab-item"
          :class="{ active: activeTab === 'password' }"
          @click="activeTab = 'password'"
        >
          Đổi mật khẩu
        </div>
      </div>

      <!-- PROFILE TAB -->
      <div class="tab-content">
        <div v-if="activeTab === 'profile'">
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
              <select class="form-select" v-model="form.quocGia">
                <option value="Việt Nam">Việt Nam</option>
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

        <!-- PASSWORD TAB -->
        <div v-else>
          <h2>Đổi mật khẩu</h2>

          <form class="profile-form" @submit.prevent="changePassword">
            <div class="form-group password-group">
              <label>Mật khẩu cũ</label>

              <div class="password-wrapper">
                <input
                  :type="showOldPassword ? 'text' : 'password'"
                  v-model="passwordForm.oldPassword"
                  class="form-control"
                />

                <button
                  type="button"
                  class="btn-eye"
                  @mousedown="showOldPassword = true"
                  @mouseup="showOldPassword = false"
                  @mouseleave="showOldPassword = false"
                >
                  👁
                </button>
              </div>
            </div>

            <div class="form-group password-group">
              <label>Mật khẩu mới</label>

              <div class="password-wrapper">
                <input
                  :type="showNewPassword ? 'text' : 'password'"
                  v-model="passwordForm.newPassword"
                  class="form-control"
                />

                <button
                  type="button"
                  class="btn-eye"
                  @mousedown="showNewPassword = true"
                  @mouseup="showNewPassword = false"
                  @mouseleave="showNewPassword = false"
                >
                  👁
                </button>
              </div>
            </div>

            <div class="form-group password-group">
              <label>Xác nhận mật khẩu</label>

              <div class="password-wrapper">
                <input
                  :type="showConfirmPassword ? 'text' : 'password'"
                  v-model="passwordForm.confirmPassword"
                  class="form-control"
                />

                <button
                  type="button"
                  class="btn-eye"
                  @mousedown="showConfirmPassword = true"
                  @mouseup="showConfirmPassword = false"
                  @mouseleave="showConfirmPassword = false"
                >
                  👁
                </button>
              </div>
            </div>

            <button class="btn-save" :disabled="loadingPassword">
              {{ loadingPassword ? "Đang xử lý..." : "Đổi mật khẩu" }}
            </button>
          </form>
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
import api from "@/services/api";
import { useAuthStore } from "@/stores/auth";
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
  }, 2000);
};
const authStore = useAuthStore();
const loading = ref(false);
const activeTab = ref("profile");

const loadingPassword = ref(false);

const passwordForm = ref({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});
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
const showOldPassword = ref(false);
const showNewPassword = ref(false);
const showConfirmPassword = ref(false);
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
    showToastMsg("success", "Cập nhật thông tin thành công");
  } catch (err) {
    console.error(err);
    showToastMsg("error", "Cập nhật thất bại");
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
const changePassword = async () => {
  if (!passwordForm.value.oldPassword) {
    showToastMsg("error", "Vui lòng nhập mật khẩu cũ");
    return;
  }

  if (!passwordForm.value.newPassword) {
    showToastMsg("error", "Vui lòng nhập mật khẩu mới");
    return;
  }

  if (passwordForm.value.newPassword.length < 6) {
    showToastMsg("error", "Mật khẩu phải >= 6 ký tự");
    return;
  }

  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    showToastMsg("error", "Xác nhận mật khẩu không khớp");
    return;
  }

  loadingPassword.value = true;

  try {
    const res = await api.put("/users/change-password", {
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword,
    });

    showToastMsg(
      "success",
      res.data?.message || "Đổi mật khẩu thành công"
    );

    passwordForm.value = {
      oldPassword: "",
      newPassword: "",
      confirmPassword: "",
    };
  } catch (err) {
    const msg = err.response?.data?.message || "Đổi mật khẩu thất bại";
    showToastMsg("error", msg);
  } finally {
    loadingPassword.value = false;
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
.password-wrapper {
  position: relative;
}

.password-wrapper input {
  padding-right: 40px;
}

.btn-eye {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 16px;
  opacity: 0.7;
}

.btn-eye:hover {
  opacity: 1;
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
.form-wrapper {
  display: grid;
  grid-template-columns: 180px 1fr;
  gap: 20px;
}

/* SIDE TAB */
.side-tabs {
  background: #fff;
  border-radius: 10px;
  border: 1px solid #e2e2e2;
  display: flex;
  flex-direction: column;
  height: fit-content;
}

.tab-item {
  padding: 12px;
  text-align: center;
  cursor: pointer;
  border-bottom: 1px solid #eee;
  font-weight: 500;
  transition: 0.2s;
}

.tab-item:hover {
  background: #f5f5f5;
}

/* ACTIVE */
.tab-item.active {
  background: #333;
  color: #fff;
}

/* CONTENT */
.tab-content {
  background: #fff;
  border-radius: 10px;
  padding: 24px;
  border: 1px solid #e2e2e2;
}
.dark .form-wrapper {
  background: transparent;
}

.dark .side-tabs {
  background: #1e293b;
  border-color: #334155;
}

.dark .tab-item {
  color: #e2e8f0;
  border-bottom: 1px solid #334155;
}

.dark .tab-item:hover {
  background: #334155;
}

.dark .tab-item.active {
  background: #475569;
  color: #fff;
}

.dark .tab-content {
  background: #1e293b;
  border-color: #334155;
}
.toast-overlay {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
}

.toast-box {
  min-width: 280px;
  max-width: 360px;
  padding: 12px 14px;
  border-radius: 10px;

  display: flex;
  align-items: center;
  gap: 10px;

  font-size: 14px;
  font-weight: 500;

  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  animation: slideIn 0.25s ease;
}

.toast-icon {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

/* SUCCESS */
.toast-box.success {
  background: #ecfdf5;
  color: #065f46;
}
.toast-box.success .toast-icon {
  background: #10b981;
  color: white;
}

/* ERROR */
.toast-box.error {
  background: #fef2f2;
  color: #7f1d1d;
}
.toast-box.error .toast-icon {
  background: #ef4444;
  color: white;
}

/* DARK MODE */
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
    transform: translateX(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}
</style>
