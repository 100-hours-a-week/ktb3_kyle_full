package com.kyle.week4.controller.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {
    @NotEmpty(message = "제목은 비어있을 수 없습니다.")
    @Size(max = 26, message = "제목은 최대 26자 까지 작성 가능합니다.")
    private String title;

    @NotEmpty(message = "내용은 비어있을 수 없습니다.")
    private String content;

    private List<String> images;

    @Builder
    public PostUpdateRequest(String title, String content, List<String> images) {
        this.title = title;
        this.content = content;
        this.images = images;
    }
}
