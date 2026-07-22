# 只需要基础运行环境，镜像极小
FROM openjdk:17-jdk-slim
WORKDIR /app
# 直接使用 GitHub Actions 传输过来的 jar 包
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]