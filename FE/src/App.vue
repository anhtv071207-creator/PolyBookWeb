<script setup>
import { onMounted } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useThemeStore } from "@/stores/theme";

import Header from "./components/Header.vue";
import Footer from "./components/Footer.vue";

const auth = useAuthStore();
const theme = useThemeStore();

onMounted(() => {
  theme.initTheme();
});
</script>

<template>
<Header :key="auth.isLoggedIn" ></Header>
<router-view />
<Footer></Footer>
</template>
