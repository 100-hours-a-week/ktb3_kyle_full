package com.kyle.week4.controller.response;

import com.kyle.week4.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Schema(description = "게시글 상세 정보 응답 DTO")
public class PostDetailResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "게시글 제목입니다.")
    private String title;

    @Schema(description = "게시글 내용", example = "게시글 내용입니다.")
    private String content;

    @Schema(description = "작성자 닉네임", example = "kyle")
    private String authorNickname;

    @Schema(description = "작성자 프로필 이미지 경로", example = "kyle.jpg")
    private String authorProfileImage;

    @Schema(description = "게시글 조회수", example = "123")
    private int viewCount;

    @Schema(description = "게시글에 작성된 댓글 개수", example = "34")
    private int commentCount;

    @Schema(description = "게시글을 조회한 사용자가 해당 게시글의 작성자와 일치하는지 여부", example = "true")
    private boolean isAuthor;

    @Schema(description = "게시글 생성 날짜", example = "2025-10-12T20:16:04.3440899")
    private LocalDateTime createdAt;

    @Schema(description = "업로드 한 이미지 경로")
    private List<PostImageResponse> imagePaths;

    @Builder
    public PostDetailResponse(Long id, String title, String content, String authorNickname, String authorProfileImage, int viewCount, int commentCount, boolean isAuthor, List<PostImageResponse> imagePaths, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorNickname = authorNickname;
        this.authorProfileImage = authorProfileImage;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.isAuthor = isAuthor;
        this.createdAt = createdAt;
        this.imagePaths = imagePaths;
    }

    public static PostDetailResponse of(Post post, Long userId, int viewCount, List<PostImageResponse> imagePaths) {
        return PostDetailResponse.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .viewCount(viewCount)
            .commentCount(post.getCommentCount())
            .isAuthor(post.getUser().getId().equals(userId))
            .authorNickname(post.getUser().getNickname())
            .authorProfileImage(post.getUser().getProfileImage())
            .createdAt(post.getCreatedAt())
            .imagePaths(imagePaths)
            .build();
    }
}
