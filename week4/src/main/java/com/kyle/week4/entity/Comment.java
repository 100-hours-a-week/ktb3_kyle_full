package com.kyle.week4.entity;

import com.kyle.week4.controller.request.CommentUpdateRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {
    private Long id;
    private User user;
    private Post post;
    private String content;
    private boolean isDeleted;

    @Builder
    public Comment(User user, Post post, String content) {
        this.user = user;
        this.post = post;
        this.content = content;
        this.isDeleted = false;
    }

    public boolean isNew() {
        return id == null;
    }

    public boolean isSamePost(Long postId) {
        return this.post.getId().equals(postId);
    }

    public boolean isSameUser(Long userId) {
        return this.user.getId().equals(userId);
    }

    public boolean isNotAuthor(Long userId) {
        return !isSameUser(userId);
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public void updateComment(CommentUpdateRequest request) {
        this.content = request.getContent();
    }
}
