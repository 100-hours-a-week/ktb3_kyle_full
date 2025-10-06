package com.kyle.week4.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionAuthenticationProvider implements AuthenticationProvider {

    @Override
    public boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("userId") != null;
    }
}
