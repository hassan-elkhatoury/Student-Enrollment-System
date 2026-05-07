package com.demo.enrollment.services;

import com.demo.enrollment.clients.CourseClient;
import com.demo.enrollment.clients.StudentClient;
import com.demo.enrollment.dtos.CourseDTO;
import com.demo.enrollment.dtos.CourseCapacityDTO;
import com.demo.enrollment.dtos.EnrollmentRequest;
import com.demo.enrollment.dtos.EnrollmentResponseDTO;
import com.demo.enrollment.dtos.StudentDTO;
import com.demo.enrollment.entities.Enrollment;
import com.demo.enrollment.exceptions.CancellationExpiredException;
import com.demo.enrollment.exceptions.CourseFullException;
import com.demo.enrollment.exceptions.CourseNotFoundException;
import com.demo.enrollment.exceptions.EnrollmentAlreadyExistsException;
import com.demo.enrollment.exceptions.EnrollmentNotFoundException;
import com.demo.enrollment.exceptions.StudentNotFoundException;
import com.demo.enrollment.repository.EnrollmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentService {

    private static final int MAX_STUDENTS_PER_COURSE = 3;
    private static final int CANCELLATION_LIMIT_HOURS = 24;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentClient studentClient;

    @Autowired
    private CourseClient courseClient;

    @Transactional
    public EnrollmentResponseDTO enrollStudent(EnrollmentRequest request) {
        StudentDTO student = findStudentByCnie(request.getStudentCnie());
        CourseDTO course = findCourseById(request.getCourseId());

        long currentCount = enrollmentRepository.countByCourseId(course.getId());
        if (currentCount >= MAX_STUDENTS_PER_COURSE) {
            throw new CourseFullException("Course " + course.getTitle() + " already has 3 students");
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new EnrollmentAlreadyExistsException("Student " + student.getCnie() + " is already enrolled in " + course.getTitle());
        }

        Enrollment enrollment = new Enrollment(student.getId(), course.getId(), LocalDateTime.now());
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return toResponse(savedEnrollment, student, course);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getMyEnrollments(String cnie) {
        StudentDTO student = findStudentByCnie(cnie);

        return enrollmentRepository.findByStudentIdOrderByEnrollmentDateDesc(student.getId())
                .stream()
                .map(enrollment -> {
                    CourseDTO course = findCourseById(enrollment.getCourseId());
                    return toResponse(enrollment, student, course);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseCapacityDTO> getCourseCapacities() {
        return courseClient.getAllCourses()
                .stream()
                .map(course -> new CourseCapacityDTO(
                        course.getId(),
                        course.getTitle(),
                        course.getDescription(),
                        course.getCredits(),
                        enrollmentRepository.countByCourseId(course.getId()),
                        MAX_STUDENTS_PER_COURSE
                ))
                .toList();
    }

    @Transactional
    public void cancelEnrollment(Long enrollmentId, String studentCnie) {
        StudentDTO student = findStudentByCnie(studentCnie);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment with id " + enrollmentId + " was not found"));

        if (!enrollment.getStudentId().equals(student.getId())) {
            throw new EnrollmentNotFoundException("Enrollment does not belong to student " + studentCnie);
        }

        if (!isDeletable(enrollment)) {
            throw new CancellationExpiredException("This enrollment can no longer be cancelled after 24 hours");
        }

        enrollmentRepository.delete(enrollment);
    }

    private StudentDTO findStudentByCnie(String cnie) {
        StudentDTO student = studentClient.getStudentByCnie(cnie);
        if (student == null) {
            throw new StudentNotFoundException("Student with CNIE " + cnie + " was not found");
        }
        return student;
    }

    private CourseDTO findCourseById(Long courseId) {
        CourseDTO course = courseClient.getCourseById(courseId);
        if (course == null) {
            throw new CourseNotFoundException("Course with id " + courseId + " was not found");
        }
        return course;
    }

    private EnrollmentResponseDTO toResponse(Enrollment enrollment, StudentDTO student, CourseDTO course) {
        return new EnrollmentResponseDTO(
                enrollment.getId(),
                student.getCnie(),
                course.getTitle(),
                enrollment.getEnrollmentDate().toString(),
                isDeletable(enrollment)
        );
    }

    private boolean isDeletable(Enrollment enrollment) {
        return enrollment.getEnrollmentDate().isAfter(LocalDateTime.now().minusHours(CANCELLATION_LIMIT_HOURS));
    }
}
