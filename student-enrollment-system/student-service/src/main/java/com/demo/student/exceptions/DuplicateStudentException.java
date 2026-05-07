package com.demo.student.exceptions;

public class DuplicateStudentException extends RuntimeException {

    public DuplicateStudentException(String message) {
        super(message);
    }
}
