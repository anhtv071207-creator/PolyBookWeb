import { defineStore } from "pinia"
import { jwtDecode } from "jwt-decode"

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("token"),
    email: localStorage.getItem("email"),
    role: localStorage.getItem("role"),
    user: localStorage.getItem("hoTen")
      ? { name: localStorage.getItem("hoTen") }
      : null,
    isLoggedIn: !!localStorage.getItem("token"),
  }),

  actions: {
    setAuth(payload) {
      const decoded = jwtDecode(payload.token)

      const rawRole =
        decoded.role ||
        decoded.authorities?.[0] ||
        decoded.scope ||
        null

      this.token = payload.token
      this.email = decoded.sub
      this.role = rawRole
      this.user = { name: payload.hoTen || decoded.sub }
      this.isLoggedIn = true

      localStorage.setItem("token", payload.token)
      localStorage.setItem("email", this.email)
      localStorage.setItem("role", rawRole)
      localStorage.setItem("hoTen", this.user.name)
    },

    logout() {
      this.$reset()
      localStorage.clear()
    },
  },
})
