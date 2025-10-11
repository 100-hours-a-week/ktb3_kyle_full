package com.kyle.week4.controller;

import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.controller.response.UserProfileResponse;
import com.kyle.week4.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public ApiResponse<Long> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.created(userService.createUser(request));
    }

    @GetMapping("/users/profile")
    public ApiResponse<UserProfileResponse> getUserProfile(
      @SessionAttribute("userId") Long userId
    ) {
        return ApiResponse.ok(userService.getUserProfile(userId));
    }
}
