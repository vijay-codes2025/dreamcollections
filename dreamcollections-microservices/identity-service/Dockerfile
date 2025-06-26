# Stage 1: Build the application using Maven
FROM maven:3.8.5-openjdk-17 AS build
LABEL authors="Jules"
WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
# Ensure mvnw is executable - might not be needed if base image handles it, but good practice
RUN chmod +x mvnw

# Copy source code
COPY src ./src

# Build the application JAR, skipping tests
# This command assumes the JAR will be named based on artifactId and version from pom.xml
RUN ./mvnw package -DskipTests -Dmaven.test.skip=true -Dspring-boot.repackage.skip=false

# Stage 2: Create the runtime image from a slim JRE base
FROM openjdk:17-jre-slim
WORKDIR /app

# Argument to specify the JAR file (can be overridden at build time)
ARG JAR_FILE=target/*.jar

# Copy the application JAR from the build stage
COPY --from=build /app/${JAR_FILE} app.jar

# Expose the port the application runs on (configured in application.properties)
EXPOSE 8081

# Set default environment variables (can be overridden in docker-compose or ECS Task Definition)
ENV SERVER_PORT=8081
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/identity_db
ENV SPRING_DATASOURCE_USERNAME=admin
ENV SPRING_DATASOURCE_PASSWORD=password
# Add other ENV vars as needed, e.g., JWT secrets, service URLs for Feign (though Feign URLs are better configured in properties files using service discovery names)

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
