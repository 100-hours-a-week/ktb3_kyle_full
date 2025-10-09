package com.kyle.week4.controller.response;

import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponse {
    private Long id;
    private String title;
    private String authorNickname;
    private String authorProfileImage;
    private int likeCount;
    private int viewCount;
    private int commentCount;
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

    public static PostResponse of(Post post, User user) {
        return PostResponse.builder()
          .id(post.getId())
          .title(post.getTitle())
          .likeCount(post.getLikeCount())
          .viewCount(post.getViewCount())
          .commentCount(post.getCommentCount())
          .authorNickname(user.getNickname())
          .authorProfileImage(user.getProfileImage())
          .createdAt(post.getCreatedAt())
          .build();
    }
}
