package com.kyle.week4.controller.response;

import com.kyle.week4.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String authorNickname;
    private String authorProfileImage;
    private int likeCount;
    private int viewCount;
    private int commentCount;
    private boolean isAuthor;
    private List<String> images;
    private List<CommentResponse> comments;
    private LocalDateTime createdAt;

    @Builder
    public PostDetailResponse(Long id, String title, String content, String authorNickname, String authorProfileImage, int likeCount, int viewCount, int commentCount, boolean isAuthor, List<String> images, List<CommentResponse> comments, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorNickname = authorNickname;
        this.authorProfileImage = authorProfileImage;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.isAuthor = isAuthor;
        this.images = images;
        this.createdAt = createdAt;
        this.comments = comments;
    }

    public static PostDetailResponse of(Post post, Long userId, List<CommentResponse> comments) {
        return PostDetailResponse.builder()
          .id(post.getId())
          .title(post.getTitle())
          .content(post.getContent())
          .likeCount(post.getLikeCount())
          .viewCount(post.getViewCount())
          .commentCount(post.getCommentCount())
          .isAuthor(post.getUser().getId().equals(userId))
          .authorNickname(post.getUser().getNickname())
          .authorProfileImage(post.getUser().getProfileImage())
          .images(post.getImages())
          .createdAt(post.getCreatedAt())
          .comments(comments)
          .build();
    }
}
