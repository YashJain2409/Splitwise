package com.splitwise.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
public class ErrorResponse {
    private String errCode;
    private String message;
    private HttpStatus httpStatus;
    private Map<String,String> errorMap;

    public ErrorResponse(String errCode, String message, HttpStatus status) {
        this.errCode = errCode;
        this.message = message;
        this.httpStatus = status;
    }

    public ErrorResponse(String errCode, String message, HttpStatus status,Map<String,String> errorMap) {
        this.errCode = errCode;
        this.errorMap=errorMap;
        this.message = message;
        this.httpStatus = status;
    }
}
