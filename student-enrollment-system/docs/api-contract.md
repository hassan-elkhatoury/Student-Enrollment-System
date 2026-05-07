# API Contract

All endpoints are available through the API Gateway at:

```text
http://localhost:8080
```

## Students

```http
GET /api/students
GET /api/students/{id}
GET /api/students/by-cnie/{cnie}
POST /api/students
PUT /api/students/{id}
DELETE /api/students/{id}
```

Student request:

```json
{
  "cnie": "CD2387",
  "firstName": "Yassine",
  "lastName": "El Amrani",
  "email": "yassine.elamrani@uae.ac.ma"
}
```

## Courses

```http
GET /api/courses
GET /api/courses/{id}
POST /api/courses
PUT /api/courses/{id}
DELETE /api/courses/{id}
```

Course request:

```json
{
  "title": "Spring Framework",
  "description": "Controllers, dependency injection, services and repositories.",
  "credits": 4
}
```

## Enrollments

```http
POST /api/enrollments
GET /api/enrollments/student/{cnie}
DELETE /api/enrollments/{enrollmentId}?studentCnie={cnie}
```

Enrollment request:

```json
{
  "studentCnie": "CD2387",
  "courseId": 1
}
```

Enrollment response:

```json
{
  "enrollmentId": 45,
  "studentCnie": "CD2387",
  "courseName": "Spring Framework",
  "date": "2026-04-28T10:30:00",
  "deletable": true
}
```
