package com.fd.insurance.exception;

public class DLQRecordNotFoundException extends RuntimeException {

    public DLQRecordNotFoundException(String message) {
        super(message);
    }
}