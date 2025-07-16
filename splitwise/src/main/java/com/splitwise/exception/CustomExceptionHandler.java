package com.splitwise.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ErrorResponse ApplicationExceptionHandler(ApplicationException exception) {
        return new ErrorResponse(exception.getErrCode(),exception.getMessage(),exception.getHttpStatus());
    }

}
