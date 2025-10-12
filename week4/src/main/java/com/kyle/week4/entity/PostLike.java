package com.kyle.week4.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostLike {
    private Long id;
    private Long userId;
    private Long postId;

    public PostLike(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public boolean isNew() {
        return id == null;
    }

    public boolean isLiked(Long userId, Long postId) {
        return this.userId.equals(userId) && this.postId.equals(postId);
    }
}
