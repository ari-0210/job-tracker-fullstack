import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), tailwindcss()],
  esbuild: {
    // 生产环境打包时，自动在语法树层面把 console.log 和 debugger 物理蒸发
    drop: ["console", "debugger"],
  },
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  server: {
    port: 5173, // 前端在一个独立的端口运行
    proxy: {
      "/api": {
        target: "http://localhost:8080", // 后端运行地址和端口
        changeOrigin: true,
      },
      "/uploads": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
