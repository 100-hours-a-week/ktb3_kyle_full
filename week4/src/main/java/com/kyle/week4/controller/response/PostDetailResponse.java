package com.kyle.week4.controller.response;

import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
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
    private LocalDateTime createdAt;

    @Builder
    public PostDetailResponse(Long id, String title, String content, String authorNickname, String authorProfileImage, int likeCount, int viewCount, int commentCount, boolean isAuthor, List<String> images, LocalDateTime createdAt) {
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
    }

    public static PostDetailResponse of(Post post, User user) {
        return PostDetailResponse.builder()
          .id(post.getId())
          .title(post.getTitle())
          .content(post.getContent())
          .likeCount(post.getLikeCount())
          .viewCount(post.getViewCount())
          .commentCount(post.getCommentCount())
          .isAuthor(post.getUserId().equals(user.getId()))
          .authorNickname(user.getNickname())
          .authorProfileImage(user.getProfileImage())
          .images(post.getImages())
          .createdAt(post.getCreatedAt())
          .build();
    }
}
