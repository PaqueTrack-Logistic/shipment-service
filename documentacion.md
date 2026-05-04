# Documentación del Proyecto Shipment Service

## Introducción

El **Shipment Service** es un microservicio desarrollado con Spring Boot que forma parte del sistema de logística **PaqueTrack**. Este servicio se encarga de la gestión completa de envíos, incluyendo la creación, consulta y actualización de estados de los mismos. Está diseñado siguiendo principios de arquitectura limpia (Clean Architecture) y está preparado para despliegue en contenedores Docker.

## Arquitectura

El proyecto sigue una arquitectura hexagonal (puertos y adaptadores) con separación clara de responsabilidades:

- **Dominio (Domain)**: Contiene las reglas de negocio, entidades y casos de uso.
- **Aplicación (Application)**: Servicios que orquestan los casos de uso.
- **Infraestructura (Infrastructure)**: Adaptadores para persistencia, mensajería y APIs externas.

### Estructura de Carpetas

```
src/main/java/com/paquetrack/shipment/
├── ShipmentApplication.java          # Clase principal
├── application/
│   └── service/                      # Servicios de aplicación
├── domain/
│   ├── exception/                    # Excepciones de dominio
│   ├── model/                        # Entidades de dominio
│   └── port/                         # Puertos (interfaces)
│       ├── in/                       # Casos de uso de entrada
│       └── out/                      # Puertos de salida
└── infrastructure/
    ├── config/                       # Configuraciones
    ├── controller/                   # Controladores REST
    ├── dto/                          # Objetos de transferencia de datos
    ├── messaging/                    # Adaptadores de mensajería
    └── persistence/                  # Adaptadores de persistencia
        ├── adapter/
        ├── entity/
        ├── mapper/
        └── repository/
```

## Tecnologías Utilizadas

- **Framework**: Spring Boot 3.3.5
- **Lenguaje**: Java 21 (LTS)
- **Base de Datos**: PostgreSQL (producción)
- **Mensajería**: RabbitMQ
- **Migraciones**: Flyway
- **Documentación API**: SpringDoc OpenAPI (Swagger)
- **Tests**: JUnit 5, Karate (pruebas de integración)
- **Cobertura**: JaCoCo
- **Contenedor**: Docker
- **Build Tool**: Maven
- **Utilidades**: Lombok, Validation API

## Funcionalidades Principales

### Gestión de Envíos
- **Creación de Envíos**: API para registrar nuevos envíos con validación de datos.
- **Consulta de Envíos**: Búsqueda por ID único o filtros.
- **Actualización de Estados**: Procesamiento de eventos de cambio de estado vía mensajería.
- **Generación de Tracking ID**: Automática en formato `PQ-YYYYMMDD-XXXXXX`.

### Características Técnicas
- **Validación**: Uso de Bean Validation para entrada de datos.
- **Manejo de Errores**: Excepciones personalizadas y handlers globales.
- **Logging**: Configurado con niveles apropiados para producción.

## Configuración

### Perfiles de Spring
- **prod**: Configuración de producción con variables de entorno.
- **local**: Configuración para desarrollo local.
- **test**: Configuración para pruebas con H2.

### Configuración JPA
- `ddl-auto: validate` (Flyway maneja el esquema)
- `open-in-view: false`
- SQL formatado en logs de desarrollo

## API REST

### Endpoints Principales

#### Crear Envío
```
POST /api/shipments
Content-Type: application/json

{
  "senderName": "Juan Pérez",
  "senderAddress": "Calle 1 # 2-3",
  "senderCity": "Medellín",
  "recipientName": "María López",
  "recipientAddress": "Carrera 4 # 5-6",
  "recipientCity": "Bogotá",
  "weightKg": 2.5
}
```

**Respuesta (201 Created)**:
```json
{
  "id": "uuid-generado",
  "trackingId": "PQ-20240401-ABC123",
  "status": "CREATED",
  "senderName": "Juan Pérez",
  "recipientName": "María López",
  "createdAt": "2024-04-01T10:00:00",
  "updatedAt": "2024-04-01T10:00:00"
}
```

#### Consultar Envío por ID
```
GET /api/shipments/{id}
```

#### Consultar por Tracking ID
```
GET /api/shipments/tracking/{trackingId}
```

#### Listar Envíos con Filtros
```
GET /api/shipments?status=CREATED&senderCity=Medellín
```

### Documentación Interactiva
- **Swagger UI**: Disponible en `/swagger-ui.html`
- **OpenAPI Spec**: Disponible en `/v3/api-docs`

### Migraciones Flyway
- Ubicadas en `src/main/resources/db/migration/`
- Versionadas: `V1__create_shipments.sql`, `V2__create_shipment_event_history.sql`

## Mensajería

### RabbitMQ
- **Exchange**: Direct exchange para eventos de tracking
- **Queue**: `shipment.status.queue`
- **Routing Key**: `shipment.status.update`

### Eventos Procesados
- **Tipo**: `TrackingStatusEventDTO`
- **Campos**: `shipmentId`, `eventType`, `previousStatus`, `newStatus`, `occurredAt`

### Listener
- `ShipmentStatusUpdateListener`: Procesa actualizaciones de estado
- Manejo de errores: Reintentos y envío a Dead Letter Queue (DLQ)

## Tests

### Cobertura
- **Herramienta**: JaCoCo
- **Umbral**: Configurado en `pom.xml`
- **Reportes**: Generados en `target/site/jacoco/`

### Tipos de Tests
1. **Unitarios**: JUnit 5 para servicios y utilidades
2. **Integración**: Karate para APIs REST
3. **Mensajería**: Spring AMQP Test para RabbitMQ

### Ejemplo Karate
```gherkin
Scenario: Crear envío con datos válidos
  Given path '/api/shipments'
  And request { senderName: 'Juan Pérez', ... }
  When method POST
  Then status 201
  And match response.trackingId == '#regex PQ-\\d{8}-[A-Z0-9]{6}'
```

## Despliegue

### Docker
```dockerfile
# Multi-stage build
FROM maven:3.9-eclipse-temurin-21 AS builder
# ... build steps ...

FROM eclipse-temurin:21-jre-noble
# ... runtime setup ...
```

### Variables de Build
- **Puerto**: Configurable via `PORT` (default: 8080)
- **Health Checks**: Endpoints `/actuator/health`

### CI/CD
- **GitHub Actions**: Workflow en `.github/workflows/sonar.yml`
- **SonarCloud**: Análisis de calidad de código
- **JaCoCo**: Cobertura de código

## Monitoreo y Observabilidad

### Actuator Endpoints
- `/actuator/health`: Estado de salud
- `/actuator/info`: Información de la aplicación
- `/actuator/metrics`: Métricas de aplicación

### Logging
```yaml
logging:
  level:
    com.paquetrack: INFO
    org.hibernate.SQL: WARN
    org.springframework.amqp: INFO
```

## Seguridad

### Mejores Prácticas Implementadas
- **Usuario No-Root**: En imagen Docker
- **Variables de Entorno**: Para credenciales sensibles
- **Validación de Entrada**: Bean Validation
- **Manejo Seguro de Errores**: Sin exposición de información sensible

## Desarrollo Local

### Requisitos
- Java 21
- Maven 3.9+
- PostgreSQL (o usar H2 para desarrollo)
- RabbitMQ

### Comandos
```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run

# Construir JAR
mvn clean package -DskipTests

# Ejecutar con Docker
docker build -t shipment-service .
docker run -p 8080:8080 shipment-service
```

## Conclusión

El Shipment Service es un ejemplo de microservicio bien diseñado, siguiendo las mejores prácticas de desarrollo con Spring Boot. Su arquitectura limpia facilita el mantenimiento y escalabilidad, mientras que la contenerización y configuración adecuada lo hacen ideal para despliegues en la nube.

Para más detalles técnicos, revisar el código fuente y la documentación de Spring Boot.</content>
