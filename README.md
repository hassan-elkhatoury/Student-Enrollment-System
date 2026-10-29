# Microservices-Based Student Enrollment System

A distributed student enrollment system built with Spring Boot and Spring Cloud. The system manages student records, course catalogs, and enrollment processing through dedicated microservices orchestrated by an API Gateway and Eureka Service Discovery.

---

## Architecture Overview

```
                      +-----------------------------+
                      |   Web Browser / Dashboard   |
                      |    http://localhost:8080    |
                      +--------------+--------------+
                                     |
                                     v
                      +-----------------------------+
                      |     API Gateway (8080)      |
                      +--------------+--------------+
                                     |
           +-------------------------+-------------------------+
           |                         |                         |
           v                         v                         v
+--------------------+    +--------------------+    +--------------------+
|  Student Service   |    |   Course Service   |    | Enrollment Service |
|       (8081)       |    |       (8082)       |    |       (8083)       |
+--------------------+    +--------------------+    +---------+----------+
           ^                         ^                        |
           |                         +------------------------+ (WebClient)
           +--------------------------------------------------+
                                     |
                      +-----------------------------+
                      | Eureka Discovery Server     |
                      |    http://localhost:8761    |
                      +-----------------------------+
```

### Microservices

1. **Discovery Server (`discovery-server`)**: Netflix Eureka registry on port `8761`.
2. **API Gateway (`api-gateway`)**: Spring Cloud Gateway routing on port `8080` with built-in dashboard UI.
3. **Student Service (`student-service`)**: Student management and CNIE records on port `8081`.
4. **Course Service (`course-service`)**: Course catalog and credit management on port `8082`.
5. **Enrollment Service (`enrollment-service`)**: Business logic orchestrator and enrollment rules on port `8083`.

---

## Business Rules

- **Identity Verification**: Students enroll using their Moroccan CNIE; verified against Student Service.
- **Capacity Constraint**: Maximum 3 students per course.
- **Duplicate Prevention**: A student cannot enroll in the same course more than once.
- **24-Hour Cancellation**: Enrollments can only be cancelled within 24 hours of creation.

---

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.3.5, Spring Cloud 2023
- **Communication**: Spring Cloud Gateway, Netflix Eureka, Spring WebClient
- **Data & Persistence**: Spring Data JPA, H2 Database (Dev), MySQL (Prod)
- **Documentation**: OpenAPI / Swagger UI
- **Frontend**: HTML5, CSS3, Vanilla JavaScript

---

## Project Structure

```text
student-enrollment-system/
├── api-gateway/              # Cloud Gateway & Dashboard UI
├── course-service/           # Course catalog microservice
├── discovery-server/         # Eureka discovery server
├── enrollment-service/       # Enrollment business logic
├── student-service/          # Student management microservice
├── database/                 # SQL schemas and scripts
├── scripts/                  # Automation scripts (run-dev, stop-dev)
└── pom.xml                   # Root Maven parent POM
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+ *(Optional: default profile runs with embedded H2)*

### Option 1: Automated Start (PowerShell)

```powershell
cd student-enrollment-system
.\scripts\run-dev.ps1
```

To stop all background services:

```powershell
.\scripts\stop-dev.ps1
```

### Option 2: Manual Start (Terminal)

Open separate terminals in `student-enrollment-system/` and run:

```powershell
mvn -pl discovery-server spring-boot:run
mvn -pl student-service spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl course-service spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl enrollment-service spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl api-gateway spring-boot:run
```

---

## Service URLs & Endpoints

| Component | URL | Description |
|---|---|---|
| **Dashboard UI** | `http://localhost:8080` | Web application interface |
| **Eureka Server** | `http://localhost:8761` | Microservice registry dashboard |
| **Student Swagger** | `http://localhost:8081/swagger-ui.html` | Student Service OpenAPI spec |
| **Course Swagger** | `http://localhost:8082/swagger-ui.html` | Course Service OpenAPI spec |
| **Enrollment Swagger** | `http://localhost:8083/swagger-ui.html` | Enrollment Service OpenAPI spec |

### Demo Credentials & Sample Data

- **Sample Student CNIE**: `CD2387`
- **Default Database Credentials** *(MySQL)*: `root` / `root`
