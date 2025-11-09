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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import static com.kyle.week4.exception.ErrorCode.*;

@Tag(name = "User API", description = "사용자 관련 API")
public interface UserControllerDocs {

    @Operation(
        summary = "신규 사용자 회원가입",
        description = "### 신규 사용자의 정보를 생성합니다."
    )
    @ApiResponse(
        responseCode = "201", description = "회원가입 성공",
        content = @Content(schema = @Schema(implementation = Long.class))
    )
    @ApiErrorResponses({DUPLICATE_NICKNAME_ERROR, DUPLICATE_EMAIL_ERROR})
    BaseResponse<Long> createUser(
        @Parameter(
            name = "request",
            description = "사용자 생성 본문(JSON)",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserCreateRequest.class)
            )
        )
        @RequestPart("request")
        UserCreateRequest request,
        @Parameter(
            name = "image",
            description = "업로드할 이미지",
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(type = "string", format = "binary")
            )
        )
        @RequestPart(value = "image", required = false)
        MultipartFile image
    );

    @Operation(
        summary = "사용자 정보 조회",
        description = "### 사용자의 정보(이메일, 닉네임, 프로필 이미지)를 조회합니다."
    )
    @ApiResponse(
        responseCode = "200", description = "조회 성공",
        content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
    )
    @ApiErrorResponse(USER_NOT_FOUND)
    BaseResponse<UserProfileResponse> getUserProfile(@Parameter(hidden = true) Long userId);

    @Operation(
        summary = "사용자 정보 수정",
        description = "### 사용자의 정보(닉네임, 프로필 이미지)를 수정합니다."
    )
    @ApiResponse(
        responseCode = "200", description = "수정 성공",
        content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
    )
    @ApiErrorResponses({USER_NOT_FOUND, DUPLICATE_NICKNAME_ERROR})
    BaseResponse<UserProfileResponse> updateUserProfile(
        @Parameter(hidden = true)
        Long userId,
        UserProfileUpdateRequest request,
        MultipartFile image
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
