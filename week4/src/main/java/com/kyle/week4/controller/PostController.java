package com.kyle.week4.controller;

import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.response.PostDetailResponse;
import com.kyle.week4.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/posts")
    public ApiResponse<PostDetailResponse> createPost(
      @SessionAttribute("userId") Long userId,
      @RequestBody PostCreateRequest request
    ) {
        return ApiResponse.ok(postService.createPost(userId, request));
    }
}
