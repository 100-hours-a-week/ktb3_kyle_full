package com.kyle.week4.entity;

import com.kyle.week4.controller.request.CommentUpdateRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(columnDefinition = "LONGTEXT")
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

    public boolean canDisplay(Long postId) {
        return this.post.getId().equals(postId) && !isDeleted;
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

    public void delete() {
        isDeleted = true;
    }
}
