package com.kyle.week4.filter;

import com.kyle.week4.exception.CustomException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

import static com.kyle.week4.exception.ErrorCode.FAILED_AUTHENTICATE;

@Order(2)
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    private static final Set<String> EXCLUDED_PATHS = Set.of(
      "/users", "/auth/sessions"
    );

    private final AuthenticationProvider provider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (EXCLUDED_PATHS.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!provider.isAuthenticated(request, response)) {
            throw new CustomException(FAILED_AUTHENTICATE);
        }
        filterChain.doFilter(request, response);
    }
}
