package com.kyle.week4.controller;

import com.kyle.week4.controller.request.LoginRequest;
import com.kyle.week4.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/sessions")
    public ApiResponse<Long> login(
      @RequestBody LoginRequest loginRequest,
      HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(authService.login(loginRequest, httpRequest));
    }
}
