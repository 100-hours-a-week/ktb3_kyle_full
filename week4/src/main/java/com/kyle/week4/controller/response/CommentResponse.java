package com.kyle.week4.controller.response;

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
}
