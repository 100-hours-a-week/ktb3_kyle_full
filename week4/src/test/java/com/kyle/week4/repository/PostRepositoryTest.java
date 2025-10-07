package com.kyle.week4.repository;

import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostRepositoryTest {
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
    void save() {
        // given
        Post post = createPost("제목1");

        // when
        Post savedPost = postRepository.save(post);

        // then
        assertThat(savedPost)
          .extracting("id", "title", "content", "likeCount", "viewCount", "commentCount")
          .contains(1L, "제목1", "내용입니다.", 0, 0, 0);
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