package com.fd.insurance.exception;

public class PolicyAlreadyExistsException extends RuntimeException{
    public PolicyAlreadyExistsException(String message){
        super(message);
    }

}
