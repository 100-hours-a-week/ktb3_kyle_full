package com.kyle.week4.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class User {
    @Getter
    private Long userId;
    private String email;
    @Getter
    private String password;
    private String nickname;
    private String profileImage;

    @Builder
    public User(String email, String nickname, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public boolean isSameNickname(String nickname) {
        return this.nickname.equals(nickname);
    }

    public boolean isSameEmail(String email) {
        return this.email.equals(email);
    }

    public void assignUserId(Long userId) {
        this.userId = userId;
    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
