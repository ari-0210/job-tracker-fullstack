# ----- 阶段 1: 构建 -----
# 使用一个包含 Maven 和 JDK 的基础镜像来构建项目
FROM maven:3.8.5-openjdk-17 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 并下载依赖，利用 Docker 的层缓存
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制所有源代码
COPY src ./src

# 运行 Maven 打包命令，生成 .jar 文件
RUN mvn clean package -DskipTests
#阶段 1 结束后，得到了一个jar 的产物，但它存在于一个非常庞大的maven 镜像环境中
# ----- 阶段 2: 运行 -----
# 使用一个更小的、只包含 Java 运行环境的基础镜像
#这个阶段的目标是使用一个只包含 Java 运行环境的小镜像，把第一阶段的构建产物拿过来，然后运行它。
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 从 builder 阶段复制构建好的 .jar 文件
COPY --from=builder /app/target/JobTracker-0.0.1-SNAPSHOT.jar app.jar
# 在 builder 阶段中，由 mvn clean package 命令生成的 .jar 文件的完整路径
#app.jar: 是目标文件名。因为源是一个文件，目标也是一个文件名，所以 Docker 会把源文件复制过来，并将其重命名为 app.jar。
#--from=builder: 这个特殊的参数告诉 Docker，不从本地电脑复制，而是从之前名为 builder 的构建阶段中去复制文件。


# 暴露后端应用运行的端口
EXPOSE 8080

# 容器启动时运行的命令，用 java 命令来运行刚刚复制过来的 app.jar 文件
# 注意：这里通过 -Dspring.profiles.active=prod 来激活生产环境的配置
CMD ["java", "-jar", "app.jar"]
