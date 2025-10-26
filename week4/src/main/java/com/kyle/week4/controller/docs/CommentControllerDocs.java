package com.kyle.week4.controller.docs;

import com.kyle.week4.controller.BaseResponse;
import com.kyle.week4.controller.request.CommentCreateRequest;
import com.kyle.week4.controller.request.CommentUpdateRequest;
import com.kyle.week4.controller.response.CommentResponse;
import com.kyle.week4.controller.response.PostLikeResponse;
import com.kyle.week4.controller.response.PostResponse;
import com.kyle.week4.swagger.annotation.ApiErrorResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import static com.kyle.week4.exception.ErrorCode.*;

@Tag(name = "Comment API", description = "게시글 댓글 관련 API")
public interface CommentControllerDocs {
    @Operation(
      summary = "댓글 생성",
      description =
        """
            ### 댓글을 생성합니다.
            - 댓글의 내용은 비어있으면 안됩니다.
          """
    )
    @ApiResponse(
      responseCode = "201", description = "댓글 생성 성공",
      content = @Content(schema = @Schema(implementation = Long.class))
    )
    @ApiErrorResponses({USER_NOT_FOUND})
    BaseResponse<Long> createComment(
      @Parameter(hidden = true)
      Long userId,
      @Parameter(
        name = "postId",
        description = "댓글을 작성할 게시글의 ID",
        in = ParameterIn.PATH,
        required = true
      )
      Long postId,
      CommentCreateRequest request
    );

    @Operation(
      summary = "댓글 무한 스크롤 조회",
      description =
        """
          ### 댓글 ID 기반으로 댓글 목록을 작성순으로 조회합니다.
          - lastCommentId null일 경우 댓글 작성순으로 limit 개수만큼 반환
          - lastCommentId가 null이 아닐 경우 lastCommentId 이후에 작성된 댓글 limit 개수만큼 반환
          """
    )
    @ApiResponse(
      responseCode = "200", description = "댓글 목록 조회 성공",
      content = @Content(
        mediaType = "application/json",
        array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class))
      )
    )
    BaseResponse<List<CommentResponse>> infiniteScroll(
      @Parameter(hidden = true)
      Long userId,
      @Parameter(
        name = "게시글 ID",
        description = "댓글을 조회할 게시글의 ID",
        in = ParameterIn.PATH,
        required = true
      )
      Long postId,
      @Parameter(
        name = "limit",
        description = "한번 조회 시 지정한 수만큼 댓글이 조회됩니다.",
        in = ParameterIn.QUERY,
        required = true
      )
      int limit,
      @Parameter(
        name = "lastCommentId",
        description = "댓글 목록에서 마지막 댓글 ID로 최초 조회 시 해당 값을 지정하지 않습니다.",
        in = ParameterIn.QUERY
      )
      Long lastCommentId
    );

    @Operation(
      summary = "댓글 수정",
      description =
        """
          ### 댓글을 수정합니다.
          - 댓글의 내용은 비어있으면 안됩니다.
          """
    )
    @ApiResponse(
      responseCode = "200", description = "수정 성공",
      content = @Content(schema = @Schema(implementation = CommentResponse.class))
    )
    @ApiErrorResponses({POST_NOT_FOUND, COMMENT_NOT_FOUND, PERMISSION_DENIED})
    BaseResponse<CommentResponse> updateComment(
      @Parameter(hidden = true)
      Long userId,
      @Parameter(
        name = "postId",
        description = "댓글을 수정할 게시글의 ID",
        in = ParameterIn.PATH,
        required = true
      )
      Long postId,
      @Parameter(
        name = "commentId",
        description = "수정할 댓글의 ID",
        in = ParameterIn.PATH,
        required = true
      )
      Long commentId,
      CommentUpdateRequest request
    );

    @Operation(
      summary = "댓글 삭제",
      description = "### 댓글을 삭제합니다.(Soft Delete)"
    )
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiErrorResponses({POST_NOT_FOUND, COMMENT_NOT_FOUND, PERMISSION_DENIED})
    BaseResponse<?> deleteComment(
      @Parameter(hidden = true)
      Long userId,
      @Parameter(
        name = "postId",
        description = "댓글을 삭제할 게시글의 ID",
        in = ParameterIn.PATH,
        required = true
      )
      Long postId,
      @Parameter(
        name = "commentId",
        description = "삭제할 댓글의 ID",
        in = ParameterIn.PATH,
        required = true
      )
      Long commentId
    );
}
