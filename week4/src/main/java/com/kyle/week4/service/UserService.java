package com.kyle.week4.service;

import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.domain.User;
import com.kyle.week4.repository.UserRepository;
import com.kyle.week4.utils.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long createUser(UserCreateRequest request) {
        User user = request.toEntity();
        user.encodePassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }
}
