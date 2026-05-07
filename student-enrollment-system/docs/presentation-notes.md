# Presentation Notes

## Suggested Demo Order

1. Open Eureka at `http://localhost:8761` and show that all services are registered.
2. Open the dashboard at `http://localhost:8080`.
3. Load enrollments for `CD2387`.
4. Explain that the UI receives DTOs from Enrollment Service, not raw database rows.
5. Create a new enrollment and show the table refresh.
6. Try to delete a recent enrollment and explain the 24-hour rule.
7. Mention that older enrollments are locked because `deletable=false`.
8. Open Enrollment Swagger and show the exact endpoints.

## Points To Explain To The Teacher

- Student, Course and Enrollment are separate services.
- Each service owns its own database.
- Enrollment does not duplicate course titles or student CNIE in its table.
- WebClient lets Enrollment Service ask the other services for data.
- `EnrollmentResponseDTO` is required because the response combines several data sources.
- Business logic is in `EnrollmentService`, not in the controller.
- Gateway is the single entry point for browser and API calls.
