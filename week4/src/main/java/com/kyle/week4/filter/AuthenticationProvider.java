package com.kyle.week4.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationProvider {
    boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response);
}
