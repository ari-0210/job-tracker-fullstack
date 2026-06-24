# frontend/Dockerfile
FROM nginx:1.27-alpine

# 1. 直接把你在本地跑 npm run build 生成的 dist 文件夹复制进去
COPY dist/ /usr/share/nginx/html

# 2. 复制你的 nginx 配置文件
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]