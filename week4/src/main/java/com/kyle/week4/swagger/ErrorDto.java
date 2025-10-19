package com.kyle.week4.swagger;

import com.kyle.week4.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorDto {
    private String httpStatus;
    private boolean success;
    private Object data;
    private String errorMessage;

    static ErrorDto from(ErrorCode errorCode) {
        return new ErrorDto(
          errorCode.getHttpStatus().getReasonPhrase(),
          false,
          null,
          errorCode.getMessage()
        );
    }
}
