package com.kyle.week4.controller;

import com.kyle.week4.controller.request.LoginRequest;
import com.kyle.week4.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/sessions")
    public BaseResponse<Long> login(
      @RequestBody LoginRequest loginRequest,
      HttpServletRequest httpRequest
    ) {
        Long userId = authService.login(loginRequest);
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("userId", userId);
        return BaseResponse.ok(userId);
    }
}
