package com.kyle.week4.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class User {
    private Long id;
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

    public boolean isSameNickname(String nickname) {
        return this.nickname.equals(nickname);
    }

    public boolean isSameEmail(String email) {
        return this.email.equals(email);
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
