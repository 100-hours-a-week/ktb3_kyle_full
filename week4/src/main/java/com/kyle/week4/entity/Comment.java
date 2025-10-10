package com.kyle.week4.entity;

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

    public void assignId(Long id) {
        this.id = id;
    }
}
