package com.kyle.week4.controller;

import com.kyle.week4.controller.docs.AuthControllerDocs;
import com.kyle.week4.controller.request.LoginRequest;
import com.kyle.week4.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {
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

    @DeleteMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false); // 세션이 있을 때만 가져오기
        if (session != null) {
            session.invalidate(); // 세션 자체를 완전히 삭제
        }
        return BaseResponse.noContent();
    }
}
