package com.kyle.week4.service;

import com.kyle.week4.controller.request.LoginRequest;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.exception.ErrorCode;
import com.kyle.week4.repository.UserRepository;
import com.kyle.week4.utils.PasswordEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.kyle.week4.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @AfterEach
    void tearDown() {
        userRepository.clear();
    }

    @Test
    @DisplayName("이메일과 비밀번호가 일치하면 로그인할 수 있다.")
    void login() {
        // given
        String email = "test@test.com";
        String password = "Test1234!";

        User user = createUser(email);
        user.encodePassword(encoder.encode(password));
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
          .email(email)
          .password(password)
          .build();

        // when
        Long userId = authService.login(request);

        // then
        assertThat(userId).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("로그인 시 이메일이 일치하지 않으면 예외가 발생한다.")
    void invalidEmail() {
        // given
        String email = "test@test.com";
        String password = "Test1234!";

        User user = createUser(email);
        user.encodePassword(encoder.encode(password));
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
          .email("test1@test.com")
          .password(password)
          .build();

        // when // then
        assertThatThrownBy(() -> authService.login(request))
          .isInstanceOf(CustomException.class)
          .hasFieldOrPropertyWithValue("errorCode", INVALID_EMAIL);
    }

    @Test
    @DisplayName("로그인 시 비밀번호가 일치하지 않으면 예외가 발생한다.")
    void invalidPassword() {
        // given
        String email = "test@test.com";
        String password = "Test1234!";

        User user = createUser(email);
        user.encodePassword(encoder.encode(password));
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
          .email(email)
          .password("Test123456789")
          .build();

        // when // then
        assertThatThrownBy(() -> authService.login(request))
          .isInstanceOf(CustomException.class)
          .hasFieldOrPropertyWithValue("errorCode", INVALID_PASSWORD);
    }

    private User createUser(String email) {
        return User.builder()
          .email(email)
          .nickname("test")
          .profileImage("image")
          .build();
    }
}