package com.splitwise.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException{
    private final String errCode;

    private final HttpStatus httpStatus;

    public ApplicationException(String errCode,String message,HttpStatus httpStatus) {
        super(message);
        this.errCode = errCode;
        this.httpStatus = httpStatus;
    }


}
