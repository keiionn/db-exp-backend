# ==========================================
# Stage 1: Build the Application
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml first to leverage Docker layer caching for dependencies
COPY pom.xml .
# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skipping tests to speed up build)
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Create the Runtime Image
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the JAR file from the build stage
# The name matches the artifactId and version in your pom.xml
COPY --from=build /app/target/db-experiment-1.0.0.jar app.jar

# Expose the default port
EXPOSE 8080

# Set default environment variables (can be overridden at runtime)
# Defaulting to 'prod' so we don't accidentally drop tables in containers
ENV SPRING_PROFILES_ACTIVE=prod
# Default DB URL points to 'host.docker.internal' for local testing,
# or a service name like 'mysql' if using Docker Compose
ENV DB_URL=jdbc:mysql://host.docker.internal:3306/db_experiment
ENV DB_USERNAME=dbexp
ENV DB_PASSWORD=A1b212345

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]