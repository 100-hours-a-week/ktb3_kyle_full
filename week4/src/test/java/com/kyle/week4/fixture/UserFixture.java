package com.kyle.week4.fixture;

import com.kyle.week4.entity.User;

public class UserFixture {
    public static User defaultUser() {
        return User.builder()
            .email("test@test.com")
            .nickname("test")
            .build();
    }

    public static User withEmailAndNickname(String email, String nickname) {
        return User.builder()
            .email(email)
            .nickname(nickname)
            .build();
    }
}
