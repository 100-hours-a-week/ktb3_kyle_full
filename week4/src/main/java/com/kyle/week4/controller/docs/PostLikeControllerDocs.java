package com.kyle.week4.controller.docs;

import com.kyle.week4.controller.BaseResponse;
import com.kyle.week4.controller.response.PostLikeResponse;
import com.kyle.week4.exception.ErrorCode;
import com.kyle.week4.swagger.annotation.ApiErrorResponse;
import com.kyle.week4.swagger.annotation.ApiErrorResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import static com.kyle.week4.exception.ErrorCode.*;

@Tag(name = "PostLike API", description = "게시글 좋아요 관련 API")
public interface PostLikeControllerDocs {

    @Operation(
      summary = "좋아요수와 좋아요 여부 조회",
      description = "### 해당 게시글의 좋아요수와 사용자의 좋아요 여부를 조회합니다."
    )
    @ApiResponse(
      responseCode = "200", description = "조회 성공",
      content = @Content(schema = @Schema(implementation = PostLikeResponse.class))
    )
    BaseResponse<PostLikeResponse> count(
      @Parameter(hidden = true)
      Long userId,
      @Parameter(
        name = "postId",
        description = "좋아요수와 좋아요 여부를 조회할 게시글의 ID",
        in = ParameterIn.PATH,
        required = true
      )
      Long postId
    );

    @Operation(
      summary = "게시글 좋아요 생성",
      description = "### 해당 게시글의 좋아요를 생성합니다."
    )
    @ApiResponse(
      responseCode = "200", description = "생성 성공",
      content = @Content(schema = @Schema(implementation = PostLikeResponse.class))
    )
    @ApiErrorResponses({ALREADY_LIKED_ERROR, POST_NOT_FOUND})
    BaseResponse<PostLikeResponse> like(
      @Parameter(hidden = true)
      Long userId,
      @Parameter(
        name = "postId",
        description = "좋아요를 생성할 게시글의 ID",
        in = ParameterIn.PATH,
        required = true
      )
      Long postId
    );

    @Operation(
      summary = "게시글 좋아요 삭제",
      description = "### 해당 게시글의 좋아요를 삭제합니다."
    )
    @ApiResponse(
      responseCode = "200", description = "삭제 성공",
      content = @Content(schema = @Schema(implementation = PostLikeResponse.class))
    )
    @ApiErrorResponses({POST_LIKE_NOT_FOUND, POST_NOT_FOUND})
    BaseResponse<PostLikeResponse> removeLike(
      @Parameter(hidden = true)
      Long userId,
      @Parameter(
        name = "postId",
        description = "좋아요를 삭제할 게시글의 ID",
        in = ParameterIn.PATH,
        required = true
      )
      Long postId
    );
}
