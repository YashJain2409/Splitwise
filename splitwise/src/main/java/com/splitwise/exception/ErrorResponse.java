package com.splitwise.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResponse {
    private String errCode;
    private String message;
    private HttpStatus httpStatus;

    public ErrorResponse(String errCode, String message, HttpStatus status) {
        this.errCode = errCode;
        this.message = message;
        this.httpStatus = status;
    }
}
