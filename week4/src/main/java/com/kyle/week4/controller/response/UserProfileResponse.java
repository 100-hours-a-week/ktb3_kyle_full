package com.kyle.week4.controller.response;

import com.kyle.week4.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "사용자 프로필 정보 응답 DTO")
public class UserProfileResponse {
    private Long id;

    @Schema(description = "이메일", defaultValue = "kyle@test.com")
    private String email;

    @Schema(description = "닉네임", defaultValue = "kyle")
    private String nickname;

    @Schema(description = "사용자 프로필 이미지 경로", defaultValue = "kyle.jpg")
    private String profileImage;

    @Builder
    public UserProfileResponse(Long id, String email, String nickname, String profileImage) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public static UserProfileResponse of(User user) {
        return UserProfileResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .profileImage(user.getProfileImage())
            .build();
    }
}
