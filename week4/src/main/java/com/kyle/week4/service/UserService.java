package com.kyle.week4.service;

import com.kyle.week4.aop.CustomCacheEvict;
import com.kyle.week4.aop.CustomCacheable;
import com.kyle.week4.controller.request.PasswordUpdateRequest;
import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.controller.request.UserProfileUpdateRequest;
import com.kyle.week4.controller.response.UserProfileResponse;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.user.UserRepository;
import com.kyle.week4.utils.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.kyle.week4.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final ImageUploader imageUploader;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long createUserAndImage(UserCreateRequest request, MultipartFile image) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(DUPLICATE_EMAIL_ERROR);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(DUPLICATE_NICKNAME_ERROR);
        }

        User user = request.toEntity();
        String imagePath = imageUploader.upload(image);
        user.changeImage(imagePath);
        user.encodePassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    @CustomCacheable(cacheName = "UserProfile", key = "#userId")
    public UserProfileResponse getUserProfile(Long userId) {
        User user = findUserBy(userId);
        return UserProfileResponse.of(user);
    }

    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Transactional
    @CustomCacheEvict(cacheName = "UserProfile", key = "#userId")
    public UserProfileResponse updateUserProfileAndImage(Long userId, UserProfileUpdateRequest request, MultipartFile image) {
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(DUPLICATE_NICKNAME_ERROR);
        }

        User user = findUserBy(userId);

        if (image != null) {
            String imagePath = imageUploader.upload(image);
            imageUploader.delete(user.getProfileImage());
            user.changeImage(imagePath);
        }
        user.updateUserProfile(request);
        return UserProfileResponse.of(user);
    }

    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequest request) {
        User user = findUserBy(userId);
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(PASSWORD_SAME_BEFORE_ERROR);
        }
        user.encodePassword(passwordEncoder.encode(request.getPassword()));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserBy(userId);

        if (user.isDeleted()) {
            throw new CustomException(ALREADY_DELETED_USER);
        }

        user.withdraw();
    }

    private User findUserBy(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }
}
