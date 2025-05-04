# Етап 1: Складання
FROM gradle:8.6-jdk21 AS builder
WORKDIR /home/gradle/project

# Копіюємо весь проєкт (усі модулі)
COPY . .

# Переконуємося, що Gradle wrapper є виконуваним
RUN chmod +x gradlew

# Публікуємо модуль common у mavenLocal
RUN ./gradlew :common:publishToMavenLocal

# Збираємо модуль api-gateway, пропускаючи тести
RUN ./gradlew :api-gateway:clean :api-gateway:build -x test

# Етап 2: Виконання
FROM openjdk:21-jdk
WORKDIR /app

# Копіюємо зібраний JAR з етапу builder
COPY --from=builder /home/gradle/project/api-gateway/build/libs/*.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]