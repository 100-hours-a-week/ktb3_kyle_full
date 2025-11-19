package com.kyle.week4.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyle.week4.controller.request.LoginRequest;
import com.kyle.week4.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.IOException;
import java.util.Map;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final SecurityContextRepository securityContextRepository;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;

    public LoginFilter(AuthenticationManager authenticationManager,
                       SecurityContextRepository repository,
                       SessionAuthenticationStrategy sessionStrategy) {
        super(authenticationManager);
        this.securityContextRepository = repository;
        this.sessionAuthenticationStrategy = sessionStrategy;
        setFilterProcessesUrl("/auth/sessions");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            throw new AuthenticationServiceException("지원하지 않는 HTTP 메서드입니다.");
        }

        if (request.getContentType() == null || !request.getContentType().contains("application/json")) {
            throw new AuthenticationServiceException("JSON 타입만 허용합니다");
        }

        try {
            LoginRequest loginRequest = OBJECT_MAPPER.readValue(request.getInputStream(), LoginRequest.class);

            String username = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            return this.getAuthenticationManager().authenticate(token);
        } catch (IOException e) {
            throw new AuthenticationServiceException("로그인 요청 파싱 실패입니다.", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);

        sessionAuthenticationStrategy.onAuthentication(authResult, request, response);
        securityContextRepository.saveContext(context, request, response);

        HttpSession session = request.getSession(true);
        CustomUserDetails principal = (CustomUserDetails) authResult.getPrincipal();
        session.setAttribute("userId", principal.getUserId());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
            OBJECT_MAPPER.writeValueAsString(Map.of("success", true, "userId", principal.getUserId()))
        );
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        String message;
        if (failed instanceof BadCredentialsException) {
            message = "비밀번호가 일치하지 않습니다.";
        } else if (failed instanceof UsernameNotFoundException) {
            message = "이메일이 일치하지 않습니다.";
        } else {
            message = "인증에 실패했습니다.";
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
            OBJECT_MAPPER.writeValueAsString(Map.of("success", false, "message", message))
        );
    }
}
