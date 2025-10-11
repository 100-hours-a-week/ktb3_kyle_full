package com.kyle.week4.controller;

import com.kyle.week4.controller.request.CommentCreateRequest;
import com.kyle.week4.controller.request.CommentUpdateRequest;
import com.kyle.week4.controller.response.CommentResponse;
import com.kyle.week4.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<Long> createComment(
      @SessionAttribute("userId") Long userId,
      @PathVariable("postId") Long postId,
      @Valid @RequestBody CommentCreateRequest request
    ) {
        return ApiResponse.created(commentService.createComment(userId, postId, request));
    }

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<List<CommentResponse>> infiniteScroll(
      @SessionAttribute("userId") Long userId,
      @PathVariable("postId") Long postId,
      @RequestParam("limit") int limit,
      @RequestParam(value = "lastCommentId", required = false) Long lastCommentId
    ) {
        return ApiResponse.ok(commentService.infiniteScroll(userId, postId, lastCommentId, limit));
    }

    @PatchMapping("/posts/{postId}/comments/{commentId}")
    public ApiResponse<CommentResponse> updateComment(
      @SessionAttribute("userId") Long userId,
      @PathVariable("postId") Long postId,
      @PathVariable("commentId") Long commentId,
      @Valid @RequestBody CommentUpdateRequest request
    ) {
        return ApiResponse.ok(commentService.updateComment(userId, postId, commentId, request));
    }
}
