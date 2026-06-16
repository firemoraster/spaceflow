# SpaceFlow

> Event-driven space booking platform — modular monolith built with Java 17 & Spring Boot 3.

[![Build](https://img.shields.io/badge/build-pending-lightgrey)](.)
[![Java 17](https://img.shields.io/badge/Java-17-orange)](.)
[![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.3-green)](.)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

Booking system for coworking spaces, meeting rooms and hot-desks. Demonstrates
hexagonal architecture, CQRS, the Transactional Outbox pattern and Kafka-based
eventual consistency inside a single deployable.

## Architecture

Package-by-module. Each module (`booking`, `resource`, `notification`) is a
hexagonal slice; modules talk only through domain events or published ports.

```
com.spaceflow
├── booking/        domain · application(ports) · adapters(in/web, out/persistence)
├── resource/       (planned)
├── notification/   Kafka consumer (planned)
└── shared/         security · exception · config
```

## Tech stack

| Area        | Tech                                            |
|-------------|-------------------------------------------------|
| Language    | Java 17 (records, pattern matching, Streams)    |
| Framework   | Spring Boot 3.3 (Web, Data JPA, Security)       |
| Persistence | PostgreSQL + Flyway                             |
| Read-model  | Redis                                           |
| Messaging   | Apache Kafka (Spring Kafka)                     |
| Security    | OAuth2 login + JWT resource server             |
| Docs        | OpenAPI 3 / Swagger UI                          |
| Observability | Actuator + Micrometer + Prometheus + Grafana |
| Testing     | JUnit 5 + Testcontainers                        |
| CI/CD       | GitHub Actions                                  |

## Quick start

```bash
cp .env.example .env          # fill in secrets
docker compose up -d          # postgres, redis, kafka, prometheus, grafana (planned)
./mvnw spring-boot:run        # or run SpaceFlowApplication from the IDE
```

- Swagger UI: http://localhost:8080/swagger-ui
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/prometheus

## Testing

```bash
./mvnw verify                 # unit + Testcontainers integration tests
```

## Roadmap

- [x] Project skeleton, hexagonal booking slice, schema (Flyway)
- [ ] Transactional Outbox + Kafka publisher
- [ ] CQRS read-model projection in Redis
- [ ] Idempotent consumer + notification module
- [ ] docker-compose (full infra) + Dockerfile
- [ ] GitHub Actions CI + Grafana dashboards

## License

MIT
