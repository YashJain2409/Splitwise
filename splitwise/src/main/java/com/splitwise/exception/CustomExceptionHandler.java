package com.splitwise.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ErrorResponse ApplicationExceptionHandler(ApplicationException exception) {
        return new ErrorResponse(exception.getErrCode(),exception.getMessage(),exception.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex){

        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));
        ErrorResponse errorResponse = new ErrorResponse("000","Enter valid user details",HttpStatus.BAD_REQUEST,errors);
        return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.BAD_REQUEST);


    }

}
