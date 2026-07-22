FROM nginx:1.27-alpine

# 清理 Nginx 默认的 html 文件
RUN rm -rf /usr/share/nginx/html/*

# 直接把我们在 Actions 里打包好、并由 SCP 传过来的 dist 目录，复制到 Nginx 的网页目录下
COPY dist /usr/share/nginx/html

# 暴露 80 端口
EXPOSE 80

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]