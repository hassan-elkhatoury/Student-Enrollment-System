# Architecture Notes

## Project Objective

The system implements the project PDF's Microservices-Based Student Enrollment System. It is composed of Student Service, Course Service, Enrollment Service and API Gateway. A Eureka Discovery Server is included because the course lists service discovery as part of the microservices architecture.

## Service Responsibilities

Student Service manages student identity data:

- `id`
- `cnie`
- `firstName`
- `lastName`
- `email`

Course Service manages course catalog data:

- `id`
- `title`
- `description`
- `credits`

Enrollment Service stores only enrollment links:

- `id`
- `studentId`
- `courseId`
- `enrollmentDate`

This respects the PDF's rule that student and course details are not duplicated in the enrollment database.

## Request Flow

1. The browser calls the API Gateway on port 8080.
2. Gateway routes `/api/students/**`, `/api/courses/**` and `/api/enrollments/**`.
3. Enrollment Service receives the enrollment request.
4. Enrollment Service calls Student Service by CNIE.
5. Enrollment Service calls Course Service by course id.
6. Enrollment Service checks the max-3 rule and duplicate enrollment rule.
7. Enrollment Service saves the enrollment and returns an `EnrollmentResponseDTO`.

## Teacher Methodology

The code follows the course pattern:

- `@RestController` classes are entry points.
- `@Service` classes contain business logic.
- `@Repository` interfaces extend `JpaRepository`.
- `@Entity` classes use JPA/Hibernate annotations.
- DTO classes define JSON contracts.
- WebClient is used for service-to-service communication.
- API Gateway is the single entry point for the frontend.

## Main Classes

- `StudentController`, `StudentService`, `StudentRepository`, `Student`
- `CourseController`, `CourseService`, `CourseRepository`, `Course`
- `EnrollmentController`, `EnrollmentService`, `EnrollmentRepository`, `Enrollment`
- `StudentClient`, `CourseClient`, `WebClientConfig`
- `EnrollmentRequest`, `EnrollmentResponseDTO`

## Business Rules Implemented

- Verify student exists.
- Verify course exists.
- Reject enrollment if course already has 3 students.
- Reject duplicate enrollment for the same student and course.
- Compute `deletable` from the 24-hour rule.
- Reject cancellation after 24 hours.
