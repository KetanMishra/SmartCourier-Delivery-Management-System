# SmartCourier Delivery Management System

SmartCourier is a backend-only courier platform built with Spring Boot microservices. It supports user registration, JWT login, parcel booking, delivery charge calculation, pickup scheduling, document upload, delivery tracking, exception handling, hub management, and admin reporting.

## What This Project Demonstrates

- Proper Spring Boot layered architecture
- Spring Cloud Gateway as the single entry point
- Eureka service discovery
- Spring Cloud Config with a Git-backed config-repo pattern
- MySQL with one schema per microservice
- JWT authentication and role-based authorization
- OpenFeign for synchronous inter-service communication
- RabbitMQ for asynchronous event-driven communication
- Swagger / OpenAPI for endpoint testing
- JUnit, Mockito, JaCoCo, and SonarQube-ready configuration

## Services

- `gateway-service`: routes all `/gateway/*` APIs
- `auth-service`: signup, login, user lookup, JWT generation
- `delivery-service`: booking, charge calculation, lifecycle updates
- `tracking-service`: tracking timeline, document upload, delivery proof
- `admin-service`: dashboard, reports, user list, hub management, exception resolution
- `discovery-server`: Eureka registry
- `config-server`: centralized configuration server

## Recommended Spring Boot Project Architecture

Each service follows a clean layered structure:

- `controller`: exposes REST endpoints
- `service`: contains business logic
- `repository`: database access via Spring Data JPA
- `entity`: domain model mapped with Hibernate
- `dto`: request/response payloads
- `config`: Swagger, security, RabbitMQ, Feign, and infrastructure beans
- `security`: JWT parsing and authorization helpers
- `exception`: centralized exception handling

This is the standard architecture typically expected in real Spring Boot backend projects because it keeps controllers small, business rules centralized, and persistence isolated.

## High-Level Flow

```text
Postman / Swagger
       |
       v
Spring Cloud Gateway
       |
       v
--------------------------------------------------------
| auth-service | delivery-service | tracking-service   |
| admin-service | config-server | discovery-server     |
--------------------------------------------------------
       |                     |
       v                     v
   MySQL Schemas         RabbitMQ Events
```

## Communication Style

### Synchronous

- `admin-service` uses OpenFeign to call `auth-service`, `delivery-service`, and `tracking-service`
- `gateway-service` routes to services using Eureka names such as `lb://auth-service`

### Asynchronous

- `delivery-service` publishes delivery lifecycle events to RabbitMQ
- `tracking-service` consumes those events and stores tracking history and proof records

## Security

- JWT tokens are created in `auth-service`
- downstream services validate the JWT in request filters
- admin operations are secured through role checks
- Swagger endpoints are left open for testing

## Delivery Lifecycle

- Main states: `DRAFT`, `BOOKED`, `PICKED_UP`, `IN_TRANSIT`, `OUT_FOR_DELIVERY`, `DELIVERED`
- Exception states: `DELAYED`, `FAILED`, `RETURNED`

## Database Strategy

Each service owns its own schema:

- `smartcourier_auth`
- `smartcourier_delivery`
- `smartcourier_tracking`
- `smartcourier_admin`

This is the correct microservice pattern because each bounded context controls its own data.

## Java Version

The project is configured for Java 21 to match your machine.

## Configuration Management Through Git

The project includes a real Config Server setup. You can run it in two ways:

1. `native` mode for local development
2. `git` mode when you are ready to point it to your own GitHub config repository

Templates for the external config repository are already created in [config-repo-template](C:\Users\vaish\Downloads\Smart_Courier\config-repo-template\README.md).

### Config repo files created for you

- [gateway-service.yml](C:\Users\vaish\Downloads\Smart_Courier\config-repo-template\gateway-service.yml)
- [auth-service.yml](C:\Users\vaish\Downloads\Smart_Courier\config-repo-template\auth-service.yml)
- [delivery-service.yml](C:\Users\vaish\Downloads\Smart_Courier\config-repo-template\delivery-service.yml)
- [tracking-service.yml](C:\Users\vaish\Downloads\Smart_Courier\config-repo-template\tracking-service.yml)
- [admin-service.yml](C:\Users\vaish\Downloads\Smart_Courier\config-repo-template\admin-service.yml)
- [config-server.yml](C:\Users\vaish\Downloads\Smart_Courier\config-repo-template\config-server.yml)
- [discovery-server.yml](C:\Users\vaish\Downloads\Smart_Courier\config-repo-template\discovery-server.yml)

### How to use your own GitHub config repository

1. Create a new GitHub repository for configuration.
2. Copy the template YAML files into that repository.
3. Start Docker Compose with `CONFIG_SERVER_PROFILE=git`.
4. Set `CONFIG_REPO_URI` to your GitHub repository URL.
5. If the repository is private, also set `CONFIG_REPO_USERNAME` and `CONFIG_REPO_PASSWORD`.

Until you do that, the Config Server still works locally in `native` mode using bundled fallback config files.

## Swagger URLs

- [Auth Swagger](http://localhost:8081/swagger-ui.html)
- [Delivery Swagger](http://localhost:8082/swagger-ui.html)
- [Tracking Swagger](http://localhost:8083/swagger-ui.html)
- [Admin Swagger](http://localhost:8084/swagger-ui.html)
- [Eureka Dashboard](http://localhost:8761)
- [Config Server Sample](http://localhost:8888/gateway-service/default)

## Gateway APIs

- `POST /gateway/auth/signup`
- `POST /gateway/auth/login`
- `GET /gateway/services`
- `POST /gateway/deliveries`
- `GET /gateway/deliveries/my`
- `GET /gateway/deliveries/{id}`
- `POST /gateway/tracking/documents/upload`
- `GET /gateway/tracking/{trackingNumber}`
- `GET /gateway/tracking/{id}/proof`
- `GET /gateway/admin/dashboard`
- `GET /gateway/admin/deliveries`
- `PUT /gateway/admin/deliveries/{id}/resolve`
- `GET /gateway/admin/reports`
- `GET /gateway/admin/users`
- `GET /gateway/admin/hubs`

## Run Locally

### Maven

```bash
mvn clean verify
```

### Spring Tool Suite

1. Import the root folder as an existing Maven project.
2. Let STS download dependencies and build the modules.
3. Start services in this order: `discovery-server`, `config-server`, `auth-service`, `delivery-service`, `tracking-service`, `admin-service`, `gateway-service`.
4. Open Swagger or Postman and test through the gateway or individual service ports.

### Docker Compose

```bash
docker compose up --build
```

To switch the config server from local fallback config to GitHub-backed config:

```bash
$env:CONFIG_SERVER_PROFILE="git"
$env:CONFIG_REPO_URI="https://github.com/<your-user>/<your-config-repo>.git"
docker compose up --build
```

## Default Ports

- Gateway: `8080`
- Auth: `8081`
- Delivery: `8082`
- Tracking: `8083`
- Admin: `8084`
- Config Server: `8888`
- Eureka: `8761`
- MySQL: `3306`
- RabbitMQ: `5672`
- RabbitMQ Management UI: `15672`

## Quality and SonarQube

JaCoCo is configured in the parent build and produces an aggregated report for SonarQube.

Generate verification and coverage:

```bash
mvn clean verify
```

Typical SonarQube command:

```bash
mvn clean verify sonar:sonar -Dsonar.projectKey=smart-courier
```

Expected JaCoCo XML report:

- [jacoco.xml](C:\Users\vaish\Downloads\Smart_Courier\target\site\jacoco-aggregate\jacoco.xml)

## Important Notes

- This is backend-only as requested.
- Swagger is included for endpoint testing.
- The project keeps local `application.yml` files so it remains runnable before you configure your GitHub config repo.
- The Git-based configuration model is already prepared; you only need to plug in your repository details.
