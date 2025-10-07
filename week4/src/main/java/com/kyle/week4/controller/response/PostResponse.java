package com.kyle.week4.controller.response;

import com.kyle.week4.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private int likeCount;
    private int viewCount;
    private int commentCount;
    private boolean isAuthor;
    private List<String> images;

    @Builder
    public PostResponse(Long id, String title, String content, int likeCount, int viewCount, int commentCount, boolean isAuthor, List<String> images) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.isAuthor = isAuthor;
        this.images = images;
    }

    public static PostResponse of(Post post, Long userId) {
        return PostResponse.builder()
          .id(post.getId())
          .title(post.getTitle())
          .content(post.getContent())
          .likeCount(post.getLikeCount())
          .viewCount(post.getViewCount())
          .commentCount(post.getCommentCount())
          .isAuthor(post.getUserId().equals(userId))
          .images(post.getImages())
          .build();
    }
}
