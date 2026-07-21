# === 阶段 1: 编译打包 ===
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# === 阶段 2: Nginx 部署 ===
FROM nginx:1.27-alpine

# 注意：这里是从上面的 builder 镜像层提取打包好的 dist！
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]