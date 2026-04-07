# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .

RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application and rename JAR to app.jar
RUN mvn clean package -DskipTests -B -DfinalName=app

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-noble

WORKDIR /app

# Install curl for healthcheck
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Copy JAR from builder stage
COPY --from=builder /app/target/app.jar app.jar

# Create non-root user for security
RUN if ! id -u appuser >/dev/null 2>&1; then useradd -m appuser; fi \
    && chown -R appuser:appuser /app
USER appuser

# Expose the dynamic port used by Render
EXPOSE 10000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -fsS "http://127.0.0.1:${PORT:-10000}/actuator/health" || exit 1

# Set Spring Boot logging and ANSI colors for better logs
ENV SPRING_OUTPUT_ANSI_ENABLED=always
ENV SPRING_BOOT_LOGGING_LEVEL_ROOT=DEBUG

# Run the application with dynamic port and profile dev by default
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-10000} --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev}"]
