package com.kyle.week4.controller;

import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.request.PostUpdateRequest;
import com.kyle.week4.controller.response.PostDetailResponse;
import com.kyle.week4.controller.response.PostResponse;
import com.kyle.week4.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/posts")
    public BaseResponse<PostDetailResponse> createPost(
      @SessionAttribute("userId") Long userId,
      @Valid @RequestBody PostCreateRequest request
    ) {
        return BaseResponse.created(postService.createPost(userId, request));
    }

    @GetMapping("/posts")
    public BaseResponse<List<PostResponse>> infiniteScroll(
      @RequestParam("limit") int limit,
      @RequestParam(value = "lastPostId", required = false) Long lastPostId
    ) {
        return BaseResponse.ok(postService.infiniteScroll(lastPostId, limit));
    }

    @GetMapping("/posts/{postId}")
    public BaseResponse<PostDetailResponse> getPostDetail(
      @PathVariable("postId") Long postId,
      @SessionAttribute("userId") Long userId
    ) {
        return BaseResponse.ok(postService.getPostDetail(userId, postId));
    }

    @PatchMapping("/posts/{postId}")
    public BaseResponse<PostDetailResponse> updatePost(
      @SessionAttribute("userId") Long userId,
      @PathVariable("postId") Long postId,
      @Valid @RequestBody PostUpdateRequest request
    ) {
        return BaseResponse.ok(postService.updatePost(userId, postId, request));
    }

    @DeleteMapping("/posts/{postId}")
    public BaseResponse<?> deletePost(
      @SessionAttribute("userId") Long userId,
      @PathVariable("postId") Long postId
    ) {
        postService.deletePost(userId, postId);
        return BaseResponse.noContent();
    }
}
