# Microservices-Based Student Enrollment System

Academic Spring Boot project based on the course project PDF. The system lets students enroll in courses by CNIE through a microservices architecture with an API Gateway as the single entry point.

## Main Features

- Student Service manages student identities: CNIE, first name, last name and email.
- Course Service manages the course catalog: title, description and credits.
- Enrollment Service links students to courses and applies the business rules.
- API Gateway exposes one frontend entry point and routes API calls to the services.
- Eureka Discovery Server lets services register and communicate by service name.
- Dashboard UI loads courses, lists a student's enrollments, creates enrollments and disables cancellation after 24 hours.

## Business Rules

- The student enters a CNIE and selects a course.
- Enrollment Service verifies that the student exists through Student Service.
- Enrollment Service verifies that the course exists through Course Service.
- A course cannot have more than 3 students enrolled.
- A student cannot enroll twice in the same course.
- An enrollment can be deleted only during the first 24 hours.
- Enrollment database stores only `studentId`, `courseId` and `enrollmentDate`.
- Final enrollment responses use a DTO that combines CNIE, course name, date and deletable status.

## Technologies

- Java 17 target
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Spring Cloud Gateway
- Eureka Service Discovery
- WebClient
- MySQL
- H2 for quick dev/demo profile
- OpenAPI/Swagger for service documentation
- HTML, CSS and JavaScript dashboard

## Project Structure

```text
student-enrollment-system
  api-gateway
  course-service
  discovery-server
  enrollment-service
  student-service
  database
  docs
  scripts
```

Each business service follows the course structure:

```text
src/main/java/com/demo/<service>
  controllers
  services
  repository
  entities
  dtos
  config
```

Enrollment Service also contains:

```text
clients
exceptions
```

## Run With H2 Dev Profile

This is the easiest way to present the project without configuring MySQL.

Open five terminals from the project root and run:

```powershell
mvn -pl discovery-server spring-boot:run
mvn -pl student-service spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl course-service spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl enrollment-service spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl api-gateway spring-boot:run
```

Or use:

```powershell
.\scripts\run-dev.ps1
```

To stop services started by the script:

```powershell
.\scripts\stop-dev.ps1
```

Then open:

```text
http://localhost:8080
```

Useful URLs:

```text
Eureka dashboard: http://localhost:8761
Student Swagger:  http://localhost:8081/swagger-ui.html
Course Swagger:   http://localhost:8082/swagger-ui.html
Enrollment Swagger: http://localhost:8083/swagger-ui.html
```

## Run With MySQL

Create the databases:

```sql
CREATE DATABASE IF NOT EXISTS student_service_db;
CREATE DATABASE IF NOT EXISTS course_service_db;
CREATE DATABASE IF NOT EXISTS enrollment_service_db;
```

Default credentials in the services:

```properties
spring.datasource.username=root
spring.datasource.password=root
```

Update the passwords in each service `application.properties` if your MySQL setup is different.

Then run the same five services without the `dev` profile.

## Sample Data

The services insert sample data automatically at startup if the tables are empty.

Recommended demo CNIE:

```text
CD2387
```

Sample course names:

```text
Spring Framework
Data Mining
Microservices Architecture
Advanced Java
```

## API Examples

Create enrollment:

```http
POST /api/enrollments
Content-Type: application/json

{
  "studentCnie": "CD2387",
  "courseId": 3
}
```

List enrollments:

```http
GET /api/enrollments/student/CD2387
```

Cancel enrollment:

```http
DELETE /api/enrollments/1?studentCnie=CD2387
```

## Notes About Course Alignment

The project follows the teacher's layered architecture: controllers receive HTTP requests, services contain business rules, repositories access the database, and DTOs define API contracts. The Enrollment Service is intentionally the business hub, exactly as described in the PDF. It uses WebClient to call Student and Course services and returns `EnrollmentResponseDTO` because the response combines data from multiple services.

## Screenshots To Add

- Eureka dashboard showing registered services.
- Main web dashboard with CNIE `CD2387`.
- Course list loaded from Course Service.
- Enrollments table showing one deletable and one locked enrollment.
- Swagger page for Enrollment Service.
- A successful POST request through the gateway.

## Author

Prepared by: `Your Name`
