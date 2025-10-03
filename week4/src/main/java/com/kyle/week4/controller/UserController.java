package com.kyle.week4.controller;

import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public ApiResponse<?> createUser(@RequestBody UserCreateRequest request) {
        return ApiResponse.created(userService.createUser(request));
    }
}
