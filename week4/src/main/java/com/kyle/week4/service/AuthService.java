package com.kyle.week4.service;

import com.kyle.week4.controller.request.LoginRequest;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.UserRepository;
import com.kyle.week4.utils.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kyle.week4.exception.ErrorCode.INVALID_EMAIL;
import static com.kyle.week4.exception.ErrorCode.INVALID_PASSWORD;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
          .orElseThrow(() -> new CustomException(INVALID_EMAIL));

        if (isNotSamePassword(request.getPassword(), user.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }
        return user.getId();
    }

    private boolean isNotSamePassword(String rawPassword, String encodedPassword) {
        return !passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
