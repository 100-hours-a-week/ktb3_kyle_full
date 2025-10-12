package com.kyle.week4.controller;

import com.kyle.week4.controller.response.PostLikeResponse;
import com.kyle.week4.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
public class PostLikeController {
    private final PostLikeService postLikeService;

    @PostMapping("/posts/{postId}/like")
    public ApiResponse<PostLikeResponse> like(
      @SessionAttribute("userId") Long userId,
      @PathVariable("postId") Long postId
    ) {
        return ApiResponse.ok(postLikeService.like(userId, postId));
    }
}
