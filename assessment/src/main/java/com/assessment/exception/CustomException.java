package com.assessment.exception;


public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CustomException(String exception) {
        super(exception);
    }
}
