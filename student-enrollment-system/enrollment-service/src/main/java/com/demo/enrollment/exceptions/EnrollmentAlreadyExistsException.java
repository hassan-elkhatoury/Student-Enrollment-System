package com.demo.enrollment.exceptions;

public class EnrollmentAlreadyExistsException extends RuntimeException {

    public EnrollmentAlreadyExistsException(String message) {
        super(message);
    }
}
