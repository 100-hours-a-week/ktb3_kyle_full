package com.kyle.week4.controller.request;

import com.kyle.week4.domain.User;
import lombok.Getter;

@Getter
public class UserCreateRequest {
    private String email;
    private String password;
    private String nickname;
    private String profileImage;

    public User toEntity() {
        return User.builder()
            .email(email)
            .password(password)
            .nickname(nickname)
            .profileImage(profileImage)
            .build();
    }
}
