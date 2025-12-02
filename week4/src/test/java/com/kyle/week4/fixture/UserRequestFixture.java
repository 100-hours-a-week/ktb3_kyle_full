package com.kyle.week4.fixture;

import com.kyle.week4.controller.request.PasswordUpdateRequest;
import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.controller.request.UserProfileUpdateRequest;

public class UserRequestFixture {

    public static UserCreateRequest create(String email, String nickname) {
        return UserCreateRequest.builder()
            .email(email)
            .nickname(nickname)
            .password("Test1234!")
            .build();
    }

    public static UserCreateRequest create() {
        return create("test@test.com", "test");
    }

    public static UserProfileUpdateRequest update(String nickname) {
        return new UserProfileUpdateRequest(nickname);
    }

    public static PasswordUpdateRequest passwordUpdate(String newPassword) {
        return new PasswordUpdateRequest(newPassword);
    }
}
