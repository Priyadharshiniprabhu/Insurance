package com.fd.insurance.exception;

public class DuplicateDLQRequeueException extends RuntimeException {

    public DuplicateDLQRequeueException(String message) {
        super(message);
    }
}