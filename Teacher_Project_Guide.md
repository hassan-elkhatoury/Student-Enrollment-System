# Teacher Project Guide

This note summarizes the project style implied by the course PDFs in this folder.

## Big Picture

The course moves from low-level Java web concepts to Spring Boot:

- Java OOP fundamentals: classes, interfaces, inheritance, composition, lambdas.
- HTTP fundamentals: stateless request/response, methods, status codes, headers, cookies, sessions, JWT idea.
- Jakarta Servlet internals: Tomcat connector, context, wrapper, servlet lifecycle, `init()`, `service()`, `doGet()`, `doPost()`, `destroy()`.
- Filters and listeners: authentication/logging/encoding/rate-limit filters; session/context/request listeners.
- MVC and JSP/Thymeleaf: controllers prepare data, views display it, business logic stays out of views.
- Spring MVC and DI: replace manual `new` coupling with IoC-managed `@Controller`, `@Service`, `@Repository`.
- Data access: JPA specification, Hibernate implementation, Spring Data JPA repositories.
- Spring Boot: auto-configuration, embedded Tomcat, starters, package scanning, `application.properties`.
- REST APIs and project structure: JSON contracts, DTOs, shared service/repository layers, microservice-style boundaries.

## Preferred Final Project Shape

For a modern project, use Spring Boot rather than manual Servlet/JSP unless explicitly asked otherwise.

Recommended package structure:

```text
src/main/java/com/demo/<app>
  controllers/
  services/
  repository/
  models/ or entities/
  dtos/
  clients/        # only if calling other services
  config/
  aop/            # only if cross-cutting behavior is needed
```

Keep the main `@SpringBootApplication` class above these packages so component scanning sees controllers, services, repositories, and entities.

## Layer Rules

- Controller layer:
  - Handles HTTP routing only.
  - Uses `@Controller` for server-rendered pages.
  - Uses `@RestController` for JSON APIs.
  - Receives request params/path variables/body data.
  - Delegates work to services.
  - Returns view names, redirects, or JSON responses.

- Service layer:
  - Contains business rules.
  - Checks constraints and validation that belong to the app domain.
  - Coordinates repositories and external service clients.
  - Uses `@Transactional` for multi-step database work or lazy-loading boundaries.

- Repository layer:
  - Talks to the database only.
  - Prefer `interface XRepository extends JpaRepository<Entity, IdType>`.
  - Use derived methods such as `findByEmail`, `findByCnie`, `countByCourseId` when names match entity fields exactly.

- Entity/model layer:
  - Uses `@Entity`, `@Table`, `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
  - Must include a no-args constructor for Hibernate.
  - Fields should map clearly to database columns.

- DTO layer:
  - Use DTOs when API responses combine data, hide internal entity structure, or cross service boundaries.
  - Especially important in the enrollment microservice example.

## Database Expectations

Typical Spring Boot config:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/com_demo_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

The teacher repeatedly explains that `ddl-auto=update` lets Hibernate create or update tables from entity metadata.

## Dependencies

For Spring Boot projects, prefer starters without explicit versions because the Spring Boot parent manages them:

- Spring Web
- Spring Data JPA
- MySQL Driver
- Thymeleaf if server-rendered views are needed
- Springdoc OpenAPI if API contract documentation is needed

For non-Boot Spring MVC, the teacher emphasizes manual `pom.xml`, `web.xml`, and `spring-servlet.xml`, but Spring Boot is the cleaner final direction.

## REST API Style

The teacher distinguishes:

- `@Controller`: returns pages such as Thymeleaf/JSP.
- `@RestController`: returns JSON/XML data, not views.

Recommended REST contract style:

- `GET /api/resources` to list data.
- `POST /api/resources` to create data.
- Use JSON with clear field names and types.
- Document endpoints with Swagger/OpenAPI when useful.

Hybrid apps are acceptable:

- A view controller serves an empty or form page.
- JavaScript fetches JSON from a REST controller.
- Both controllers reuse the same service and repository layers.

## Validation And Forms

For server-rendered forms:

- Controller method creates a model object before showing the form.
- Form uses `th:object` and `th:field`.
- Submit method uses `@Valid @ModelAttribute(...)`.
- `BindingResult` must immediately follow the validated object.
- On errors, return the form view again.
- On success, call the service and redirect.

## Cross-Cutting Concerns

Use AOP for logic that cuts across layers or many service methods:

- Logging
- Security checks
- Performance timing
- Error alerts

Expected package: `com.demo.aop`.

Typical annotations:

- `@Aspect`
- `@Component`
- `@Before`
- `@AfterReturning`
- `@AfterThrowing`
- `@Around`

Do not put business logic inside aspects.

## Error Handling

For Spring Boot:

- Use custom runtime exceptions for domain errors such as not found.
- Use `@ControllerAdvice` for global handling.
- For HTML apps, return custom error views.
- For REST APIs, prefer structured JSON error responses with correct HTTP status codes.
- Static error pages can live under `src/main/resources/templates/error/`.

## Microservice Project Pattern From The Course

The project PDF describes a student enrollment system:

- Student Service: manages student identity fields such as name, email, CNIE.
- Course Service: manages course catalog.
- Enrollment Service: stores only `studentId`, `courseId`, and `enrollmentDate`.
- API Gateway: single entry point for the front end.

Important microservice rule:

- Avoid duplicating student/course details in the enrollment database.
- Each service owns its own data.
- Enrollment combines data from other services through clients and returns DTOs.

Enrollment business rules:

- Verify student exists.
- Verify course exists.
- A course cannot have more than 3 students.
- Returned enrollment DTO includes combined fields such as `studentCnie`, `courseName`, date, and `deletable`.
- `deletable` is calculated by business logic, for example based on whether 24 hours have passed.

Suggested enrollment package structure:

```text
src/main/java/com/demo/enrollment
  config/WebClientConfig.java
  clients/StudentClient.java
  clients/CourseClient.java
  controllers/EnrollmentController.java
  services/EnrollmentService.java
  entities/Enrollment.java
  dtos/EnrollmentRequest.java
  dtos/EnrollmentResponseDTO.java
  repository/EnrollmentRepository.java
```

## What The Teacher Will Likely Reward

- Clear layered architecture.
- Thin controllers.
- Real service layer with business logic.
- Spring dependency injection instead of manual object creation.
- `JpaRepository` instead of manual SQL/JDBC.
- Correct entity annotations and no-args constructors.
- Sensible package names under `com.demo`.
- Working MySQL configuration.
- REST controllers that return JSON only.
- DTOs for API contracts, especially when combining data.
- Correct HTTP methods and status codes.
- Simple, readable code that matches the lecture examples.

## What To Avoid

- Fat controllers containing validation, SQL, and business rules.
- Creating services/repositories with `new`.
- Returning HTML views from `@RestController`.
- Putting business logic in JSP/Thymeleaf.
- Duplicating data between microservices.
- Skipping DTOs when the API response is not exactly one entity.
- Placing the main Spring Boot class outside the parent package.
- Hardcoding too much in controllers.
