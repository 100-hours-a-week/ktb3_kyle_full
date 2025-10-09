package com.kyle.week4.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyle.week4.filter.AuthenticationFilter;
import com.kyle.week4.filter.AuthenticationProvider;
import com.kyle.week4.service.PostService;
import com.kyle.week4.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
  UserController.class,
  PostController.class
})
public class ControllerTestSupport {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected PostService postService;

    @MockitoBean
    protected AuthenticationProvider authenticationProvider;
}
