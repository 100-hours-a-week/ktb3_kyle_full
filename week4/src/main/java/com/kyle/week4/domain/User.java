package com.kyle.week4.domain;

import lombok.Builder;

public class User {
    private Long userId;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;

    @Builder
    public User(Long userId, String email, String password, String nickname, String profileImage) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
