package com.kyle.week4.service;

import com.kyle.week4.IntegrationTestSupport;
import com.kyle.week4.controller.request.PasswordUpdateRequest;
import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.controller.request.UserProfileUpdateRequest;
import com.kyle.week4.controller.response.UserProfileResponse;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.user.UserJpaRepository;
import com.kyle.week4.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static com.kyle.week4.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

class UserServiceTest extends IntegrationTestSupport {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자 정보를 저장한다.")
    void createUser() {
        // given
        String imagePath = UUID.randomUUID().toString();
        given(imageUploader.upload(any()))
            .willReturn(imagePath);

        User user = createUser("test1@test.com", "test1");
        userRepository.save(user);

        UserCreateRequest request = UserCreateRequest.builder()
            .email("test2@test.com")
            .password("test1234!")
            .nickname("test2")
            .build();

        // when
        Long userId = userService.createUserAndImage(request, null);

        // then
        User savedUser = userRepository.findById(userId).orElseThrow();
        assertThat(savedUser)
            .extracting("email", "nickname", "profileImage")
            .containsExactlyInAnyOrder("test2@test.com", "test2", imagePath);
    }

    @Test
    @DisplayName("이미 존재하는 닉네임으로 회원가입 시 예외가 발생한다.")
    void createUserDuplicateNicknameTest() {
        // given
        String nickname = "test";

        User user = createUser("test1@test.com", nickname);
        userRepository.save(user);

        UserCreateRequest request = UserCreateRequest.builder()
            .email("test@test.com")
            .nickname(nickname)
            .password("test1234!")
            .build();

        // when // then
        assertThatThrownBy(() -> userService.createUserAndImage(request, null))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", DUPLICATE_NICKNAME_ERROR);
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시 예외가 발생한다.")
    void createUserDuplicateEmailTest() {
        // given
        String email = "test@test.com";

        User user = createUser(email, "test1");
        userRepository.save(user);

        UserCreateRequest request = UserCreateRequest.builder()
            .email(email)
            .nickname("test2")
            .password("test1234!")
            .build();

        // when // then
        assertThatThrownBy(() -> userService.createUserAndImage(request, null))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", DUPLICATE_EMAIL_ERROR);
    }

    @Test
    @DisplayName("사용자의 정보를 조회한다.")
    void getUserProfile() {
        // given
        User user = createUser("test1@test.com", "test1");
        userRepository.save(user);

        // when
        UserProfileResponse response = userService.getUserProfile(user.getId());

        // then
        assertThat(response)
            .extracting("email", "nickname")
            .containsExactlyInAnyOrder("test1@test.com", "test1");
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
    @DisplayName("사용자의 정보를 수정한다. 프로필 이미지를 변경하면 이미지 경로도 같이 변경된다.")
    void updateUserProfile() {
        // given
        String imagePath = UUID.randomUUID().toString();
        given(imageUploader.upload(any()))
            .willReturn(imagePath);
        willDoNothing().given(imageUploader).delete(anyString());

        MockMultipartFile profileImage = new MockMultipartFile(
            "image",                // 파라미터 이름
            "avatar.png",                  // 원본 파일명
            "image/png",                   // Content-Type
            "dummy image bytes".getBytes() // 파일 내용
        );

        User user = createUser("test1@test.com", "test1");
        userRepository.save(user);

        UserProfileUpdateRequest request = new UserProfileUpdateRequest("update", "update.jpg");

        // when
        UserProfileResponse response = userService.updateUserProfileAndImage(user.getId(), request, profileImage);

        // then
        assertThat(response)
            .extracting("email", "nickname", "profileImage")
            .containsExactlyInAnyOrder("test1@test.com", "update", imagePath);
    }

    @Test
    @DisplayName("사용자의 정보를 수정한다. 프로필 이미지를 변경하지 않으면 이미지 경로가 변경되지 않는다.")
    void updateUserProfile_noImage() {
        // given
        User user = createUser("test1@test.com", "test1");
        userRepository.save(user);

        UserProfileUpdateRequest request = new UserProfileUpdateRequest("update", "update.jpg");

        // when
        UserProfileResponse response = userService.updateUserProfileAndImage(user.getId(), request, null);

        // then
        assertThat(response)
            .extracting("email", "nickname", "profileImage")
            .containsExactlyInAnyOrder("test1@test.com", "update", user.getProfileImage());
    }

    @Test
    @DisplayName("사용자 정보 수정 시 사용자가 존재하지 않으면 예외가 발생한다.")
    void updateUserProfile_whenUserNotFound() {
        // given
        Long userId = 1L;
        UserProfileUpdateRequest request = new UserProfileUpdateRequest("update", "update.jpg");

        // when // then
        assertThatThrownBy(() -> userService.updateUserProfileAndImage(userId, request, null))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", USER_NOT_FOUND);
    }

    @Test
    @DisplayName("사용자 정보 수정 시 이전 닉네임과 같거나 이미 존재하는 닉네임일 경우 예외가 발생한다.")
    void updateUserProfile_whenDuplicateNickname() {
        // given
        User user = createUser("test1@test.com", "test1");
        userRepository.save(user);

        UserProfileUpdateRequest request = new UserProfileUpdateRequest("test1", "update.jpg");

        // when // then
        assertThatThrownBy(() -> userService.updateUserProfileAndImage(user.getId(), request, null))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", DUPLICATE_NICKNAME_ERROR);
    }

    @Test
    @DisplayName("사용자의 비밀번호를 수정한다.")
    void updatePassword() {
        // given
        String oldPassword = passwordEncoder.encode("oldPassword");
        String newPassword = "newPassword";

        User user = createUser("test1", "test1");
        user.encodePassword(oldPassword);
        userRepository.save(user);

        PasswordUpdateRequest request = new PasswordUpdateRequest(newPassword);

        // when
        userService.updatePassword(user.getId(), request);

        // then
        User findUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(passwordEncoder.matches(newPassword, findUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("비밀번호 수정 시 이전과 같은 비밀번호로 수정할 수 없습니다.")
    void updatePassword_newPasswordEqualToOldPassword() {
        // given
        String oldPassword = passwordEncoder.encode("oldPassword");
        String newPassword = "oldPassword";

        User user = createUser("test1", "test1");
        user.encodePassword(oldPassword);
        userRepository.save(user);

        PasswordUpdateRequest request = new PasswordUpdateRequest(newPassword);

        // when
        assertThatThrownBy(() -> userService.updatePassword(user.getId(), request))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", PASSWORD_SAME_BEFORE_ERROR);
    }

    @Test
    @DisplayName("사용자 탈퇴 시 Soft Delete를 적용한다.")
    void deleteUser() {
        // given
        User user = createUser("test1", "test1");
        User savedUser = userRepository.save(user);

        // when
        userService.deleteUser(savedUser.getId());

        // then
        User findUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(findUser.isDeleted()).isTrue();
    }

    private User createUser(String email, String nickname) {
        return User.builder()
            .email(email)
            .nickname(nickname)
            .build();
    }
}