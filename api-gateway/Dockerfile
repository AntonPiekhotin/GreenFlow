# Stage 1: Build
FROM gradle:8.6-jdk21 AS builder
WORKDIR /home/gradle/project

# Copy the common project
COPY common /home/gradle/project/common

# Copy the api-gateway project
COPY api-gateway /home/gradle/project/api-gateway

# Debug: List files in common directory to verify Gradle Wrapper
RUN ls -la /home/gradle/project/common
RUN ls -la /home/gradle/project/common/gradle/wrapper

# Ensure the Gradle wrapper in common is executable
RUN chmod +x common/gradlew

# Publish common to mavenLocal
RUN cd common && ./gradlew publishToMavenLocal

# Ensure the Gradle wrapper in api-gateway is executable
RUN chmod +x api-gateway/gradlew

# Build api-gateway, skipping tests
RUN cd api-gateway && ./gradlew clean build -x test

# Stage 2: Runtime
FROM openjdk:21-jdk
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /home/gradle/project/api-gateway/build/libs/*.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]