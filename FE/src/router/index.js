import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Homepage.vue'
import BookDetail from '../views/BookDetail.vue'
import Login from '../views/Login.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: Home
  },
  {
    path: '/login',
    name: 'login',
    component: Login
  },
  {
    path: '/book/:id',
    name: 'book-detail',
    component: BookDetail
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})



export default router
