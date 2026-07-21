# 1. Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
RUN mvn --batch-mode dependency:go-offline
COPY src/ src/
RUN mvn --batch-mode -DskipTests package

# 2. Runtime stage
FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]