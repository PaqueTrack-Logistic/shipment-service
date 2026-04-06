# shipment-service

Servicio de backend responsable de la gestión de envíos (shipments). Permite crear y consultar envíos vía REST, persiste en PostgreSQL y publica eventos en RabbitMQ.

## Requisitos

- Docker y Docker Compose
- Java 17 (si deseas ejecutar local sin Docker)

## Variables de entorno

El proyecto utiliza un archivo `.env` en la raíz. Ya está incluido con valores base para pruebas locales.

Variables principales:

- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`
- `RABBITMQ_HOST`
- `RABBITMQ_PORT`
- `RABBITMQ_USERNAME`
- `RABBITMQ_PASSWORD`
- `RABBITMQ_VHOST`
- `RABBITMQ_SSL_ENABLED`

## Ejecutar backend en local con PostgreSQL en Docker + RabbitMQ cloud

### Setup recomendado para desarrollo:

1. Asegúrate de que credenciales de RabbitMQ cloud están en `.env`:
   ```bash
   cat .env | grep RABBITMQ
   ```

2. Levanta **solo PostgreSQL** en Docker:
   ```bash
   docker compose up postgres
   ```

3. Levanta el **backend en tu host** desde IDE o terminal:
   ```bash
   mvn spring-boot:run
   # o desde tu IDE (Run → ShipmentApplication)
   ```

4. Backend estará disponible en:
   - API REST: http://localhost:8081
   - Swagger UI: http://localhost:8081/swagger-ui.html
   - Health check: http://localhost:8081/actuator/health

**Conexiones:**
- PostgreSQL: `localhost:5432` (desde host) → Contenedor Docker
- RabbitMQ: Cloud (CloudAMQP) con SSL → Credenciales en `.env`

### Para probar deployado en Docker (opcional):

1. Descomentar sección `shipment-service` en `docker-compose.yml`
2. Ejecutar: `docker compose up --build`
3. Backend estará en `http://localhost:8081`

## Detener entorno

```bash
docker compose down
```
