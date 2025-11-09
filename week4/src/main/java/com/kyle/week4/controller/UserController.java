package com.kyle.week4.controller;

import com.kyle.week4.controller.docs.UserControllerDocs;
import com.kyle.week4.controller.request.PasswordUpdateRequest;
import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.controller.request.UserProfileUpdateRequest;
import com.kyle.week4.controller.response.UserProfileResponse;
import com.kyle.week4.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {
    private final UserService userService;

    @PostMapping(value = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Long> createUser(
        @Valid @RequestPart(value = "request") UserCreateRequest request,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return BaseResponse.created(userService.createUserAndImage(request, image));
    }

    @GetMapping("/users/profile")
    public BaseResponse<UserProfileResponse> getUserProfile(
        @SessionAttribute("userId") Long userId
    ) {
        return BaseResponse.ok(userService.getUserProfile(userId));
    }

    @GetMapping("/users/email/duplicate")
    public BaseResponse<Boolean> checkEmailDuplicate(
        @RequestParam("email") String email
    ) {
        return BaseResponse.ok(userService.checkEmailDuplicate(email));
    }

    @GetMapping("/users/nickname/duplicate")
    public BaseResponse<Boolean> checkNicknameDuplicate(
        @RequestParam("nickname") String nickname
    ) {
        return BaseResponse.ok(userService.checkNicknameDuplicate(nickname));
    }

    @PatchMapping("/users/profile")
    public BaseResponse<UserProfileResponse> updateUserProfile(
        @SessionAttribute("userId") Long userId,
        @Valid @RequestPart(value = "request") UserProfileUpdateRequest request,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return BaseResponse.ok(userService.updateUserProfileAndImage(userId, request, image));
    }

    @PatchMapping("/users/password")
    public BaseResponse<?> updatePassword(
        @SessionAttribute("userId") Long userId,
        @Valid @RequestBody PasswordUpdateRequest request
    ) {
        userService.updatePassword(userId, request);
        return BaseResponse.noContent();
    }

    @DeleteMapping("/users")
    public BaseResponse<Boolean> deleteUser(
        @SessionAttribute("userId") Long userId
    ) {
        userService.deleteUser(userId);
        return BaseResponse.noContent();
    }
}
