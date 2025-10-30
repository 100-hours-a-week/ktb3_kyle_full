package com.kyle.week4.entity;

import com.kyle.week4.controller.request.PostUpdateRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
public class Post extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 26)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private int likeCount;

    private int viewCount;

    private int commentCount;

    private boolean isDeleted;

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private List<PostImage> postImages = new ArrayList<>();

    @Builder
    public Post(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.isDeleted = false;
        this.likeCount = 0;
        this.viewCount = 0;
        this.commentCount = 0;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public void increaseCommentCount() {
        commentCount++;
    }

    public void addPostImage(PostImage postImage) {
        postImages.add(postImage);
    }

    public void updatePost(PostUpdateRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
    }

    public void delete() {
        isDeleted = true;
    }

    public boolean isNew() {
        return id == null;
    }

    public boolean isNotAuthor(Long userId) {
        return !this.user.getId().equals(userId);
    }

    public boolean isNotDeleted() {
        return !isDeleted;
    }
}
