package com.kyle.week4.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class Post extends BaseEntity {
    private Long id;
    private User user;
    private String title;
    private String content;
    private int likeCount;
    private int viewCount;
    private int commentCount;
    private boolean isDeleted;
    private List<String> images;

    @Builder
    public Post(User user, String title, String content, List<String> images) {
        super();
        this.images = images;
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

    public boolean isNew() {
        return id == null;
    }
}
