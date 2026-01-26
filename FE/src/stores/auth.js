import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    email: localStorage.getItem('email') || null,
    role: localStorage.getItem('role') || null,

    // thêm user object để header dùng
    user: localStorage.getItem('hoTen')
      ? { name: localStorage.getItem('hoTen') }
      : null,

    isLoggedIn: !!localStorage.getItem('token')
  }),

  actions: {
    setAuth(payload) {
      this.token = payload.token
      this.email = payload.email
      this.role = payload.role
      this.user = {
        name: payload.hoTen
      }
      this.isLoggedIn = true

      localStorage.setItem('token', payload.token)
      localStorage.setItem('email', payload.email)
      localStorage.setItem('role', payload.role)
      localStorage.setItem('hoTen', payload.hoTen)
    },

    logout() {
      this.token = null
      this.email = null
      this.role = null
      this.user = null
      this.isLoggedIn = false

      localStorage.clear()
    }
  }
})
