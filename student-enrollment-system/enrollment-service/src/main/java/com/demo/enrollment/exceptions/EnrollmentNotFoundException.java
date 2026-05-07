package com.demo.enrollment.exceptions;

public class EnrollmentNotFoundException extends RuntimeException {

    public EnrollmentNotFoundException(String message) {
        super(message);
    }
}
