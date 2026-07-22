FROM nginx:1.27-alpine

# 从上面的 builder 镜像层提取打包好的 dist
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]