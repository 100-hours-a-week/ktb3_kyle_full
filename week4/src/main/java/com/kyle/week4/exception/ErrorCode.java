package com.kyle.week4.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND_END_POINT(404, HttpStatus.NOT_FOUND, "not_found_api"),
    DUPLICATE_EMAIL_ERROR(409, HttpStatus.CONFLICT, "duplicate_email"),
    DUPLICATE_NICKNAME_ERROR(409, HttpStatus.CONFLICT, "duplicate_nickname"),
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "internal_server_error"),;

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
