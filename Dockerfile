# Stage 1: Build
FROM maven:3.9-eclipse-temurin-22 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .

# Download dependencies (this layer is cached if pom.xml hasn't changed)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:22-jre-noble

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create non-root user for security
RUN if ! id -u appuser >/dev/null 2>&1; then useradd -m appuser; fi \
    && chown -R appuser:appuser /app
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -fsS "http://127.0.0.1:${PORT:-8080}/actuator/health/liveness" || exit 1

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]