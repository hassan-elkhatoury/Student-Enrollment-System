package com.demo.enrollment.exceptions;

public class CancellationExpiredException extends RuntimeException {

    public CancellationExpiredException(String message) {
        super(message);
    }
}
