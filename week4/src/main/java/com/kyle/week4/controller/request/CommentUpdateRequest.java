package com.kyle.week4.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "댓글 수정 요청 DTO")
public class CommentUpdateRequest {
    @NotEmpty(message = "댓글 내용은 비어있을 수 없습니다.")
    @Schema(description = "수정할 댓글 내용", example = "수정된 댓글 내용입니다.")
    private String content;

    public CommentUpdateRequest(String content) {
        this.content = content;
    }
}
