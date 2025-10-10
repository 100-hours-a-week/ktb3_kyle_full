package com.kyle.week4.controller.response;

import com.kyle.week4.entity.Comment;
import com.kyle.week4.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {
    private Long id;
    private String content;
    private String authorNickname;
    private String authorProfileImage;
    private LocalDateTime createdAt;
    private boolean isAuthor;

    @Builder
    public CommentResponse(Long id, String content, String authorNickname, String authorProfileImage, LocalDateTime createdAt, boolean isAuthor) {
        this.id = id;
        this.content = content;
        this.authorNickname = authorNickname;
        this.authorProfileImage = authorProfileImage;
        this.createdAt = createdAt;
        this.isAuthor = isAuthor;
    }

    public static CommentResponse of(Comment comment, Long userId) {
        return CommentResponse.builder()
          .id(comment.getId())
          .content(comment.getContent())
          .authorNickname(comment.getUser().getNickname())
          .authorProfileImage(comment.getUser().getProfileImage())
          .createdAt(comment.getCreatedAt())
          .isAuthor(comment.isSameUser(userId))
          .build();
    }
}
