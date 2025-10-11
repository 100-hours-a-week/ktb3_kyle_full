package com.kyle.week4.service;

import com.kyle.week4.controller.request.CommentCreateRequest;
import com.kyle.week4.entity.Comment;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import com.kyle.week4.repository.CommentRepository;
import com.kyle.week4.repository.PostRepository;
import com.kyle.week4.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommentServiceTest {
    @Autowired
    private CommentService commentService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @AfterEach
    void tearDown() {
        postRepository.clear();
        userRepository.clear();
        commentRepository.clear();
    }

    @Test
    @DisplayName("댓글이 작성되면 게시글의 댓글수가 증가한다.")
    void increaseCommentCount_whenCreateComment() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        CommentCreateRequest request = new CommentCreateRequest("댓글");

        final int totalCommentCount = 3000;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(totalCommentCount);

        // when
        for (int i = 0; i < totalCommentCount; i++) {
            executor.submit(() -> {
                commentService.createComment(user.getId(), post.getId(), request);
                latch.countDown();
            });
        }
        latch.await();

        // then
        executor.shutdown();
        assertThat(post.getCommentCount()).isEqualTo(totalCommentCount);
    }

    private User createUser() {
        return User.builder()
          .email("test@test.com")
          .nickname("test")
          .profileImage("image.jpg")
          .build();
    }

    private Post createPost(User user, String title) {
        return Post.builder()
          .title(title)
          .content("내용입니다.")
          .user(user)
          .images(List.of("image1.jpg", "image2.jpg"))
          .build();
    }

    private Comment createComment(User user, Post post, String content) {
        return Comment.builder()
          .user(user)
          .post(post)
          .content(content)
          .build();
    }
}