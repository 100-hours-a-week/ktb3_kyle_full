package com.kyle.week4.domain;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
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

    public boolean isDuplicateNickname(String nickname) {
        return this.nickname.equals(nickname);
    }

    public boolean isDuplicateEmail(String email) {
        return this.email.equals(email);
    }

    public void assignUserId(Long userId) {
        this.userId = userId;
    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
