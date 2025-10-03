package com.kyle.week4.service;

import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.domain.User;
import com.kyle.week4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long createUser(UserCreateRequest request) {
        User user = request.toEntity();
        return userRepository.save(user);
    }
}
