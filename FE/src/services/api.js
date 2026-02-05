import axios from "axios";
import { useAuthStore } from "@/stores/auth";
const api = axios.create({
  baseURL: "http://localhost:8080/api",
});
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = "Bearer " + token;
    }
    const auth = useAuthStore();

    if (!config.data) {
      config.data = {};
    }
    config.data.userId = auth.user?.id ?? null;
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;
