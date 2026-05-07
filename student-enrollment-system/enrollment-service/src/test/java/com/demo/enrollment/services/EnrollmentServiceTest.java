package com.demo.enrollment.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.demo.enrollment.clients.CourseClient;
import com.demo.enrollment.clients.StudentClient;
import com.demo.enrollment.dtos.CourseDTO;
import com.demo.enrollment.dtos.EnrollmentRequest;
import com.demo.enrollment.dtos.EnrollmentResponseDTO;
import com.demo.enrollment.dtos.StudentDTO;
import com.demo.enrollment.entities.Enrollment;
import com.demo.enrollment.exceptions.CancellationExpiredException;
import com.demo.enrollment.exceptions.CourseFullException;
import com.demo.enrollment.exceptions.EnrollmentAlreadyExistsException;
import com.demo.enrollment.repository.EnrollmentRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentClient studentClient;

    @Mock
    private CourseClient courseClient;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Test
    void enrollStudentReturnsCombinedDto() {
        EnrollmentRequest request = request("CD2387", 1L);
        StudentDTO student = student(7L, "CD2387");
        CourseDTO course = course(1L, "Spring Framework");

        when(studentClient.getStudentByCnie("CD2387")).thenReturn(student);
        when(courseClient.getCourseById(1L)).thenReturn(course);
        when(enrollmentRepository.countByCourseId(1L)).thenReturn(0L);
        when(enrollmentRepository.existsByStudentIdAndCourseId(7L, 1L)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> {
            Enrollment enrollment = invocation.getArgument(0);
            enrollment.setId(45L);
            return enrollment;
        });

        EnrollmentResponseDTO response = enrollmentService.enrollStudent(request);

        assertEquals(45L, response.getEnrollmentId());
        assertEquals("CD2387", response.getStudentCnie());
        assertEquals("Spring Framework", response.getCourseName());
    }

    @Test
    void enrollStudentRejectsFullCourse() {
        EnrollmentRequest request = request("CD2387", 1L);

        when(studentClient.getStudentByCnie("CD2387")).thenReturn(student(7L, "CD2387"));
        when(courseClient.getCourseById(1L)).thenReturn(course(1L, "Spring Framework"));
        when(enrollmentRepository.countByCourseId(1L)).thenReturn(3L);

        assertThrows(CourseFullException.class, () -> enrollmentService.enrollStudent(request));
    }

    @Test
    void enrollStudentRejectsDuplicateEnrollment() {
        EnrollmentRequest request = request("CD2387", 1L);

        when(studentClient.getStudentByCnie("CD2387")).thenReturn(student(7L, "CD2387"));
        when(courseClient.getCourseById(1L)).thenReturn(course(1L, "Spring Framework"));
        when(enrollmentRepository.countByCourseId(1L)).thenReturn(1L);
        when(enrollmentRepository.existsByStudentIdAndCourseId(7L, 1L)).thenReturn(true);

        assertThrows(EnrollmentAlreadyExistsException.class, () -> enrollmentService.enrollStudent(request));
    }

    @Test
    void cancelEnrollmentRejectsExpiredEnrollment() {
        Enrollment oldEnrollment = new Enrollment(7L, 1L, LocalDateTime.now().minusHours(25));
        oldEnrollment.setId(11L);

        when(studentClient.getStudentByCnie("CD2387")).thenReturn(student(7L, "CD2387"));
        when(enrollmentRepository.findById(11L)).thenReturn(Optional.of(oldEnrollment));

        assertThrows(CancellationExpiredException.class, () -> enrollmentService.cancelEnrollment(11L, "CD2387"));
    }

    private EnrollmentRequest request(String cnie, Long courseId) {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentCnie(cnie);
        request.setCourseId(courseId);
        return request;
    }

    private StudentDTO student(Long id, String cnie) {
        StudentDTO student = new StudentDTO();
        student.setId(id);
        student.setCnie(cnie);
        return student;
    }

    private CourseDTO course(Long id, String title) {
        CourseDTO course = new CourseDTO();
        course.setId(id);
        course.setTitle(title);
        return course;
    }
}
