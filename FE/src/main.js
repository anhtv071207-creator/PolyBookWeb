import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from '@/stores/auth' // ✅ BẮT BUỘC

import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'bootstrap-icons/font/bootstrap-icons.css'
import './style.css'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia) 

const auth = useAuthStore()
auth.initAuth()

app.use(router)
app.mount('#app')
