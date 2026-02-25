# Mifos Connector Starter – Onboarding Guide

This repository is a **starter-level** project for building Mifos Payment Hub EE (PH-EE) connectors.
It is the **reference template for the GSoC 2026 migration of 42 connectors**.

If you want to contribute a connector during GSoC (or later), **copy this structure** and adapt it.
That way your code:

- Uses **Java 21**.
- Builds with **Gradle**.
- Uses the correct **Jakarta EE 10 `jakarta.*` namespaces** instead of the old `javax.*` ones.

---

## 1. What you need to know first

- **Primary language**: Java 21  
  You should be comfortable writing basic classes, methods, and working with JSON/REST style APIs.

- **Build tool**: Gradle (via the included `gradlew` / `gradlew.bat`)  
  You do not need a global Gradle install; the wrapper in this repo is enough.

Everything else (Spring Boot, Camel, validation) is layered on top of those two.

---

## 2. How this starter fits into GSoC 2026

For GSoC 2026, the Mifos community is migrating **42 existing PH-EE connectors** to:

- Java 21
- Spring Boot 3
- Apache Camel 4
- Jakarta EE 10 (`jakarta.*`)

This project is the **baseline**:

- **If you are starting a new connector**: clone this repo, rename the package and artifact, then plug in your own routes and integration code.
- **If you are migrating an old connector**: use this as a reference for project layout, dependencies, and Jakarta imports.

> **In short**: if your connector looks like this template, reviewers can immediately see that you are using the right stack and namespaces.

---

## 3. Key skills (GSBX-29)

You do not need to be an expert. For this starter, focus on these three:

- **Java (basics)**  
  - Classes, interfaces, methods, packages.  
  - Reading and writing simple POJOs (like `PaymentRequest`).

- **Apache Camel (routing)**  
  - Understand a simple route: **from → process → to**.  
  - Know that Camel can unmarshal JSON, validate objects, and call external systems.

- **Gradle (running commands)**  
  - `./gradlew build` and `./gradlew bootRun`.  
  - Running tests and formatting/lint tasks when asked by the project.

If you are comfortable with those three, you are ready to work with this starter.

---

## 4. Project layout (what lives where)

```text
src/
├── main/
│   ├── java/org/apache/fineract/cn/connector/starter/
│   │   ├── StarterApplication.java          # Spring Boot entry point
│   │   ├── ConnectorRouteBuilder.java       # Camel route: JSON -> validate -> log
│   │   ├── controller/
│   │   │   └── PaymentController.java       # REST endpoint /api/payments
│   │   └── dto/
│   │       └── PaymentRequest.java          # DTO with Jakarta validation
│   └── resources/
│       └── application.yml                  # Spring Boot + Camel config
└── test/
    └── java/org/apache/fineract/cn/connector/starter/
        └── StarterApplicationTests.java     # Simple context load test

config/
└── checkstyle/
    └── checkstyle.xml                       # Code style rules
```

When you create a new connector, keep the same structure but change the package and project name as needed.

---

## 5. How to build and run

### Prerequisites

- JDK **21**
- Git

You do **not** need Gradle installed; use the wrapper.

### Build

From the project root:

```bash
./gradlew build
```

On Windows:

```cmd
gradlew.bat build
```

This will:

- Compile the Java code.
- Run tests.
- Run Checkstyle and Spotless checks for formatting.

### Run locally

```bash
./gradlew bootRun
```

The application starts on port `8080`.

Test the sample payment route:

```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{"transactionId":"tx-001","accountId":"acc-001","amount":100.50}'
```

You should get:

```text
Payment Processed
```

In the logs you will see the validated payment data passing through the Camel route.

---

## 6. When you get stuck: where people actually go

In practice, Mifos developers mostly use **two places** when they are blocked:

- **Mifos Slack**  
  - Link: [https://bit.ly/mifos-slack](https://bit.ly/mifos-slack)  
  - Typical channels: `#payment-hub`, `#fineract`, `#help`.  
  - This is where you can ask things like *“My connector route is failing validation, what am I doing wrong?”* and get feedback from maintainers and other contributors.

- **Apache Camel documentation**  
  - Link: [https://camel.apache.org/docs/](https://camel.apache.org/docs/)  
  - Useful sections: components (for specific connectors), “Camel with Spring Boot”, and examples of routes.  
  - When you are not sure how to write a route or configure a component, this is the first place to look.

Feel free to start from this README, try something, and then ask questions in Slack when you hit a wall—that is normal and expected.

---

## 7. Jakarta vs javax – important tip

You will see a lot of old Java examples online using `javax.*` imports.

> **Tip from a developer:** **Don’t use `javax` anymore; Spring Boot 3 needs `jakarta`.**

For this project (and all new connectors), use:

- `jakarta.validation.*` instead of `javax.validation.*`
- `jakarta.persistence.*` instead of `javax.persistence.*`
- `jakarta.servlet.*` instead of `javax.servlet.*`

If you paste code from a blog or Stack Overflow and it uses `javax`, fix the imports before you commit.  
This is one of the most common migration mistakes.

---

## 8. Next steps for your own connector

If you are starting a new connector based on this starter:

1. **Copy the project** into a new folder / repository.
2. **Rename the package** `org.apache.fineract.cn.connector.starter` to match your connector name.
3. **Adjust the Gradle coordinates** (`group`, `version`, project name) in `build.gradle` and `settings.gradle`.
4. **Replace the sample route** in `ConnectorRouteBuilder` with the flows your connector actually needs (HTTP calls, JMS, database, etc.).
5. **Keep using Jakarta imports** and Gradle tasks from this README.

Once you do that, you have a connector that looks like the rest of the GSoC 2026 work and is much easier for reviewers to follow.

