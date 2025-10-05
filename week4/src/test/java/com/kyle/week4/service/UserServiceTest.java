package com.kyle.week4.service;

import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.exception.ErrorCode;
import com.kyle.week4.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.clear();
    }

    @Test
    @DisplayName("사용자 정보를 저장한다. 사용자의 식별자는 최근 저장된 사용자의 식별자에서 1 증가한 값이다.")
    void createUser() {
        // given
        User user = createUser("test1@test.com", "test1", "test1");
        userRepository.save(user);

        UserCreateRequest request = UserCreateRequest.builder()
          .email("test2@test.com")
          .password("test1234!")
          .profileImage("test2")
          .nickname("test2")
          .build();

        // when
        Long userId = userService.createUser(request);

        // then
        User savedUser = userRepository.findById(userId).orElseThrow();
        assertThat(savedUser)
          .extracting("userId", "email", "nickname", "profileImage")
          .contains(2L, "test2@test.com", "test2", "test2");
    }

    @Test
    @DisplayName("이미 존재하는 닉네임으로 회원가입 시 예외가 발생한다.")
    void createUserDuplicateNicknameTest() {
        // given
        String nickname = "test";

        User user = createUser("test1@test.com", nickname, "test1");
        userRepository.save(user);

        UserCreateRequest request = UserCreateRequest.builder()
          .email("test@test.com")
          .nickname(nickname)
          .password("test1234!")
          .build();

        // when // then
        assertThatThrownBy(() -> userService.createUser(request))
          .isInstanceOf(CustomException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_NICKNAME_ERROR)
          .hasMessage("이미 가입된 닉네임입니다.");
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시 예외가 발생한다.")
    void createUserDuplicateEmailTest() {
        // given
        String email = "test@test.com";

        User user = createUser(email, "test1", "test1");
        userRepository.save(user);

        UserCreateRequest request = UserCreateRequest.builder()
          .email(email)
          .nickname("test2")
          .password("test1234!")
          .build();

        // when // then
        assertThatThrownBy(() -> userService.createUser(request))
          .isInstanceOf(CustomException.class)
          .hasMessage("이미 가입된 이메일입니다.");
    }

    private User createUser(String email, String nickname, String profileImage) {
        return User.builder()
          .email(email)
          .nickname(nickname)
          .profileImage(profileImage)
          .build();
    }
}