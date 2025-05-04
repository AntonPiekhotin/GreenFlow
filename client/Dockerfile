# Stage 1: Build
FROM gradle:8.6-jdk21 AS builder
WORKDIR /home/gradle/project

# Copy the common project
COPY common /home/gradle/project/common

# Copy the client project
COPY client /home/gradle/project/client

# Ensure the Gradle wrapper in common is executable
RUN chmod +x common/gradlew

# Publish common to mavenLocal
RUN cd common && ./gradlew publishToMavenLocal

# Ensure the Gradle wrapper in client is executable
RUN chmod +x client/gradlew

# Build client, skipping tests
RUN cd client && ./gradlew clean build -x test

# Stage 2: Runtime
FROM openjdk:21-jdk
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /home/gradle/project/client/build/libs/*.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]