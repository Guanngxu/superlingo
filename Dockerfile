# 第一阶段：编译构建阶段
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# 略过测试打包，加快 Render 的部署速度
RUN mvn clean package -DskipTests

# 第二阶段：运行阶段
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# 从第一阶段复制打包好的 jar 文件
COPY --from=build /app/target/*.jar app.jar

# 声明暴露端口（非必须，但有助于 Render 识别）
EXPOSE 8080

# 启动命令：注意限制 JVM 内存，防止在 Render 免费层被杀掉进程
ENTRYPOINT ["java", "-Xmx300m", "-jar", "app.jar"]