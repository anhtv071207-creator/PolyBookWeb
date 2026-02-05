import { defineStore } from "pinia";
import { jwtDecode } from "jwt-decode";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("token"),
    email: localStorage.getItem("email"),
    role: localStorage.getItem("role"),
    user: null,
    isLoggedIn: !!localStorage.getItem("token"),
  }),

  getters: {
    userId: (state) => {
      if (state.user?.id) return state.user.id;

      const id = localStorage.getItem("userId");
      return id ? Number(id) : null;
    },
  },

  actions: {
    setAuth(payload) {
      const d = jwtDecode(payload.token);
      const userId = Number(d.userId ?? d.id);

      this.token = payload.token;
      this.email = d.sub;
      this.role = d.role || d.authorities?.[0] || d.scope || null;
      this.user = { id: userId, name: payload.hoTen || d.sub };
      this.isLoggedIn = true;

      localStorage.setItem("token", this.token);
      localStorage.setItem("email", this.email);
      localStorage.setItem("role", this.role);
      localStorage.setItem("userId", userId);
      localStorage.setItem("hoTen", this.user.name);
    },

    initAuth() {
      const token = localStorage.getItem("token");
      if (!token) return;

      const d = jwtDecode(token);
      const userId = Number(d.userId ?? d.id);

      this.token = token;
      this.email = d.sub;
      this.role = d.role || d.authorities?.[0] || d.scope || null;
      this.user = { id: userId, name: localStorage.getItem("hoTen") || d.sub };
      this.isLoggedIn = true;
    },

    logout() {
      this.$reset();
      localStorage.clear();
    },
  },
});
