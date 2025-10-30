package com.kyle.week4.service;

import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.controller.request.UserProfileUpdateRequest;
import com.kyle.week4.controller.response.UserProfileResponse;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.MemoryClearRepository;
import com.kyle.week4.repository.user.UserJpaRepository;
import com.kyle.week4.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kyle.week4.exception.ErrorCode.DUPLICATE_NICKNAME_ERROR;
import static com.kyle.week4.exception.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // TODO: 테스트에만 필요한 deleteAllInBatch를 인터페이스에 포함하여야 할까?
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private List<MemoryClearRepository> memoryClearRepositoryList;

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
        memoryClearRepositoryList.forEach(MemoryClearRepository::clear);
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
          .extracting("email", "nickname", "profileImage")
          .containsExactlyInAnyOrder("test2@test.com", "test2", "test2");
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
          .hasFieldOrPropertyWithValue("errorCode", DUPLICATE_NICKNAME_ERROR)
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

    @Test
    @DisplayName("사용자의 정보를 조회한다.")
    void getUserProfile() {
        // given
        User user = createUser("test1@test.com", "test1", "image.jpg");
        userRepository.save(user);

        // when
        UserProfileResponse response = userService.getUserProfile(user.getId());

        // then
        assertThat(response)
          .extracting("email", "nickname", "profileImage")
          .containsExactlyInAnyOrder("test1@test.com", "test1", "image.jpg");
    }

    @Test
    @DisplayName("사용자 정보 조회 시 사용자가 존재하지 않으면 예외가 발생한다.")
    void getUserProfile_whenUserNotFound() {
        // given
        Long userId = 1L;

        // when // then
        assertThatThrownBy(() -> userService.getUserProfile(userId))
          .isInstanceOf(CustomException.class)
          .hasFieldOrPropertyWithValue("errorCode", USER_NOT_FOUND);
    }

    @Test
    @DisplayName("사용자의 정보를 수정한다.")
    void updateUserProfile() {
        // given
        User user = createUser("test1@test.com", "test1", "image.jpg");
        userRepository.save(user);

        UserProfileUpdateRequest request = new UserProfileUpdateRequest("update", "update.jpg");

        // when
        UserProfileResponse response = userService.updateUserProfile(user.getId(), request);

        // then
        assertThat(response)
          .extracting("email", "nickname", "profileImage")
          .containsExactlyInAnyOrder("test1@test.com", "update", "update.jpg");
    }

    @Test
    @DisplayName("사용자 정보 수정 시 사용자가 존재하지 않으면 예외가 발생한다.")
    void updateUserProfile_whenUserNotFound() {
        // given
        Long userId = 1L;
        UserProfileUpdateRequest request = new UserProfileUpdateRequest("update", "update.jpg");

        // when // then
        assertThatThrownBy(() -> userService.updateUserProfile(userId, request))
          .isInstanceOf(CustomException.class)
          .hasFieldOrPropertyWithValue("errorCode", USER_NOT_FOUND);
    }

    @Test
    @DisplayName("사용자 정보 수정 시 이전 닉네임과 같거나 이미 존재하는 닉네임일 경우 예외가 발생한다.")
    void updateUserProfile_whenDuplicateNickname() {
        // given
        User user = createUser("test1@test.com", "test1", "image.jpg");
        userRepository.save(user);

        UserProfileUpdateRequest request = new UserProfileUpdateRequest("test1", "update.jpg");

        // when // then
        assertThatThrownBy(() -> userService.updateUserProfile(user.getId(), request))
          .isInstanceOf(CustomException.class)
          .hasFieldOrPropertyWithValue("errorCode", DUPLICATE_NICKNAME_ERROR);
    }

    private User createUser(String email, String nickname, String profileImage) {
        return User.builder()
          .email(email)
          .nickname(nickname)
          .profileImage(profileImage)
          .build();
    }
}