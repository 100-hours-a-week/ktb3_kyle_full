package com.kyle.week4.domain;

import lombok.Builder;

public class User {
    private Long userId;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;

    @Builder
    public User(String email, String nickname, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public void assignUserId(Long userId) {
        this.userId = userId;
    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
