# Dockerfile for Spring Boot service
FROM openjdk:23-jdk-slim AS build

# Устанавливаем Maven
RUN apt-get update && apt-get install -y maven

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# Скопировать весь проект и собрать jar
COPY src ./src
RUN mvn package -DskipTests

# Финальный образ
FROM openjdk:23-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Запуск сервиса
ENTRYPOINT ["java", "-jar", "app.jar"]
