package com.kyle.week4.controller.response;

import com.kyle.week4.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "게시글 목록에 포함하는 게시글 정보 응답 DTO")
public class PostResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "게시글 제목입니다.")
    private String title;

    @Schema(description = "작성자 닉네임", example = "kyle")
    private String authorNickname;

    @Schema(description = "작성자 프로필 이미지 경로", example = "kyle.jpg")
    private String authorProfileImage;

    @Schema(description = "게시글 좋아요수", example = "4567")
    private int likeCount;

    @Schema(description = "게시글 조회수", example = "123")
    private int viewCount;

    @Schema(description = "게시글에 작성된 댓글 개수", example = "34")
    private int commentCount;

    @Schema(description = "게시글 생성 날짜", example = "2025-10-12T20:16:04.3440899")
    private LocalDateTime createdAt;

    @Builder
    public PostResponse(Long id, String title, String authorNickname, String authorProfileImage, int likeCount, int viewCount, int commentCount, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.authorNickname = authorNickname;
        this.authorProfileImage = authorProfileImage;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
    }

    public static PostResponse of(Post post, int viewCount, int likeCount) {
        return PostResponse.builder()
          .id(post.getId())
          .title(post.getTitle())
          .likeCount(likeCount)
          .viewCount(viewCount)
          .commentCount(post.getCommentCount())
          .authorNickname(post.getUser().getNickname())
          .authorProfileImage(post.getUser().getProfileImage())
          .createdAt(post.getCreatedAt())
          .build();
    }
}
