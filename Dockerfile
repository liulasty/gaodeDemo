# 使用 OpenJDK 作为基础镜像（推荐 alpine 版本，体积小）
FROM openjdk:17-jdk-alpine

# 添加容器元数据标签
LABEL maintainer="myDemo"
LABEL description="Spring Boot Application Container"

# 设置工作目录
WORKDIR /app

# 将 JAR 文件复制到容器中
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# 暴露 Spring Boot 默认端口（按需修改）
EXPOSE 8080

# 设置环境变量（可运行时覆盖）
ENV SPRING_PROFILES_ACTIVE=prod

# 添加日志目录挂载点
VOLUME /app/logs

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]

# 使用说明：
# 构建时命名镜像: docker build -t my-app-image .
# 运行时命名容器: docker run --name my-app-container -p 8080:8080 -v /path/to/host/logs:/app/logs my-app-image