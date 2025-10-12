package com.kyle.week4.controller.response;

import com.kyle.week4.entity.PostLike;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostLikeResponse {
    private int likeCount;
    private boolean isLiked;

    public PostLikeResponse(int likeCount, boolean isLiked) {
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }
}
