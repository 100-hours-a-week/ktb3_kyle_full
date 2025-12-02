package com.kyle.week4.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "사용자 프로필 정보 수정 요청 DTO")
public class UserProfileUpdateRequest {
    @Size(max = 10, message = "닉네임은 최대 10자 까지 작성 가능합니다.")
    @NotBlank(message = "닉네임을 작성하지 않거나, 공백을 포함할 수 없습니다.")
    @Schema(description = "닉네임", defaultValue = "kyle123")
    private String nickname;

    @Schema(description = "사용자 프로필 이미지 경로", defaultValue = "update.jpg")
    private String profileImage;

    public UserProfileUpdateRequest(String nickname) {
        this.nickname = nickname;
    }

    public UserProfileUpdateRequest(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
