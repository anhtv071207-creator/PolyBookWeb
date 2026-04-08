<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "@/services/api";
import { useThemeStore } from "@/stores/theme";

const theme = useThemeStore();
const router = useRouter();


onMounted(fetchStatistics);
</script>

<template>

</template>
<style scoped>
.container {
  max-width: 1200px;
  margin: 40px auto;
  padding: 30px;
  background: #f6f7f9;
  min-height: 100vh;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
}

.page-header h2 {
  font-size: 22px;
  font-weight: 600;
  margin: 0;
}

.btn-back {
  padding: 8px 16px;
  border-radius: 6px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  font-size: 14px;
}

.btn-back:hover {
  background: #f2f2f2;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 8px;
  border: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stat-icon {
  font-size: 22px;
}

.stat-card h4 {
  font-size: 13px;
  font-weight: 500;
  color: #666;
  margin: 0;
}

.stat-card p {
  font-size: 26px;
  font-weight: 700;
  margin: 0;
}

.blue {
  border-left: 4px solid #0d6efd;
}

.green {
  border-left: 4px solid #198754;
}

.orange {
  border-left: 4px solid #fd7e14;
}

.purple {
  border-left: 4px solid #6f42c1;
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

/* stats card */

.dark .stat-card {
  background: #1e293b;
  border-color: #334155;
}

.dark .stat-card h4 {
  color: #94a3b8;
}

.dark .stat-card p {
  color: #f1f5f9;
}

/* icon */

.dark .stat-icon {
  filter: brightness(1.2);
}

/* border colors giữ nguyên nhưng nhẹ hơn trong dark */

.dark .blue {
  border-left-color: #3b82f6;
}

.dark .green {
  border-left-color: #22c55e;
}

.dark .orange {
  border-left-color: #f59e0b;
}

.dark .purple {
  border-left-color: #a855f7;
}
</style>
