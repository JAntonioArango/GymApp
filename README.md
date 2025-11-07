# 🏋️ GYM TASK MICROSERVICES 🏋️

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.x-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.x-green)
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
- **API Gateway** for routing and load balancing
- **Distributed Configuration** management
- **Circuit Breaker** patterns for resilience
- **Asynchronous Messaging** for event-driven communication
- **Centralized Monitoring** and observability

---

## 🗝 Key Features 🗝

| Area                              | Endpoints / Use-Cases                                                                                                      |
| --------------------------------- |----------------------------------------------------------------------------------------------------------------------------|
| **Account Creation**              | Trainer & Trainee registration with auto-generated, BCrypt-hashed credentials                                              |
| **Authentication**                | Username + Password login → JWT issued · Change Password · Brute-force protector (3 fails → 5-min lock)                    |
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
| **Microservices**                | Service Discovery (Eureka) · API Gateway · Config Server · Circuit Breakers                                                |
| **Deployment**                    | Docker Compose stack: App, MySQL, Prometheus, Grafana, SonarQube – credentials via `.env`                                  |

---

## ⚙ Tech Stack ⚙

| Layer             | Technology                                                               |
| ----------------- | ------------------------------------------------------------------------ |
| **Runtime**          | Java 21, Spring Boot 3.4.x                                               |
| **Microservices**    | Spring Cloud 2024.x** · Eureka** · Gateway · Config Server · Circuit Breaker |
| **Messaging**        | Spring AMQP · Event-driven architecture · Dead Letter Queue                            |
| **Security**         | Spring Security 6 · OAuth2 Resource-Server (JWT) · BCrypt · Caffeine cache |
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
| API Gateway | 8081 | `/actuator/health` |

## 🔧 Configuration

Services are configured via:
- `application.yml` - Base configuration
- `application-docker.yml` - Docker-specific overrides
- `.env` - Environment variables for Docker Compose

## 📈 Monitoring & Observability

- **Metrics**: Micrometer + Prometheus integration
- **Health Checks**: Spring Actuator endpoints
- **Distributed Tracing**: Request correlation IDs
- **Dashboards**: Grafana with pre-configured panels

## 🧪 Testing

```bash
# Run all tests (unit + integration + BDD)
mvn clean test

# Run only unit tests
mvn test -Dtest="*Test"

# Run only integration tests  
mvn test -Dtest="*IT"

# Run only Cucumber BDD tests
mvn test -Dtest="IntegrationTest,ComponentTest"
```
<style>
  h1 { color: rgba(0,178,255,0.9); }
  h2 { color: #60c5db; }
  p  { color: rgb(255,255,255); }
</style>