package com.kyle.week4.exception;

import com.kyle.week4.controller.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {
    // 존재하지 않는 요청에 대한 예외
    @ExceptionHandler(value = {NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public BaseResponse<?> handleNoPageFoundException(Exception e) {
        return BaseResponse.fail(new CustomException(ErrorCode.NOT_FOUND_END_POINT));
    }

    // 커스텀 예외
    @ExceptionHandler(value = {CustomException.class})
    public BaseResponse<?> handleCustomException(CustomException e) {
        log.error(e.getMessage());
        return BaseResponse.fail(e);
    }

    // validation 예외
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public BaseResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        return BaseResponse.validationFail(e);
    }

    // 기본 예외
    @ExceptionHandler(value = {Exception.class})
    public BaseResponse<?> handleException(Exception e) {
        log.error(e.getMessage());
        return BaseResponse.fail(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
