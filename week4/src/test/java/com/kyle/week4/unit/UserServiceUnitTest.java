package com.kyle.week4.unit;

import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.fixture.UserRequestFixture;
import com.kyle.week4.repository.user.UserRepository;
import com.kyle.week4.service.UserService;
import com.kyle.week4.utils.ImageUploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

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
}
