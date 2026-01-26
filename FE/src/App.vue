<script setup>
import Header from './components/Header.vue';
import Footer from './components/Footer.vue';
import route from './router';
</script>

<template>
<Header></Header>
<router-view />
<Footer></Footer>
</template>
