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
EXPOSE 8080

ENV SERVER_PORT=8080
# API Gateway route URIs will use service names like http://identity-service:8081
# These will be resolved by Docker's internal DNS if services are on the same Docker network.
# No explicit database ENV needed for gateway itself unless it stores configs/state.

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
