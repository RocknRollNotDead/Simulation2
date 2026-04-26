# Используем Maven образ для сборки
FROM maven:3.9-eclipse-temurin-21 AS build

# Копируем проект
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY .env .

# Собираем проект (пропускаем JavaFX тесты)
RUN mvn clean package -DskipTests

# Используем JRE для запуска
FROM eclipse-temurin:21-jre

WORKDIR /app

# Копируем собранный jar
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/.env .env

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
