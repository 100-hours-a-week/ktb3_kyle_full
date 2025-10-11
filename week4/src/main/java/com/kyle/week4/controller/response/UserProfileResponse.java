package com.kyle.week4.controller.response;

import com.kyle.week4.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileResponse {
    private String email;
    private String nickname;
    private String profileImage;

    @Builder
    public UserProfileResponse(String email, String nickname, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public static UserProfileResponse of(User user) {
        return UserProfileResponse.builder()
          .email(user.getEmail())
          .nickname(user.getNickname())
          .profileImage(user.getProfileImage())
          .build();
    }
}
