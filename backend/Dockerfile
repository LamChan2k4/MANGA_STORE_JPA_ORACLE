# GIAI ĐOẠN 1: Build file JAR
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
# Copy pom.xml và tải thư viện trước để cache cho nhanh
COPY pom.xml .
RUN mvn dependency:go-offline
# Copy source và đóng gói
COPY src ./src
RUN mvn clean package -DskipTests

# GIAI ĐOẠN 2: Chạy ứng dụng
FROM eclipse-temurin:21-jre
WORKDIR /app
# Lấy file jar vừa build ở trên sang
COPY --from=build /app/target/*.jar app.jar
# Folder lưu ảnh PDF đã xẻ
RUN mkdir manga_storage
# Mở cổng 8082 (như chúng ta đã bàn để không đè lên bản kiến 8081)
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]