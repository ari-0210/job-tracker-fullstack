import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), tailwindcss()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  server: {
    port: 5173, // learn;前端在一个独立的端口运行
    proxy: {
      "/api": {
        target: "http://localhost:8080", // learn;后端运行地址和端口
        changeOrigin: true,
      },
    },
  },
});
