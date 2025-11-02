package com.kyle.week4.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CommentCount {
    @Id
    private Long postId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private int count;

    @Version
    private Long version;

    @Builder
    public CommentCount(Post post, int count) {
        this.post = post;
        this.count = count;
    }

    public void increase() {
        count++;
    }
}
