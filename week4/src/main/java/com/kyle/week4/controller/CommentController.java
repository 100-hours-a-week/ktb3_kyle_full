package com.kyle.week4.controller;

import com.kyle.week4.controller.request.CommentCreateRequest;
import com.kyle.week4.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<Long> createComment(
      @SessionAttribute("userId") Long userId,
      @PathVariable Long postId,
      @Valid @RequestBody CommentCreateRequest request
    ) {
        return ApiResponse.created(commentService.createComment(userId, postId, request));
    }
}
