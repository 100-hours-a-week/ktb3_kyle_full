package com.kyle.week4.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "게시글 수정 요청 DTO")
public class PostUpdateRequest {
    @NotEmpty(message = "제목은 비어있을 수 없습니다.")
    @Size(max = 26, message = "제목은 최대 26자 까지 작성 가능합니다.")
    @Schema(description = "수정할 제목", example = "수정된 제목입니다.")
    private String title;

    @NotEmpty(message = "내용은 비어있을 수 없습니다.")
    @Schema(description = "수정할 내용", example = "수정된 내용입니다.")
    private String content;

    @Schema(description = "업로드 한 이미지 경로", example = "[\"image1.jpg\", \"image2.jpg\"]")
    private List<String> images;

    @Builder
    public PostUpdateRequest(String title, String content, List<String> images) {
        this.title = title;
        this.content = content;
        this.images = images;
    }
}
