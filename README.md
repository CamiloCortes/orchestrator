# Orchestrator Service

API backend desarrollado con Spring Boot 4 y Java 21 para orquestar operaciones entre servicios de usuarios, saldo y transferencias.

## Descripción

Este proyecto expone un servicio REST seguro que:

- valida sesión mediante `x-session-id` y datos almacenados en Redis
- propaga trazabilidad con `x-trace-id`
- orquesta solicitudes de transferencia hacia servicios externos mediante Feign
- maneja errores de negocio y validación con respuestas JSON consistentes
- utiliza Resilience4j para tolerancia a fallos y reintentos

## Tecnologías

- Java 21
- Spring Boot 4.0.6
- Spring Cloud OpenFeign
- Spring Data Redis
- Spring Validation
- Resilience4j
- Logback + logstash encoder
- SpringDoc OpenAPI UI
- Gradle

## Requisitos

- JDK 21
- Redis accesible desde el servicio
- Servicios externos para:
  - Core Usuarios
  - Core Saldo
  - Core Transferencias

## Configuración

El archivo de configuración principal se encuentra en `src/main/resources/application.yml`.

Valores importantes:

- `session.encryption-key`: clave AES para desencriptar sesiones almacenadas en Redis
- `spring.data.redis.host` y `spring.data.redis.port`: conexión a Redis
- `core.usuarios.url`, `core.saldo.url`, `core.transferencias.url`: endpoints de los servicios externos
- `cors.allowed-origin`: origen permitido para solicitudes CORS

Puedes sobreescribir la clave de sesión con la variable de entorno:

```bash
export CRIPT_KEY="tu-clave-de-encriptacion"
```

> En Windows PowerShell usa `setx CRIPT_KEY "tu-clave-de-encriptacion"` o `powershell -Command "$env:CRIPT_KEY='tu-clave-de-encriptacion'"`.

## Construir

Desde la raíz del proyecto:

```bash
d:; cd d:\cursosIA\backend_daviplata\orchestrator-service
./gradlew.bat clean build
```

## Ejecutar

```bash
./gradlew.bat bootRun
```

O ejecutar el JAR generado:

```bash
java -jar build/libs/orchestrator-service-0.0.1-SNAPSHOT.jar
```

## Pruebas

Ejecuta los tests con:

```bash
./gradlew.bat test
```

## Endpoints clave

- `POST /transfers` : crea una transferencia orquestada

> Asegúrate de enviar los encabezados requeridos de sesión y trazabilidad desde el cliente, como `x-session-id` y `x-trace-id`.

## Estructura principal

- `src/main/java/com/backend/orchestrator/orchestrator_service` : código fuente principal
- `src/main/resources` : configuración, `application.yml`, y perfiles
- `build.gradle` : dependencias y configuración de Gradle

## Notas adicionales

- Este proyecto activa clientes Feign con `@EnableFeignClients` en `OrchestratorServiceApplication`.
- La aplicación espera que Redis almacene sesiones cifradas con la misma clave definida en `session.encryption-key`.
