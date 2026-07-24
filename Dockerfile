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
# Stage 2: Build React Vite Application
# ==========================================
FROM node:20-alpine AS frontend-builder
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
RUN npm run build

# ==========================================
# Stage 3: Final Production Runtime Image
# ==========================================
FROM eclipse-temurin:21-jre-alpine AS final
WORKDIR /app
EXPOSE 8081

# Copy Backend JAR
COPY --from=backend-builder /app/target/*.jar app.jar

# Environment Variables
ENV PORT=8081
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
