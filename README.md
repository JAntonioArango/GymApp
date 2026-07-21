# 🏋️ GYM TASK MICROSERVICES 🏋️

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.0-green)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![JWT](https://img.shields.io/badge/JWT-Security-orange)
![Prometheus](https://img.shields.io/badge/Prometheus-Monitoring-red)
![Grafana](https://img.shields.io/badge/Grafana-11-orange)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue)
![Maven](https://img.shields.io/badge/Maven-3.9.x-blue)
![JUnit](https://img.shields.io/badge/JUnit-5-green)
![Cucumber](https://img.shields.io/badge/Cucumber-BDD-brightgreen)
![SonarQube](https://img.shields.io/badge/SonarQube-10-blue)

A comprehensive microservices-based gym management system built with Spring Boot and Spring Cloud. </br>
The application provides complete trainer and trainee management, training sessions, authentication, and monitoring capabilities across distributed services.

## 🏗️ Architecture Overview

This system follows a microservices architecture pattern with:
- **Service Discovery** (Eureka)
- **Circuit Breaker** patterns for resilience (Resilience4j)
- **Asynchronous Messaging** for event-driven communication
- **Centralized Monitoring** and observability

> Only the main app and `eureka-server/` (service discovery) are implemented in this repository. An API Gateway and Config Server are not part of the current codebase.

---

## 🗝 Key Features 🗝

| Area                              | Endpoints / Use-Cases                                                                                                      |
| --------------------------------- |----------------------------------------------------------------------------------------------------------------------------|
| **Account Creation**              | Trainer & Trainee registration with auto-generated, BCrypt-hashed credentials, assigned a `TRAINER`/`TRAINEE` role         |
| **Authentication**                | Username + Password login → JWT issued (role embedded as a claim) · Change Password · Brute-force protector (3 fails → 5-min DB-backed lock) |
| **Authorization**                 | Role-based access control (`TRAINER`/`TRAINEE`) plus method-level ownership checks (`@PreAuthorize`) on most profile/training endpoints |
| **Logout / Token Revocation**     | `POST /api/v1/auth/logout` black-lists the JWT (token + exp stored in DB) so it can't be reused                            |
| **Profile Management**            | Retrieve, update, activate/deactivate & delete profiles with validation and optimistic locking                             |
| **Training Management**           | CRUD trainings, list & filter by date-range, trainer/trainee name, and training type                                       |
| **Trainer ⇄ Trainee assignments** | List unassigned active trainers · Atomic update of trainee's trainer list                                                  |
| **Monitoring & Health**           | Spring Actuator endpoints `/ops/prometheus`, `/ops/gym-health` exposed for Prometheus                                      |
| **REST & Exception Handling**     | Global exception handler, consistent RFC 7807 (Problem-Details) responses                                                  |
| **Documentation**                 | Swagger / OpenAPI with DTO validation examples & API metadata                                                              |
| **Testing & Coverage**            | JUnit 5, Mockito, Cucumber BDD; JaCoCo gate ≥ 80 % (uploaded to SonarQube)                                                 |
| **Code Quality**                  | SonarQube static analysis; Spotless plugin with Google Java Format                                                         |
| **Logging**                       | Console logs, transaction-ID filter, detailed REST call logging                                                            |
| **Asynchronous Messaging**       | Event-driven communication between services with message queues, event publishing & dead letter queue for invalid messages |
| **Microservices**                | Service Discovery (Eureka) · Circuit Breakers                                                |
| **Deployment**                    | Docker Compose stack: App, MySQL, Prometheus, Grafana, SonarQube – credentials via `.env`                                  |

---

## ⚙ Tech Stack ⚙

| Layer             | Technology                                                               |
| ----------------- | ------------------------------------------------------------------------ |
| **Runtime**          | Java 21, Spring Boot 3.2.0                                               |
| **Microservices**    | Spring Cloud 2023.0.0** · Eureka** · Circuit Breaker |
| **Messaging**        | Spring AMQP · Event-driven architecture · Dead Letter Queue                            |
| **Security**         | Spring Security 6 · OAuth2 Resource-Server (JWT) · Role-based method security (`@PreAuthorize`) · BCrypt · CORS allowlist · HSTS |
| **Persistence**       | Spring Data JPA / Hibernate 6 · MySQL 8**                                |
| **Monitoring**        | Spring Boot Actuator · Micrometer** · Prometheus v2** · Grafana 11      |
| **Build**             | Maven 3.9.x** (wrapper) · Dockerfile / Docker Compose                    |
| **Testing**           | JUnit 5 · Mockito · Spring Boot Test · JaCoCo · MethodName_Scenario_ExpectedBehavior naming |
| **Documentation**     | Swagger / OpenAPI 3.1 (springdoc-openapi)                                |
| **Utilities**         | Lombok · Jackson · Apache Commons Lang                                   |
| **Quality & Linting** | SonarQube 10 · Spotless (google-java-format)                             |

---

## 🔗 URLS 🔗

Your App: http://localhost:8080 <br>

Eureka Dashboard: http://localhost:8762 <br>

Artemis ActiveMQ: http://localhost:8161/console/artemis <br>

Swagger UI: http://localhost:8080/swagger-ui/index.html <br>

Prometheus: http://localhost:9090 <br>

Grafana: http://localhost:3000 <br>

SonarQube: http://localhost:9000 <br>

---

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 21+ (for local development)
- Maven 3.9+ (for local development)

### Run with Docker Compose
```bash
# Start all services
docker compose up -d --build

# Start specific service
docker compose up -d --build app

# View logs
docker compose logs -f app

# Stop services
docker compose down
```

## 📊 Service Endpoints

| Service | Port | Health Check |
|---------|------|-----------------|
| Main App | 8080 | `/ops/gym-health` |
| Eureka Server | 8762 | `/actuator/health` |

## 🔧 Configuration

Services are configured via:
- `application.yml` - Base configuration
- `application-dev.yml` / `application-prod.yml` - Profile-specific overrides
- `.env` - Environment variables for Docker Compose and local runs (gitignored — create your own at the repo root, never commit it)

TLS is expected to terminate at an external reverse proxy/load balancer in front of the app — the app itself does not serve HTTPS directly. `server.forward-headers-strategy: framework` is set so Spring Security can correctly derive the original request scheme from `X-Forwarded-*` headers (required for HSTS and secure-cookie behavior behind that proxy).

### `.env` — required to build/run

| Variable | Consumed by | Notes |
| --- | --- | --- |
| `MYSQL_ROOT_PASSWORD` | `db` container | MySQL root password on first init |
| `MYSQL_DATABASE` | `db` container | Database created on first boot (`gymdb` by convention) |
| `MYSQL_USER` | `db` container, `app` container | Compose also forwards this as `SPRING_DATASOURCE_USERNAME` for the app |
| `MYSQL_PASSWORD` | `db` container, `app` container | Compose also forwards this as `SPRING_DATASOURCE_PASSWORD` for the app |
| `JWT_SECRET` | app | **No default** — `jwt.secret: ${JWT_SECRET}` — the app refuses to start without it |

### `.env` — optional (sensible defaults)

| Variable | Default | Purpose |
| --- | --- | --- |
| `JWT_EXPIRES_MINUTES` | `30` | JWT expiration window (minutes) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:5173` | Comma-separated allowlist of origins permitted for CORS — no wildcard is supported |
| `EUREKA_SERVER_URL` | `http://eureka-server:8761/eureka` | Only relevant if pointing the app at a Eureka instance other than the bundled `eureka-server` container |
| `HOSTNAME` | `app` | Eureka instance hostname advertised by this service |

### `.env` — only needed for local (non-Docker) runs

`docker-compose.yml` hardcodes the datasource/broker connection details for the `app` container (pointing at the `db`/`artemis` service names on the Compose network), so these only matter when running the app directly via `mvn spring-boot:run` / your IDE against services exposed on `localhost`:

| Variable | Example |
| --- | --- |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3307/gymdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
| `SPRING_DATASOURCE_USERNAME` | same value as `MYSQL_USER` |
| `SPRING_DATASOURCE_PASSWORD` | same value as `MYSQL_PASSWORD` |
| `SPRING_ARTEMIS_BROKER_URL` | `tcp://localhost:61616` |
| `SPRING_ARTEMIS_USER` | `artemis` |
| `SPRING_ARTEMIS_PASSWORD` | `artemis` |

### `.env` — test / tooling (not required to run the app)

| Variable | Consumed by | Notes |
| --- | --- | --- |
| `JWT_SECRET_TEST` | `IntegrationTest` Cucumber suite | Falls back to `test-secret-key` in most integration test config, but is required with no fallback when the `integration-tests` Maven profile wires it through `IntegrationTestConfiguration` |
| `SONAR_TOKEN` | `.githooks/pre-commit` | Sonar login token for the local pre-commit hook's `sonar:sonar` scan — must be exported in your shell (e.g. `set -a; source .env; set +a`), the hook doesn't read `.env` itself |

> ⚠️ Artemis broker credentials (`ARTEMIS_USER`/`ARTEMIS_PASSWORD`, hardcoded to `artemis`/`artemis`) and Grafana's default admin login are **not** sourced from `.env` in `docker-compose.yml` — don't expose ports `8161` (Artemis console), `61616` (Artemis broker), or `3000` (Grafana) outside a trusted network without changing these first.

## 📈 Monitoring & Observability

- **Metrics**: Micrometer + Prometheus integration
- **Health Checks**: Spring Actuator endpoints
- **Distributed Tracing**: Request correlation IDs
- **Dashboards**: Grafana with pre-configured panels

## 🧪 Testing

```bash
# Unit tests + component BDD suite (default Surefire run, excludes IntegrationTest)
mvn clean test

# Only unit tests
mvn test -Dtest="*Test"

# Only the component Cucumber suite (features/component/*.feature)
mvn test -Dtest="ComponentTest"

# Only the integration Cucumber suite (features/integration/*.feature) —
# requires the "integration-tests" profile, which swaps Surefire's includes/excludes
mvn test -P integration-tests -Dtest="IntegrationTest"

# Single test class / method
mvn test -Dtest="TrainerServiceTest"
mvn test -Dtest="TrainerServiceTest#methodName_scenario_expectedBehavior"

# Coverage gate (80% line coverage, runs JaCoCo's check goal)
mvn verify
```

`mvn clean test` alone never runs `IntegrationTest` — CI must invoke both the default run and the `integration-tests` profile to get full coverage.
<style>
  h1 { color: rgba(0,178,255,0.9); }
  h2 { color: #60c5db; }
  p  { color: rgb(255,255,255); }
</style>