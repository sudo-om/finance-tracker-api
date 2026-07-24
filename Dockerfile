# ==========================================
# Stage 1: Build Spring Boot Application
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS backend-builder
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# ==========================================
# Stage 2: Final Production Runtime Image
# ==========================================
FROM eclipse-temurin:21-jre-alpine AS final
WORKDIR /app

# Copy Compiled Spring Boot Executable JAR
COPY --from=backend-builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
