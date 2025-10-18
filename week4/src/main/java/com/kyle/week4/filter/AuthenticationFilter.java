package com.kyle.week4.filter;

import com.kyle.week4.exception.CustomException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static com.kyle.week4.exception.ErrorCode.FAILED_AUTHENTICATE;

@Order(2)
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String[] EXCLUDED_PATHS = {
      "/users",
      "/auth/sessions",
      "/swagger-ui/**"
    };

    private final AuthenticationProvider provider;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (!provider.isAuthenticated(request, response)) {
            System.out.println(path);
            throw new CustomException(FAILED_AUTHENTICATE);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();

        return Arrays.stream(EXCLUDED_PATHS)
          .anyMatch(path -> antPathMatcher.match(path, requestURI));
    }
}
