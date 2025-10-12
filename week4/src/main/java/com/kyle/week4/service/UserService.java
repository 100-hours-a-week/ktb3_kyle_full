package com.kyle.week4.service;

import com.kyle.week4.controller.request.LoginRequest;
import com.kyle.week4.controller.request.PasswordUpdateRequest;
import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.controller.request.UserProfileUpdateRequest;
import com.kyle.week4.controller.response.UserProfileResponse;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.UserRepository;
import com.kyle.week4.utils.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kyle.week4.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(DUPLICATE_EMAIL_ERROR);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(DUPLICATE_NICKNAME_ERROR);
        }
        User user = request.toEntity();
        user.encodePassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public UserProfileResponse getUserProfile(Long userId) {
        User user = findUserBy(userId);
        return UserProfileResponse.of(user);
    }

    public UserProfileResponse updateUserProfile(Long userId, UserProfileUpdateRequest request) {
        User user = findUserBy(userId);

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(DUPLICATE_NICKNAME_ERROR);
        }

        user.updateUserProfile(request);
        return UserProfileResponse.of(user);
    }

    public void updatePassword(Long userId, PasswordUpdateRequest request) {
        User user = findUserBy(userId);
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(PASSWORD_SAME_BEFORE_ERROR);
        }
        user.encodePassword(passwordEncoder.encode(request.getPassword()));
    }

    private User findUserBy(Long userId) {
        return userRepository.findById(userId)
          .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }
}
