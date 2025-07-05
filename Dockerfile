# 使用 OpenJDK 作为基础镜像（推荐 alpine 版本，体积小）
FROM openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 将 JAR 文件复制到容器中
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# 暴露 Spring Boot 默认端口（按需修改）
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]