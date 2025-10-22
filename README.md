# GitHub REST Automation

[![CI](https://github.com/ravejoy/github-rest-automation/actions/workflows/ci.yml/badge.svg)](https://github.com/ravejoy/github-rest-automation/actions/workflows/ci.yml)
[![Coverage](https://img.shields.io/badge/Coverage-JaCoCo-green.svg)](https://github.com/ravejoy/github-rest-automation/actions/workflows/ci.yml)
[![Allure Report](https://img.shields.io/badge/Allure-Report-blue)](https://ravejoy.github.io/github-rest-automation/)
[![Sonar Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=ravejoy_github-rest-automation&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ravejoy_github-rest-automation)

---

### Project Status

This is an **in-progress automation framework** under active development.  
A clean, modular API testing framework with CI/CD integration and code quality setup.

---

### Overview

A Java 21 + Gradle + RestAssured + JUnit5 framework for GitHub REST API testing.

The project emphasizes:

- composition-based design
- fail-fast configuration via `AppConfig`
- retryable HTTP executor with exponential backoff
- masked HTTP logging
- CI-ready setup (Spotless, JaCoCo, SonarCloud, Allure)

---

### Structure

```
src/
 ├─ main/java/com/ravejoy/github/
 │   ├─ api/       → domain clients (e.g. UsersClient)
 │   ├─ http/      → executor, filters, status codes
 │   └─ config/    → config loader, env providers
 ├─ test/java/com/ravejoy/github/
 │   ├─ api/       → smoke & integration tests
 │   └─ infra/     → http/config unit tests
 └─ testFixtures/java/com/ravejoy/github/
     ├─ annotations/ → @ApiSmoke, @InfraUnit
     ├─ support/     → TestConfig, fixtures, helpers
```

---

### CI & Reports

- **Main workflow:** [ci.yml](.github/workflows/ci.yml)
- **Nightly report:** [Allure GitHub Pages](https://ravejoy.github.io/github-rest-automation/)
- **Quality gate:** [SonarCloud Dashboard](https://sonarcloud.io/summary/new_code?id=ravejoy_github-rest-automation)

---

### Quick Run

```bash
./gradlew spotlessApply clean test
```

Local Allure report:

```bash
./gradlew allureReport
./gradlew allureServe
```

---

### Stack

| Tool          | Version |
| ------------- | ------- |
| Java          | 21      |
| Gradle        | 8.10.2  |
| RestAssured   | 5.5.0   |
| JUnit 5       | 5.11    |
| Allure Junit5 | 2.29    |
| JaCoCo        | 0.8.12  |
| SonarCloud    | latest  |
| Spotless      | 6.25    |
| MockWebServer | 4.12    |

---

### License

[MIT License](LICENSE) © 2025 Nazarii Tsyhaniuk
