package com.kyle.week4.controller.docs;

import com.kyle.week4.controller.BaseResponse;
import com.kyle.week4.controller.request.LoginRequest;
import com.kyle.week4.swagger.annotation.ApiErrorResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import static com.kyle.week4.exception.ErrorCode.INVALID_EMAIL;
import static com.kyle.week4.exception.ErrorCode.INVALID_PASSWORD;

@Tag(name = "Auth API", description = "사용자 인증 관련 API")
public interface AuthControllerDocs {

    @Operation(
      summary = "로그인",
      description = "사용자의 **이메일과 비밀번호를 입력**받아 **로그인을 수행**합니다."
    )
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiErrorResponses({INVALID_EMAIL, INVALID_PASSWORD})
    BaseResponse<Long> login(LoginRequest loginRequest, HttpServletRequest httpRequest);
}
