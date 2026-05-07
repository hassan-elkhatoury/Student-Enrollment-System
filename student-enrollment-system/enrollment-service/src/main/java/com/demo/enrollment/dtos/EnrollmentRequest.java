package com.demo.enrollment.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EnrollmentRequest {

    @NotBlank(message = "Student CNIE is required")
    @JsonAlias("cnie")
    private String studentCnie;

    @NotNull(message = "Course id is required")
    private Long courseId;

    public String getStudentCnie() {
        return studentCnie;
    }

    public void setStudentCnie(String studentCnie) {
        this.studentCnie = studentCnie;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
