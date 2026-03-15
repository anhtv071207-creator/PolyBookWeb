import { defineStore } from "pinia";
import { ref } from "vue";

export const useThemeStore = defineStore("theme", () => {
  const darkMode = ref(localStorage.getItem("theme") === "dark");

  const toggleTheme = () => {
    darkMode.value = !darkMode.value;

    if (darkMode.value) {
      document.documentElement.classList.add("dark");
      localStorage.setItem("theme", "dark");
    } else {
      document.documentElement.classList.remove("dark");
      localStorage.setItem("theme", "light");
    }
  };

  const initTheme = () => {
    if (darkMode.value) {
      document.documentElement.classList.add("dark");
    }
  };

  return { darkMode, toggleTheme, initTheme };
});