package com.kyle.week4.controller.docs;

import com.kyle.week4.controller.BaseResponse;
import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.request.PostUpdateRequest;
import com.kyle.week4.controller.response.PostDetailResponse;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.kyle.week4.exception.ErrorCode.*;

@Tag(name = "Post API", description = "게시글 관련 API")
public interface PostControllerDocs {

    @Operation(
            summary = "게시글 생성",
            description =
                    """
                              ### 게시글을 생성합니다.
                              - 제목과 내용은 비어있으면 안됩니다.
                              - 제목은 최대 26자 까지 작성 가능합니다.
                            """
    )
    @ApiResponse(
            responseCode = "201", description = "게시글 생성 성공",
            content = @Content(schema = @Schema(implementation = PostDetailResponse.class))
    )
    @ApiErrorResponses({USER_NOT_FOUND})
    BaseResponse<PostDetailResponse> createPost(
            @Parameter(hidden = true)
            Long userId,
            PostCreateRequest request
    );

    @Operation(
            summary = "게시글 생성 - 이미지 업로드",
            description =
                    """
                              ### 게시글을 생성합니다.
                              - 제목과 내용은 비어있으면 안됩니다.
                              - 제목은 최대 26자 까지 작성 가능합니다.
                              - formData의 request 필드로 요청 필드 전달
                              - formData의 imaged 필드로 업로드 할 이미지 전달
                            """
    )
    @ApiResponse(
            responseCode = "201", description = "게시글 생성 성공",
            content = @Content(schema = @Schema(implementation = PostDetailResponse.class))
    )
    @ApiErrorResponses({USER_NOT_FOUND})
    BaseResponse<PostDetailResponse> createPostAndImage(
            @Parameter(hidden = true)
            Long userId,
            @Parameter(
                    name = "request",
                    description = "게시글 생성 본문(JSON)",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PostCreateRequest.class)
                    )
            )
            @RequestPart("request")
            PostCreateRequest request,
            @Parameter(
                    description = "업로드할 이미지",
                    name = "images",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))
                    )
            )
            @RequestPart(value = "images", required = false)
            List<MultipartFile> images
    );

    @Operation(
            summary = "게시글 무한 스크롤 조회",
            description =
                    """
                            ### 게시글 ID 기반으로 게시글 목록을 최신순으로 조회합니다.
                            - lastPostId가 null일 경우 가장 최신 게시글을 limit 개수만큼 반환
                            - lastPostId가 null이 아닐 경우 lastPostId 이후의 최신 게시글을 limit 개수만큼 반환
                            """
    )
    @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PostResponse.class))
            )
    )
    BaseResponse<List<PostResponse>> infiniteScroll(
            @Parameter(
                    name = "limit",
                    description = "한번 조회 시 지정한 수만큼 게시글이 조회됩니다.",
                    in = ParameterIn.QUERY,
                    required = true
            )
            int limit,
            @Parameter(
                    name = "lastPostId",
                    description = "게시글 목록에서 마지막 게시글 ID로 최초 조회 시 해당 값을 지정하지 않습니다.",
                    in = ParameterIn.QUERY
            )
            Long lastPostId
    );

    @Operation(
            summary = "게시글 상세 정보 조회",
            description = "### 게시글의 상세 정보를 조회합니다."
    )
    @ApiResponse(
            responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PostDetailResponse.class))
    )
    @ApiErrorResponses({POST_NOT_FOUND})
    BaseResponse<PostDetailResponse> getPostDetail(
            @Parameter(
                    name = "postId",
                    description = "조회할 게시글의 ID",
                    in = ParameterIn.PATH,
                    required = true
            )
            Long postId,
            @Parameter(hidden = true)
            Long userId
    );

    @Operation(
            summary = "게시글 수정",
            description =
                    """
                            ### 게시글의 제목과 내용, 업로드 이미지를 수정합니다.
                            - 제목과 내용은 비어있으면 안됩니다.
                            - 제목은 최대 26자 까지 작성 가능합니다.
                            """
    )
    @ApiResponse(
            responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = PostDetailResponse.class))
    )
    @ApiErrorResponses({POST_NOT_FOUND, PERMISSION_DENIED})
    BaseResponse<PostDetailResponse> updatePost(
            @Parameter(hidden = true)
            Long userId,
            @Parameter(
                    name = "postId",
                    description = "수정할 게시글의 ID",
                    in = ParameterIn.PATH,
                    required = true
            )
            Long postId,
            PostUpdateRequest request
    );

    @Operation(
            summary = "게시글 삭제",
            description = "### 게시글을 삭제합니다.(Soft Delete)"
    )
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiErrorResponses({POST_NOT_FOUND, PERMISSION_DENIED})
    BaseResponse<?> deletePost(
            @Parameter(hidden = true)
            Long userId,
            @Parameter(
                    name = "postId",
                    description = "삭제할 게시글의 ID",
                    in = ParameterIn.PATH,
                    required = true
            )
            Long postId
    );
}
