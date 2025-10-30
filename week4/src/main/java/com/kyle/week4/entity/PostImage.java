package com.kyle.week4.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String imagePath;

    public PostImage(String imagePath) {
        this.imagePath = imagePath;
    }

    public void connectPost(Post post) {
        this.post = post;

        if (!post.getPostImages().contains(this)) {
            post.getPostImages().add(this);
        }
    }
}
