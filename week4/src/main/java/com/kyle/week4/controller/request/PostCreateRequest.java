package com.kyle.week4.controller.request;

import com.kyle.week4.entity.Post;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostCreateRequest {
    @NotEmpty(message = "제목은 비어있을 수 없습니다.")
    @Max(value = 26, message = "제목은 최대 26자 까지 작성 가능합니다.")
    private String title;

    @NotEmpty(message = "내용은 비어있을 수 없습니다.")
    private String content;

    private List<String> images;

    public Post toEntity(Long userId) {
        return Post.builder()
          .title(title)
          .content(content)
          .images(images)
          .userId(userId)
          .build();
    }
}
