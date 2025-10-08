package com.kyle.week4.service;

import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.response.PostResponse;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import com.kyle.week4.repository.PostRepository;
import com.kyle.week4.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostServiceTest {
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = createUser();
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        postRepository.clear();
    }

    @Test
    @DisplayName("게시물을 저장한다.")
    void createPostTest() {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
          .title("제목1")
          .content("내용1")
          .images(List.of("image1", "image2"))
          .build();

        // when
        PostResponse postResponse = postService.createPost(1L, request);

        // then
        assertThat(postResponse.getId()).isNotNull();
        assertThat(postResponse)
          .extracting("title", "content", "likeCount", "viewCount", "commentCount", "isAuthor")
          .contains("제목1", "내용1", 0, 0, 0, true);
        assertThat(postResponse.getImages()).hasSize(2)
          .containsExactlyInAnyOrder(
            "image1", "image2"
          );
    }

    private User createUser() {
        return User.builder()
          .email("test@test.com")
          .nickname("test")
          .profileImage("image.jpg")
          .build();
    }

    private Post createPost(String title) {
        return Post.builder()
          .title(title)
          .content("내용입니다.")
          .userId(1L)
          .images(List.of("image1.jpg", "image2.jpg"))
          .build();
    }
}