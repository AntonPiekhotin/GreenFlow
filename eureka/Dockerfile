# Stage 1: Build
FROM gradle:8.6-jdk21 AS builder
WORKDIR /home/gradle/project

# Copy the eureka project
COPY eureka /home/gradle/project/eureka

# Ensure the Gradle wrapper in eureka is executable
RUN chmod +x eureka/gradlew

# Build eureka, skipping tests
RUN cd eureka && ./gradlew clean build -x test

# Stage 2: Runtime
FROM openjdk:21-jdk
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /home/gradle/project/eureka/build/libs/*.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]