package com.demo.enrollment.dtos;

public class EnrollmentResponseDTO {

    private Long enrollmentId;
    private String studentCnie;
    private String courseName;
    private String date;
    private boolean deletable;

    public EnrollmentResponseDTO() {
    }

    public EnrollmentResponseDTO(Long enrollmentId, String studentCnie, String courseName, String date, boolean deletable) {
        this.enrollmentId = enrollmentId;
        this.studentCnie = studentCnie;
        this.courseName = courseName;
        this.date = date;
        this.deletable = deletable;
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getStudentCnie() {
        return studentCnie;
    }

    public void setStudentCnie(String studentCnie) {
        this.studentCnie = studentCnie;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }
}
