# Mifos Connector Starter Template

A modern, professional starter template for building **Mifos Payment Hub (PH-EE) connectors**. This project serves as the reference architecture for contributors migrating and developing the 42 Mifos connectors, aligning with GSBX-29 "Example Starter Story" and establishing standards for the GSoC migration effort.

## Tech Stack

- **Java 21**
- **Gradle 8.5** (Groovy DSL)
- **Spring Boot 3.4+**
- **Apache Camel 4.x**
- **Jakarta EE 10** (validation, persistence, annotations)

## Project Structure

```
src/
├── main/
│   ├── java/org/apache/fineract/cn/connector/starter/
│   │   ├── StarterApplication.java          # Main entry point
│   │   ├── ConnectorRouteBuilder.java       # Camel payment route
│   │   ├── controller/
│   │   │   └── PaymentController.java       # REST endpoint
│   │   └── dto/
│   │       └── PaymentRequest.java          # Jakarta-validated DTO
│   └── resources/
│       └── application.yml
└── test/
    └── java/org/apache/fineract/cn/connector/starter/
        └── StarterApplicationTests.java
config/
└── checkstyle/
    └── checkstyle.xml                       # Mifos coding standards
```

## Skills Needed

To work effectively with this template and contribute to Mifos connectors, you should be familiar with:

| Skill | Purpose |
|-------|---------|
| **Java** | Core language; Java 21 features (records, pattern matching, virtual threads) are encouraged |
| **Apache Camel** | Route definitions, EIPs, component integration, and message processing |
| **Gradle** | Build automation, dependency management, Spotless and Checkstyle plugins |

Additional helpful skills: Spring Boot, REST APIs, JSON processing, and basic understanding of payment/financial systems.

## Sources of Information

- **Mifos Community**: [Mifos Slack](https://bit.ly/mifos-slack) — channels such as `#payment-hub`, `#fineract`, `#help`
- **Apache Camel**: [Camel Documentation](https://camel.apache.org/docs/)
- **Jakarta EE 10 Migration**: [Migrating javax to jakarta (JetBrains)](https://www.jetbrains.com/guide/java/tutorials/migrating-javax-jakarta/), [OpenRewrite Jakarta EE 10 Recipe](https://docs.openrewrite.org/running-recipes/popular-recipe-guides/migrate-to-jakarta-ee-10)

## Developer Guide

### Prerequisites

- **JDK 21**
- **Gradle 8.5+** (or use the project Gradle wrapper)

### Build

From the project root:

```bash
./gradlew build
```

On Windows:

```cmd
gradlew.bat build
```

This compiles the project, runs tests, applies Checkstyle, and runs Spotless checks.

### Run

```bash
./gradlew bootRun
```

The application starts on port 8080. You can exercise the payment route via the REST API:

```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{"transactionId":"tx-001","accountId":"acc-001","amount":100.50}'
```

On success, the response is `Payment Processed` and the route logs the validated payment details.

### Code Quality

- **Spotless**: `./gradlew spotlessApply` — formats code; `./gradlew spotlessCheck` — checks formatting
- **Checkstyle**: Runs during `./gradlew build`; configure rules in `config/checkstyle/checkstyle.xml`

## Migration Note: `jakarta.*` vs `javax.*`

This template uses **Jakarta EE 10** namespaces (`jakarta.validation`, `jakarta.persistence`, etc.) instead of the legacy **Java EE** namespaces (`javax.*`).

**Why?**
Oracle transferred stewardship of Java EE to the Eclipse Foundation, and the project was rebranded to Jakarta EE. Because Oracle restricts the use of the `javax` namespace, new specifications under Jakarta EE use the `jakarta` package prefix. Spring Boot 3.x and Apache Camel 4.x require Jakarta EE 9+ and therefore use `jakarta.*` imports.

**Impact for contributors:**

- Replace `javax.validation.*` with `jakarta.validation.*`
- Replace `javax.persistence.*` with `jakarta.persistence.*`
- Replace `javax.servlet.*` with `jakarta.servlet.*`
- Similar mappings apply for JAX-RS, CDI, and other Jakarta specifications.

Migration can be automated with tools such as [OpenRewrite](https://docs.openrewrite.org/running-recipes/popular-recipe-guides/migrate-to-jakarta-ee-10) or IDE refactoring support.

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.
