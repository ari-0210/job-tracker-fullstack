# ----- 阶段 1: 构建 Vue 应用 -----
# 使用一个 Node.js 基础镜像
FROM node:20-alpine AS builder

# 设置工作目录
WORKDIR /app

# 复制 package.json 和 package-lock.json 
COPY package*.json ./

# 安装依赖
RUN npm install

# 复制所有源代码
COPY . .

# 运行构建命令，生成 dist 文件夹
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