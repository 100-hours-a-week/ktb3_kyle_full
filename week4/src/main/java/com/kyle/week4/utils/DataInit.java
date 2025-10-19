package com.kyle.week4.utils;

import com.kyle.week4.controller.request.CommentCreateRequest;
import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.request.UserCreateRequest;
import com.kyle.week4.controller.response.PostDetailResponse;
import com.kyle.week4.repository.UserMemoryRepository;
import com.kyle.week4.service.CommentService;
import com.kyle.week4.service.PostService;
import com.kyle.week4.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInit {
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    private final UserMemoryRepository userRepository;

    //@PostConstruct
    public void init() {
        UserCreateRequest userCreateRequest1 = UserCreateRequest.builder()
          .email("test1@test.com")
          .password("Test1234!")
          .nickname("kyle")
          .profileImage("image.jpg")
          .build();

        UserCreateRequest userCreateRequest2 = UserCreateRequest.builder()
          .email("other@test.com")
          .password("Test1234!")
          .nickname("other")
          .profileImage("other.jpg")
          .build();

        Long myId = userService.createUser(userCreateRequest1);
        Long otherId = userService.createUser(userCreateRequest2);

        List<Long> postIds = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            PostCreateRequest postCreateRequest = PostCreateRequest.builder()
              .title("제목 " + i)
              .content("내용 " + i)
              .images(List.of("image.jpg"))
              .build();

            Long userId = i % 2 == 0 ? myId : otherId;
            PostDetailResponse response = postService.createPost(userId, postCreateRequest);
            postIds.add(response.getId());
        }

        for (Long postId : postIds) {
            for (int i = 1; i <= 20; i++) {
                CommentCreateRequest commentCreateRequest = new CommentCreateRequest("댓글 내용 " + i);

                Long userId = i % 2 == 0 ? myId : otherId;
                commentService.createComment(userId, postId, commentCreateRequest);
            }
        }
    }
}
