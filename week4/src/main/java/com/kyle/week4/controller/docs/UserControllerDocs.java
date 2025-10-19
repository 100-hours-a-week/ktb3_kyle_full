package com.kyle.week4.controller.docs;

import com.kyle.week4.controller.BaseResponse;
import com.kyle.week4.controller.request.PasswordUpdateRequest;
import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.controller.request.UserProfileUpdateRequest;
import com.kyle.week4.controller.response.UserProfileResponse;
import com.kyle.week4.swagger.annotation.ApiErrorResponse;
import com.kyle.week4.swagger.annotation.ApiErrorResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import static com.kyle.week4.exception.ErrorCode.*;

@Tag(name = "User API", description = "사용자 관련 API")
public interface UserControllerDocs {

    @Operation(
      summary = "신규 사용자 회원가입",
      description = "### 신규 사용자의 정보를 생성합니다."
    )
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @ApiErrorResponses({DUPLICATE_NICKNAME_ERROR, DUPLICATE_EMAIL_ERROR})
    BaseResponse<Long> createUser(UserCreateRequest request);

    @Operation(
      summary = "사용자 정보 조회",
      description = "### 사용자의 정보(이메일, 닉네임, 프로필 이미지)를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiErrorResponse(USER_NOT_FOUND)
    BaseResponse<UserProfileResponse> getUserProfile(@Parameter(hidden = true) Long userId);

    @Operation(
      summary = "사용자 정보 수정",
      description = "### 사용자의 정보(닉네임, 프로필 이미지)를 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiErrorResponses({USER_NOT_FOUND, DUPLICATE_NICKNAME_ERROR})
    BaseResponse<UserProfileResponse> updateUserProfile(
      @Parameter(hidden = true)
      Long userId,
      UserProfileUpdateRequest request
    );

    @Operation(
      summary = "사용자 비밀번호 변경",
      description =
        """
          ### 사용자의 비밀번호를 변경합니다.
          - 이전 비밀번호와 같을 수 없습니다.
          """
    )
    @ApiResponse(responseCode = "204", description = "변경 성공")
    @ApiErrorResponses({USER_NOT_FOUND, PASSWORD_SAME_BEFORE_ERROR})
    BaseResponse<?> updatePassword(
      @Parameter(hidden = true)
      Long userId,
      PasswordUpdateRequest request
    );
}
