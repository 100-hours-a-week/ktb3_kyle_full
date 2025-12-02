package com.kyle.week4.unit;

import com.kyle.week4.controller.request.PasswordUpdateRequest;
import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.controller.request.UserProfileUpdateRequest;
import com.kyle.week4.controller.response.UserProfileResponse;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.fixture.UserFixture;
import com.kyle.week4.fixture.UserRequestFixture;
import com.kyle.week4.repository.user.UserRepository;
import com.kyle.week4.service.UserService;
import com.kyle.week4.utils.ImageUploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.kyle.week4.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageUploader imageUploader;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    @DisplayName("새로운 사용자를 저장한다.")
    void createUser() {
        // given
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        UserCreateRequest request = UserRequestFixture.create();
        String encodedPassword = "encodedPassword";

        given(userRepository.existsByEmail(request.getEmail()))
            .willReturn(false);
        given(userRepository.existsByNickname(request.getNickname()))
            .willReturn(false);
        given(passwordEncoder.encode(request.getPassword()))
            .willReturn(encodedPassword);
        given(userRepository.save(any(User.class)))
            .willAnswer(invocation -> {
                User saved = invocation.getArgument(0, User.class);
                saved.assignId(1L);
                return saved;
            });

        // when
        userService.createUserAndImage(request, null);

        // then
        then(userRepository).should().save(captor.capture());
        User savedUser = captor.getValue();

        assertThat(savedUser.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("이미 가입된 이메일로 회원가입을 할 수 없다.")
    void createUser_isDuplicateEmail() {
        // given
        UserCreateRequest request = UserRequestFixture.create();
        given(userRepository.existsByEmail(request.getEmail()))
            .willReturn(true);

        // when // then
        assertThatThrownBy(() -> userService.createUserAndImage(request, null))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", DUPLICATE_EMAIL_ERROR);

        then(userRepository).should().existsByEmail(request.getEmail());
        then(userRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("이미 가입된 닉네임으로 회원가입을 할 수 없다.")
    void createUser_isDuplicateNickname() {
        // given
        UserCreateRequest request = UserRequestFixture.create();

        given(userRepository.existsByEmail(request.getEmail()))
            .willReturn(false);
        given(userRepository.existsByNickname(request.getNickname()))
            .willReturn(true);

        // when // then
        assertThatThrownBy(() -> userService.createUserAndImage(request, null))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", DUPLICATE_NICKNAME_ERROR);

        then(userRepository).should().existsByEmail(request.getEmail());
        then(userRepository).should().existsByNickname(request.getNickname());
        then(userRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("이미지 업로드 중 예외가 발생하면 회원가입을 할 수 없다.")
    void createUser_whenImageUploadFail() {
        // given
        MultipartFile mockImage = mock(MultipartFile.class);
        UserCreateRequest request = UserRequestFixture.create();

        given(userRepository.existsByEmail(request.getEmail()))
            .willReturn(false);
        given(userRepository.existsByNickname(request.getNickname()))
            .willReturn(false);
        given(imageUploader.upload(mockImage))
            .willThrow(new CustomException(GCS_IMAGE_UPLOAD_ERROR));

        // when // then
        assertThatThrownBy(() -> userService.createUserAndImage(request, mockImage))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", GCS_IMAGE_UPLOAD_ERROR);

        then(imageUploader).should().upload(mockImage);
        then(userRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("회원 정보를 수정한다.")
    void updateUser() {
        // given
        UserProfileUpdateRequest request = UserRequestFixture.update("update");
        User user = UserFixture.defaultUser();

        given(userRepository.existsByNickname(request.getNickname()))
            .willReturn(false);
        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));

        // when
        UserProfileResponse response = userService.updateUserProfileAndImage(1L, request, null);

        // then
        assertThat(response.getNickname()).isEqualTo("update");
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 회원정보를 수정할 수 없다.")
    void updateUser_whenUserNotFound() {
        // given
        UserProfileUpdateRequest request = UserRequestFixture.update("update");

        given(userRepository.existsByNickname(request.getNickname()))
            .willReturn(false);
        given(userRepository.findById(1L))
            .willReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> userService.updateUserProfileAndImage(1L, request, null))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", USER_NOT_FOUND);

        then(userRepository).should().findById(1L);
    }

    @Test
    @DisplayName("이미 가입된 닉네임으로 수정할 수 없다.")
    void updateUser_isDuplicateNickname() {
        // given
        UserProfileUpdateRequest request = UserRequestFixture.update("update");
        User user = UserFixture.defaultUser();

        given(userRepository.existsByNickname(request.getNickname()))
            .willReturn(true);

        // when // then
        assertThatThrownBy(() -> userService.updateUserProfileAndImage(1L, request, null))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", DUPLICATE_NICKNAME_ERROR);

        then(userRepository).should().existsByNickname(request.getNickname());
        then(userRepository).should(never()).findById(1L);
    }

    @Test
    @DisplayName("프로필 이미지를 변경하지 않으면 이전 이미지는 삭제되지 않고, 이미지 업로드는 수행되지 않는다.")
    void updateUser_whenImageUnchange() {
        // given
        UserProfileUpdateRequest request = UserRequestFixture.update("update");
        User user = UserFixture.defaultUser();
        String beforeProfileImage = user.getProfileImage();

        given(userRepository.existsByNickname(request.getNickname()))
            .willReturn(false);
        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));

        // when
        UserProfileResponse response = userService.updateUserProfileAndImage(1L, request, null);

        // then
        assertThat(response.getProfileImage()).isEqualTo(beforeProfileImage);
        then(imageUploader).should(never()).upload(any());
        then(imageUploader).should(never()).delete(any());
    }

    @Test
    @DisplayName("프로필 이미지를 변경하면 새로운 이미지를 업로드하고, 이전 이미지를 삭제한다.")
    void updateUser_whenImageChange() {
        // given
        MultipartFile mockImage = mock(MultipartFile.class);
        UserProfileUpdateRequest request = UserRequestFixture.update("update");
        User user = UserFixture.defaultUser();
        String beforeProfileImage = user.getProfileImage();

        given(userRepository.existsByNickname(request.getNickname()))
            .willReturn(false);
        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));
        given(imageUploader.upload(mockImage))
            .willReturn("change.jpg");
        willDoNothing().given(imageUploader).delete(beforeProfileImage);

        // when
        UserProfileResponse response = userService.updateUserProfileAndImage(1L, request, mockImage);

        // then
        assertThat(response.getProfileImage()).isEqualTo("change.jpg");

        InOrder inOrder = inOrder(imageUploader);
        then(imageUploader).should(inOrder).upload(mockImage);
        then(imageUploader).should(inOrder).delete(beforeProfileImage);
    }

    @Test
    @DisplayName("이미지 업로드 중 예외가 발생하면 회원정보를 수정할 수 없다.")
    void updateUser_whenImageUploadFail() {
        // given
        MultipartFile mockImage = mock(MultipartFile.class);
        UserProfileUpdateRequest request = UserRequestFixture.update("update");
        User user = UserFixture.defaultUser();
        String beforeProfileImage = user.getProfileImage();

        given(userRepository.existsByNickname(request.getNickname()))
            .willReturn(false);
        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));
        given(imageUploader.upload(mockImage))
            .willThrow(new CustomException(GCS_IMAGE_UPLOAD_ERROR));

        // when // then
        assertThatThrownBy(() -> userService.updateUserProfileAndImage(1L, request, mockImage))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", GCS_IMAGE_UPLOAD_ERROR);

        assertThat(user.getProfileImage()).isEqualTo(beforeProfileImage);

        then(imageUploader).should().upload(mockImage);
        then(imageUploader).should(never()).delete(user.getProfileImage());
    }

    @Test
    @DisplayName("비밀번호를 변경한다.")
    void updatePassword() {
        // given
        PasswordUpdateRequest request = UserRequestFixture.passwordUpdate("Qwer1234!");
        User user = UserFixture.defaultUser();
        String beforePassword = user.getPassword();
        String encodedPassword = "encodedPassword";

        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getPassword(), beforePassword))
            .willReturn(false);
        given(passwordEncoder.encode(request.getPassword()))
            .willReturn(encodedPassword);

        // when
        userService.updatePassword(1L, request);

        // then
        assertThat(user.getPassword()).isEqualTo(encodedPassword);

        then(passwordEncoder).should().matches(request.getPassword(), beforePassword);
        then(passwordEncoder).should().encode(request.getPassword());
    }

    @Test
    @DisplayName("비밀번호 변경 시 이전 비밀번호와 같으면 변경할 수 없다.")
    void updatePassword_sameBefore() {
        // given
        User user = UserFixture.defaultUser();
        PasswordUpdateRequest request = UserRequestFixture.passwordUpdate(user.getPassword());

        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getPassword(), user.getPassword()))
            .willReturn(true);

        // when // then
        assertThatThrownBy(() -> userService.updatePassword(1L, request))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", PASSWORD_SAME_BEFORE_ERROR);

        then(passwordEncoder).should().matches(request.getPassword(), user.getPassword());
        then(passwordEncoder).should(never()).encode(request.getPassword());
    }

    @Test
    @DisplayName("사용자 탈퇴 시 soft delete를 적용한다.")
    void deleteUser() {
        // given
        User user = UserFixture.defaultUser();

        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));

        // when
        userService.deleteUser(1L);

        // then
        assertThat(user.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("이미 탈퇴한 사용자는 다시 탈퇴할 수 없다.")
    void deleteUser_isAlreadyDeleted() {
        // given
        User user = UserFixture.deletedUser();

        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));

        // when // then
        assertThatThrownBy(() -> userService.deleteUser(1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode",ALREADY_DELETED_USER);
    }
}
