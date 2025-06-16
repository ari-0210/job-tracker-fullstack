const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 5173, // 确保前端在一个独立的端口运行，例如 5173
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端运行地址和端口
        changeOrigin: true, 
      }
    }
  }
});
