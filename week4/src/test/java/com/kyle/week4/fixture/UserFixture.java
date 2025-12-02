package com.kyle.week4.fixture;

import com.kyle.week4.entity.User;

public class UserFixture {
    public static User defaultUser() {
        return User.builder()
            .email("test@test.com")
            .nickname("test")
            .profileImage("profile.jpg")
            .build();
    }

    public static User deletedUser() {
        User user = UserFixture.defaultUser();
        user.withdraw();
        return user;
    }

    public static User withEmailAndNickname(String email, String nickname) {
        return User.builder()
            .email(email)
            .nickname(nickname)
            .build();
    }
}
