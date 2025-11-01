package com.kyle.week4.controller.response;

import com.kyle.week4.entity.PostImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "게시글 이미지 정보 응답 DTO")
public class PostImageResponse {
    @Schema(description = "게시글 이미지 ID")
    private Long postImageId;

    @Schema(description = "게시글 이미지 경로")
    private String postImagePath;

    public static PostImageResponse of(Long  postImageId, String postImagePath) {
        return new PostImageResponse(postImageId, postImagePath);
    }
}
