package com.kyle.week4.controller;

import com.kyle.week4.controller.docs.CommentControllerDocs;
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
public class CommentController implements CommentControllerDocs {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public BaseResponse<Long> createComment(
      @SessionAttribute("userId") Long userId,
      @PathVariable("postId") Long postId,
      @Valid @RequestBody CommentCreateRequest request
    ) {
        return BaseResponse.created(commentService.createComment(userId, postId, request));
    }

    @GetMapping("/posts/{postId}/comments")
    public BaseResponse<List<CommentResponse>> infiniteScroll(
      @SessionAttribute("userId") Long userId,
      @PathVariable("postId") Long postId,
      @RequestParam("limit") int limit,
      @RequestParam(value = "lastCommentId", required = false) Long lastCommentId
    ) {
        return BaseResponse.ok(commentService.infiniteScroll(userId, postId, lastCommentId, limit));
    }

    @PatchMapping("/posts/{postId}/comments/{commentId}")
    public BaseResponse<CommentResponse> updateComment(
      @SessionAttribute("userId") Long userId,
      @PathVariable("postId") Long postId,
      @PathVariable("commentId") Long commentId,
      @Valid @RequestBody CommentUpdateRequest request
    ) {
        return BaseResponse.ok(commentService.updateComment(userId, postId, commentId, request));
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public BaseResponse<?> deleteComment(
      @SessionAttribute("userId") Long userId,
      @PathVariable("postId") Long postId,
      @PathVariable("commentId") Long commentId
    ) {
        commentService.deleteComment(userId, postId, commentId);
        return BaseResponse.noContent();
    }
}
