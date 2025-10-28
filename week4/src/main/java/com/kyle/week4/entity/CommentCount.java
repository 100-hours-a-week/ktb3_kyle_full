package com.kyle.week4.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CommentCount {
    @Id
    private Long postId;

    private int commentCount;

    @Version
    private Long version;

    @Builder
    public CommentCount(Long postId, int commentCount) {
        this.postId = postId;
        this.commentCount = commentCount;
    }

    public void increase() {
        commentCount++;
    }
}
