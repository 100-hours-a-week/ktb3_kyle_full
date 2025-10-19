package com.kyle.week4.controller.response;

import com.kyle.week4.entity.PostLike;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "게시글의 좋아요 수 및 사용자의 좋아요 여부 응답 DTO")
public class PostLikeResponse {

    @Schema(description = "좋아요수", example = "2345")
    private int likeCount;

    @Schema(description = "사용자의 좋아요 여부", example = "true")
    private boolean isLiked;

    public PostLikeResponse(int likeCount, boolean isLiked) {
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }
}
