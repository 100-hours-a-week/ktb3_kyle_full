package com.kyle.week4.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    public HttpStatus getHttpStatus() { return errorCode.getHttpStatus(); }
    public String getMessage() {
        return errorCode.getMessage();
    }
}
