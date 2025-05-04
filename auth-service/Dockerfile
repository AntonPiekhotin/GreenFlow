# Stage 1: Build
FROM gradle:8.6-jdk21 AS builder
WORKDIR /home/gradle/project

# Copy the common project
COPY common /home/gradle/project/common

# Copy the auth-service project
COPY auth-service /home/gradle/project/auth-service

# Ensure the Gradle wrapper in common is executable
RUN chmod +x common/gradlew

# Publish common to mavenLocal
RUN cd common && ./gradlew publishToMavenLocal

# Ensure the Gradle wrapper in auth-service is executable
RUN chmod +x auth-service/gradlew

# Build auth-service, skipping tests
RUN cd auth-service && ./gradlew clean build -x test

# Stage 2: Runtime
FROM openjdk:21-jdk
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /home/gradle/project/auth-service/build/libs/*.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]