package com.kyle.week4.controller.request;

import com.kyle.week4.entity.Comment;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {
    @NotEmpty(message = "댓글 내용은 비어있을 수 없습니다.")
    private String content;

    public CommentCreateRequest(String content) {
        this.content = content;
    }

    public Comment toEntity(User user, Post post) {
        return Comment.builder()
          .user(user)
          .post(post)
          .content(content)
          .build();
    }
}
