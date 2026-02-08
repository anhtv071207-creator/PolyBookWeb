<script setup>
import { useAuthStore } from "@/stores/auth";
import Header from './components/Header.vue';
import Footer from './components/Footer.vue';
import route from './router';
const auth = useAuthStore();
</script>

<template>
<Header :key="auth.isLoggedIn" ></Header>
<router-view />
<Footer></Footer>
</template>
