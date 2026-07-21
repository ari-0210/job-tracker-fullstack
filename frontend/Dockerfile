FROM node:20-alpine AS builder

WORKDIR /app

# 先复制 package 依赖文件并安装（利用 Docker 缓存加速）
COPY package*.json ./
RUN npm install

# 复制前端源码并进行生产环境打包（直接在 Docker 内生成最新的 dist）
COPY . .
RUN npm run build
# ----- 阶段 2: 运行 Nginx -----
# 使用一个轻量的 Nginx 基础镜像
FROM nginx:1.27-alpine

# 将本地已经打包好的 dist 文件夹，直接复制到 Nginx 的托管目录下
COPY dist/ /usr/share/nginx/html

# 复制自定义的 Nginx 配置文件，覆盖掉 Nginx 默认的
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露 Nginx 监听的端口
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]