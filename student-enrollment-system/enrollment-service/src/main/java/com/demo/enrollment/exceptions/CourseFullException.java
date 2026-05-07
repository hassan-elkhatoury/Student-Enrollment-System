package com.demo.enrollment.exceptions;

public class CourseFullException extends RuntimeException {

    public CourseFullException(String message) {
        super(message);
    }
}
