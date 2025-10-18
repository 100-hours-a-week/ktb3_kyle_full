package com.kyle.week4.controller;

import com.kyle.week4.controller.docs.UserControllerDocs;
import com.kyle.week4.controller.request.PasswordUpdateRequest;
import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.controller.request.UserProfileUpdateRequest;
import com.kyle.week4.controller.response.UserProfileResponse;
import com.kyle.week4.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {
    private final UserService userService;

    @PostMapping("/users")
    public BaseResponse<Long> createUser(@Valid @RequestBody UserCreateRequest request) {
        return BaseResponse.created(userService.createUser(request));
    }

    @GetMapping("/users/profile")
    public BaseResponse<UserProfileResponse> getUserProfile(
      @SessionAttribute("userId") Long userId
    ) {
        return BaseResponse.ok(userService.getUserProfile(userId));
    }

    @PatchMapping("/users/profile")
    public BaseResponse<UserProfileResponse> updateUserProfile(
      @SessionAttribute("userId") Long userId,
      @Valid @RequestBody UserProfileUpdateRequest request
    ) {
        return BaseResponse.ok(userService.updateUserProfile(userId, request));
    }

    @PatchMapping("/users/password")
    public BaseResponse<?> updatePassword(
      @SessionAttribute("userId") Long userId,
      @Valid @RequestBody PasswordUpdateRequest request
    ) {
        userService.updatePassword(userId, request);
        return BaseResponse.noContent();
    }
}
