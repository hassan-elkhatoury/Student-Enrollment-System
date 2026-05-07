package com.demo.course.exceptions;

public class DuplicateCourseException extends RuntimeException {

    public DuplicateCourseException(String message) {
        super(message);
    }
}
