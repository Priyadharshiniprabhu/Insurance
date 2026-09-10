package com.fd.insurance.exception;

import com.fd.insurance.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //@ExceptionHandler(PolicyAlreadyExistsException.class)
    //public ResponseEntity<SuccessResponse> handleDuplicate(
        //    PolicyAlreadyExistsException ex) {

     //   return ResponseEntity
       //         .status(HttpStatus.CONFLICT)
         //       .body(new SuccessResponse(ex.getMessage()));

    @ExceptionHandler(PolicyAlreadyExistsException.class)
    public ResponseEntity<Response> handlePolicyAlreadyExists(
            PolicyAlreadyExistsException ex) {

        Response response = new Response(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Validation failed");

        Response response = new Response(message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response("Invalid policy type. Allowed values are HEALTH, MOTOR, TERM"));
    }

    @ExceptionHandler(PolicyNotFoundException.class)
    public ResponseEntity<Response> handlePolicyNotFound(PolicyNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Response(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateCustomerException.class)
    public ResponseEntity<Response> handleDuplicateCustomerException(DuplicateCustomerException ex) {

        return ResponseEntity.badRequest()
                .body(new Response(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateDLQRequeueException.class)
    public ResponseEntity<Map<String, String>>
    handleDuplicateRequeue(DuplicateDLQRequeueException ex) {

        Map<String, String> response = new HashMap<>();

        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(DLQRecordNotFoundException.class)
    public ResponseEntity<Response> handleDLQRecordNotFound(
            DLQRecordNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new Response(ex.getMessage()));
    }

}
