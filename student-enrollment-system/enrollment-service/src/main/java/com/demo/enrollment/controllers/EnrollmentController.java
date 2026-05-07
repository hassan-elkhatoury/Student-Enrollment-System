package com.demo.enrollment.controllers;

import com.demo.enrollment.dtos.EnrollmentRequest;
import com.demo.enrollment.dtos.EnrollmentResponseDTO;
import com.demo.enrollment.dtos.CourseCapacityDTO;
import com.demo.enrollment.services.EnrollmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> enrollStudent(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enrollStudent(request));
    }

    @GetMapping("/student/{cnie}")
    public List<EnrollmentResponseDTO> getMyEnrollments(@PathVariable String cnie) {
        return enrollmentService.getMyEnrollments(cnie);
    }

    @GetMapping("/courses/capacity")
    public List<CourseCapacityDTO> getCourseCapacities() {
        return enrollmentService.getCourseCapacities();
    }

    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> cancelEnrollment(
            @PathVariable Long enrollmentId,
            @RequestParam("studentCnie") String studentCnie
    ) {
        enrollmentService.cancelEnrollment(enrollmentId, studentCnie);
        return ResponseEntity.noContent().build();
    }
}
