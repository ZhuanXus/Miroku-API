# ========== 基础镜像 ==========
FROM eclipse-temurin:25-jre

# ========== 元信息（可选但推荐） ==========
LABEL maintainer="Miroku 关注永雏塔菲谢谢喵~"
LABEL description="Miroku-API Service with Flyway Auto-Migration"

# ========== 工作目录 ==========
WORKDIR /app

# ========== 复制 Jar 包（关键：写死文件名！） ==========
COPY target/Miroku-API-0.0.1-SNAPSHOT.jar app.jar

# ========== 默认环境变量（会被 docker-compose 覆盖，仅作兜底） ==========
# 这些值会和你 application.yml 里的 ${VAR:default} 配合使用
ENV DB_HOST=localhost \
    DB_PORT=3306 \
    DB_NAME=miroku_api \
    MYSQL_USERNAME=root \
    MYSQL_PASSWORD=1234 \
    DEEPSEEK_API_KEY="" \
    DOUBAO_API_KEY="" \
    QWEN_API_KEY=""  \
    TZ=Asia/Shanghai \
    JAVA_OPTS="-Xmx512m -Xms256m"

# ========== 暴露端口（根据你的 server.port 调整） ==========
EXPOSE 8080

# ========== 健康检查（可选，需引入 spring-boot-starter-actuator） ==========
# 如果没加 actuator，可注释掉或改成简单端口检测
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# ========== 启动命令 ==========
# 用 sh -c 让 $JAVA_OPTS 变量能被正确展开
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]