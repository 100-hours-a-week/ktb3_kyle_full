package com.kyle.week4.controller;

import com.kyle.week4.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    HttpStatus httpStatus;
    boolean success;
    @Nullable T data;
    String errorMessage;

    public static <T> BaseResponse<T> ok(@Nullable final T data) {
        return new BaseResponse<>(HttpStatus.OK, true, data, null);
    }

    public static <T> BaseResponse<T> created(@Nullable final T data) {
        return new BaseResponse<>(HttpStatus.CREATED,true, data, null);
    }

    public static <T> BaseResponse<T> fail(final CustomException e) {
        return new BaseResponse<>(e.getHttpStatus(), false, null, e.getMessage());
    }

    public static <T> BaseResponse<T> validationFail(final MethodArgumentNotValidException e) {
        return new BaseResponse<>(HttpStatus.BAD_REQUEST, false, null, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    public static <T> BaseResponse<T> noContent() {
        return new BaseResponse<>(HttpStatus.NO_CONTENT, true, null, null);
    }
}
