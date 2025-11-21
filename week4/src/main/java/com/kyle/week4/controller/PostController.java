package com.kyle.week4.controller;

import com.kyle.week4.controller.docs.PostControllerDocs;
import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.request.PostUpdateRequest;
import com.kyle.week4.controller.response.PostDetailResponse;
import com.kyle.week4.controller.response.PostResponse;
import com.kyle.week4.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController implements PostControllerDocs {
    private final PostService postService;

    @PostMapping(value = "/posts",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<PostDetailResponse> createPostAndImage(
            @SessionAttribute("userId") Long userId,
            @Valid @RequestPart(value = "request") PostCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return BaseResponse.created(postService.createPostAndImage(userId, request, images));
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
