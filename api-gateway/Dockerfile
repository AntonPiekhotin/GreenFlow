# етап 1: збірка
FROM gradle:8.6-jdk21 AS builder
WORKDIR /home/gradle/project

# Копіюємо Gradle wrapper і всі файли
COPY . .

# Переконайтеся, що gradlew є виконуваним
RUN chmod +x gradlew

# Збираємо jar (без тестів, щоб було швидше)
RUN ./gradlew clean build -x test

# етап 2: рантайм
FROM openjdk:21-jdk
WORKDIR /app

# Копіюємо зібраний jar з попереднього етапу
COPY --from=builder /home/gradle/project/build/libs/*.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]