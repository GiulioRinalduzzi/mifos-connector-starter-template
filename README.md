# PH-EE Connector Starter

**This project is the baseline and gold-standard reference for the GSoC 2026 migration of 42 Mifos Payment Hub EE (PH-EE) connectors.** If you are contributing to that migration or building a new connector, start here.

---

## Why This Project Exists

Payment Hub EE is the gateway that connects financial institutions to real-time payment systems. Dozens of connectors (Mastercard CBS, bulk, channel, and others) plug into this hub. Over time, those connectors drifted onto different Java versions, different dependency versions, and—critically—different namespace worlds (`javax.`* vs `jakarta.`*). That makes upgrades risky and review harder.

**This starter is the shared foundation.** It defines how a PH-EE connector is structured, how it uses the Mifos Platform BOM for versions, how it integrates with Zeebe for orchestration, and how it stays on Jakarta EE 10 so that every connector speaks the same language. Using this template means your work fits the ecosystem from day one and is easier for maintainers and other contributors to understand.

---

## What You’ll Find Here

- **Package layout:** `org.mifos.connector.starter` — the same nesting pattern as reference connectors like [ph-ee-connector-mccbs](https://github.com/openMF/ph-ee-connector-mccbs). Your connector would use `org.mifos.connector.<your-connector-name>`.
- **Zeebe worker:** A sample job worker (`starter-worker`) that uses `@JobWorker` and `@Variable`, returns a `Map` of workflow variables, and logs in a consistent way. This mirrors the worker pattern used in production PH-EE connectors.
- **Camel route:** A small route that accepts JSON, validates it with Jakarta Bean Validation, and logs a “Payment Processed” style flow. Connectors can combine Zeebe-driven workflows with REST/Camel integration; this shows how both styles coexist.
- **BOM governance:** Dependencies are not versioned in this repo. They come from the **Mifos Platform BOM** so that all 42 connectors share one source of truth for versions and stay aligned.

---

## Governance: The BOM and Why We Don’t Pin Versions

In this project you will **not** see dependency versions hard-coded for Spring Boot, Camel, Zeebe, or Jakarta. Instead, the build uses:

```groovy
implementation enforcedPlatform('org.mifos.platform:mifos-platform-bom:1.0.0-SNAPSHOT')
```

That single line pulls in the curated set of versions that the Mifos platform team maintains. Every connector that uses the same BOM stays on the same stack. That avoids “configuration drift”: one connector on Spring Boot 3.2, another on 3.4, and a third still on `javax.*`. With the BOM, we fix versions in one place and every connector benefits.

We use `**enforcedPlatform**` (not just `platform`) so that the BOM’s versions win even when a transitive dependency asks for something else—for example an old `javax.*` library. That keeps the runtime firmly on Jakarta and avoids subtle classpath bugs.

**Working locally:** If you need to change or test against a new BOM, publish it from the BOM project with `./gradlew publishToMavenLocal`, then run `./gradlew build` in this repo (or your connector). The build uses `mavenLocal()` so it will pick up your snapshot.

---

## The Move from javax to jakarta (Why It Matters)

Older Java and Spring examples use `javax.validation.*`, `javax.persistence.*`, and `javax.servlet.*`. PH-EE and Spring Boot 3 use Jakarta EE 10, so all of that is now under `jakarta.*`. If you copy-paste code from an old tutorial or another project, you will see `javax` imports; for this codebase and any new connector work, replace them with `jakarta`. The validation API, persistence API, and servlet API all live under the `jakarta` namespace. Getting this right from the start avoids confusing runtime errors and keeps the 42-connector migration consistent.

---

## Skills That Help

- **Java:** Comfort with classes, methods, and basic APIs. This project uses Java 21.
- **Gradle:** Running `./gradlew build` and `./gradlew bootRun`; no need to install Gradle globally (the wrapper is included).
- **Apache Camel:** Basic idea of routes (from → process → to) and that Camel can unmarshal JSON and call validators and external systems.
- **Zeebe:** Understanding that workers are invoked by the workflow engine with variables and return variables; the sample worker in this repo shows the pattern.

You don’t need to be an expert. Use this repo as the reference; when you’re stuck, the community and docs are there to help.

---

## Project Layout

```text
src/
├── main/
│   ├── java/org/mifos/connector/starter/
│   │   ├── StarterApplication.java          # Spring Boot entry point
│   │   ├── ConnectorRouteBuilder.java       # Camel route: JSON -> validate -> log
│   │   ├── controller/
│   │   │   └── PaymentController.java       # REST endpoint /api/payments
│   │   ├── dto/
│   │   │   └── PaymentRequest.java          # DTO with Jakarta validation
│   │   └── worker/
│   │       └── StarterZeebeWorker.java     # Zeebe job worker (starter-worker)
│   └── resources/
│       └── application.yml                 # Spring Boot, Camel, Zeebe config
└── test/
    └── java/org/mifos/connector/starter/
        └── StarterApplicationTests.java    # Context load test

config/
└── checkstyle/
    └── checkstyle.xml                       # Code style rules
```

---

## Configuration

Environment variables are optional; each has a sensible default so you can `./gradlew bootRun` without setting anything. For real deployments, override them explicitly.


| Variable                      | Default           | Purpose                                            |
| ----------------------------- | ----------------- | -------------------------------------------------- |
| `SERVER_PORT`                 | `8080`            | HTTP port for the Spring Boot application.         |
| `ZEEBE_BROKER_CONTACTPOINT`   | `127.0.0.1:26500` | Address of the Zeebe gateway/broker.               |
| `ZEEBE_PLAINTEXT`             | `true`            | Whether the Zeebe client uses plaintext (no TLS).  |
| `ZEEBE_CLIENT_WORKER_THREADS` | `5`               | Number of Zeebe worker threads for this connector. |
| `LOGGING_LEVEL_ORG_MIFOS`     | `DEBUG`           | Log level for `org.mifos` classes.                 |
| `LOGGING_LEVEL_IO_CAMUNDA`    | `INFO`            | Log level for Zeebe/Camunda libraries.             |


These map directly to the `application.yml` keys and are the knobs you will most often tweak when running locally or in a container.

---

## Build and Run

**Prerequisites:** JDK 21, Git. Use the project’s Gradle wrapper; no global Gradle install required.

**Build (compile, test, Checkstyle, Spotless):**

```bash
./gradlew build
```

On Windows:

```cmd
gradlew.bat build
```

**Run:**

```bash
./gradlew bootRun
```

The app listens on port 8080 by default. To hit the sample Camel route:

```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{"transactionId":"tx-001","accountId":"acc-001","amount":100.50}'
```

You should get a “Payment Processed” style response and see the validated payment in the logs.

**Code style:** Run `./gradlew spotlessApply` to format code; `spotlessCheck` runs during `build`.

---

## Monitoring

This starter includes Spring Boot Actuator so you can check basic health and metrics locally and in CI. With the app running on port 8080:

- **Overall health:**

```bash
curl http://localhost:8080/actuator/health
```

- **Metrics index (list of available metrics):**

```bash
curl http://localhost:8080/actuator/metrics
```

- **Example metric (JVM memory used):**

```bash
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

These endpoints are intentionally minimal but match patterns used in PH-EE connectors: enough for basic observability without overwhelming a starter.

---

## Docker

For GSoC contributors, a common workflow is “fork, build, run in Docker.” This project includes a simple Dockerfile so you can containerize the starter quickly.

1. **Build the JAR (includes tests, Checkstyle, Spotless):**

```bash
./gradlew clean bootJar
```

1. **Build the Docker image:**

```bash
docker build -t ph-ee-connector-starter .
```

1. **Run the container with optional overrides:**

```bash
docker run --rm \
  -p 8080:8080 \
  -e SERVER_PORT=8080 \
  -e ZEEBE_BROKER_CONTACTPOINT=host.docker.internal:26500 \
  -e ZEEBE_PLAINTEXT=true \
  --name ph-ee-connector-starter \
  ph-ee-connector-starter
```

Once the container is running, you can:

- Call the REST endpoint as before at `http://localhost:8080/api/payments`.
- Hit the Actuator endpoints at `http://localhost:8080/actuator/health` and `http://localhost:8080/actuator/metrics`.

Treat this as the **“DNA” for the 42-connector migration**: when you add new connectors, reuse this Docker pattern so every PH-EE connector can be started, probed, and debugged in the same way.

---

## When You Need Help

- **Mifos Slack:** [https://bit.ly/mifos-slack](https://bit.ly/mifos-slack) — channels such as `#payment-hub`, `#fineract`, `#help`. This is where maintainers and contributors answer questions about PH-EE and connectors.
- **Apache Camel:** [https://camel.apache.org/docs/](https://camel.apache.org/docs/) — for route design and components.

As you work through the starter or your own connector, try things locally first; when you hit a wall, ask in Slack. That’s how the community expects to help.

---

## Using This as the Base for Your Connector

1. Copy this repository (or use it as a template) and rename the project/artifact to your connector (e.g. `ph-ee-connector-mccbs`).
2. Rename the base package from `org.mifos.connector.starter` to `org.mifos.connector.<your-connector-name>` and move sources accordingly.
3. Update `group` and `rootProject.name` in `build.gradle` and `settings.gradle`.
4. Replace the sample Camel route and Zeebe worker with your real integration logic, keeping the same patterns (Jakarta validation, BOM, worker variable handling).
5. Keep all imports on `jakarta.*` and rely on the Mifos Platform BOM for versions.

When your connector follows this structure and governance, it fits the GSoC 2026 migration and the wider PH-EE ecosystem and is ready for review.

---

## License

This project is licensed under the **Mozilla Public License 2.0 (MPL 2.0)**. See [LICENSE](LICENSE). Source files carry the standard Mifos Initiative copyright header (Copyright 2026 The Mifos Initiative).
