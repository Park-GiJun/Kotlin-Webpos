# Multi-stage build for Main Server Microservice
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src

# Build the application
RUN gradle build -x test --no-daemon

# Runtime stage
FROM openjdk:21-jre-slim

# Add curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the built jar
COPY --from=builder /app/build/libs/*.jar app.jar

# Create non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]