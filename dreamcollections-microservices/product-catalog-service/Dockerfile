# Stage 1: Build the application using Maven
FROM maven:3.8.5-openjdk-17 AS build
LABEL authors="Jules"
WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw

# Copy source code
COPY src ./src

# Build the application JAR, skipping tests
RUN ./mvnw package -DskipTests -Dmaven.test.skip=true -Dspring-boot.repackage.skip=false

# Stage 2: Create the runtime image from a slim JRE base
FROM openjdk:17-jre-slim
WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY --from=build /app/${JAR_FILE} app.jar

# Expose the port the application runs on
EXPOSE 8082

ENV SERVER_PORT=8082
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/product_catalog_db
ENV SPRING_DATASOURCE_USERNAME=admin
ENV SPRING_DATASOURCE_PASSWORD=password

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
