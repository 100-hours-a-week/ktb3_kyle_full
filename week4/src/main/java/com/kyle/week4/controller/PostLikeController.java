package com.kyle.week4.controller;

import com.kyle.week4.controller.response.PostLikeResponse;
import com.kyle.week4.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/posts/{postId}/like")
    public ApiResponse<PostLikeResponse> removeLike(
      @SessionAttribute("userId") Long userId,
      @PathVariable("postId") Long postId
    ) {
        return ApiResponse.ok(postLikeService.removeLike(userId, postId));
    }
}
