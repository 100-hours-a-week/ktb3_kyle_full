package com.kyle.week4.controller.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentUpdateRequest {
    @NotEmpty(message = "댓글 내용은 비어있을 수 없습니다.")
    private String content;

    public CommentUpdateRequest(String content) {
        this.content = content;
    }
}
