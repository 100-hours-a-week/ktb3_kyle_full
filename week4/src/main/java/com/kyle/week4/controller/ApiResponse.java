package com.kyle.week4.controller;

import com.kyle.week4.exception.CustomException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public record ApiResponse<T>(
    HttpStatus httpStatus,
    boolean success,
    @Nullable T data,
    String errorMessage
) {
    public static <T> ApiResponse<T> ok(@Nullable final T data) {
        return new ApiResponse<>(HttpStatus.OK, true, data, null);
    }

    public static <T> ApiResponse<T> created(@Nullable final T data) {
        return new ApiResponse<>(HttpStatus.CREATED,true, data, null);
    }

    public static <T> ApiResponse<T> fail(final CustomException e) {
        return new ApiResponse<>(e.getHttpStatus(), false, null, e.getMessage());
    }
}
