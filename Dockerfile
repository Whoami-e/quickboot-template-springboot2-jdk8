# ============================================================
# QuickBoot Dockerfile - 多阶段构建
# Stage 1: 使用 Maven + JDK 8 编译打包
# Stage 2: 使用 JRE 8 Alpine 运行（最小化镜像体积）
# ============================================================

# ---- Stage 1: Builder（构建阶段） ----
# 使用 maven:3.9-eclipse-temurin-8 镜像，内置 Maven 3.9 与 JDK 8
FROM maven:3.9-eclipse-temurin-8 AS builder

# 设置工作目录
WORKDIR /build

# 先复制所有 pom.xml，利用 Docker 层缓存加速依赖下载
# 这样当源码变更但依赖未变时，可复用依赖下载层，加快构建速度
COPY pom.xml ./
COPY quickboot-common/pom.xml quickboot-common/
COPY quickboot-domain/pom.xml quickboot-domain/
COPY quickboot-infrastructure/pom.xml quickboot-infrastructure/
COPY quickboot-application/pom.xml quickboot-application/
COPY quickboot-web/pom.xml quickboot-web/

# 下载依赖（利用缓存），允许失败以继续后续构建
RUN mvn dependency:go-offline -B || true

# 复制全部源码
COPY . .

# 构建项目，跳过测试（-q 安静模式减少日志输出）
RUN mvn clean package -DskipTests -q

# ---- Stage 2: Runtime（运行阶段） ----
# 使用 eclipse-temurin:8-jre-alpine 镜像，仅含 JRE，镜像体积最小
FROM eclipse-temurin:8-jre-alpine

# 设置时区为东八区（Asia/Shanghai）
ENV TZ=Asia/Shanghai
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    apk del tzdata

# 设置工作目录
WORKDIR /app

# 从 builder 阶段复制构建产物
COPY --from=builder /build/quickboot-web/target/*.jar app.jar

# JVM 参数优化：
# - -XX:+UseG1GC：使用 G1 垃圾收集器，适合容器化环境
# - -XX:MaxRAMPercentage=75.0：限制最大堆内存为容器可用内存的 75%
# - -Djava.security.egd=file:/dev/./urandom：加速随机数生成，避免启动卡顿
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# 暴露服务端口
EXPOSE 8080

# 启动命令（exec 形式，确保应用作为 PID 1 运行，正确接收信号）
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
