package com.demo.enrollment.dtos;

public class CourseCapacityDTO {

    private Long courseId;
    private String courseName;
    private String description;
    private int credits;
    private long enrolledCount;
    private int capacity;
    private long availablePlaces;

    public CourseCapacityDTO() {
    }

    public CourseCapacityDTO(Long courseId, String courseName, String description, int credits, long enrolledCount, int capacity) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.credits = credits;
        this.enrolledCount = enrolledCount;
        this.capacity = capacity;
        this.availablePlaces = Math.max(0, capacity - enrolledCount);
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public long getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(long enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public long getAvailablePlaces() {
        return availablePlaces;
    }

    public void setAvailablePlaces(long availablePlaces) {
        this.availablePlaces = availablePlaces;
    }
}
