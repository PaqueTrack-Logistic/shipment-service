# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir la aplicación (genera JAR en target/)
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-noble

WORKDIR /app

# Instalar curl para healthcheck
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Copiar cualquier JAR generado en target/ como app.jar
COPY --from=builder /app/target/*.jar app.jar

# Crear usuario no root por seguridad
RUN if ! id -u appuser >/dev/null 2>&1; then useradd -m appuser; fi \
    && chown -R appuser:appuser /app
USER appuser

# Exponer puerto (Render asigna dinámico en PORT)
EXPOSE 10000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -fsS "http://127.0.0.1:${PORT:-10000}/actuator/health" || exit 1

# Activar logs y colores para depuración
ENV SPRING_OUTPUT_ANSI_ENABLED=always
ENV SPRING_BOOT_LOGGING_LEVEL_ROOT=DEBUG

# Ejecutar la aplicación con puerto dinámico y perfil dev por defecto
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-10000} --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev}"]
