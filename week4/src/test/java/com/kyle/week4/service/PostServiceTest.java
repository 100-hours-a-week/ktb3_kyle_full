package com.kyle.week4.service;

import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.response.PostDetailResponse;
import com.kyle.week4.controller.response.PostResponse;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import com.kyle.week4.repository.PostRepository;
import com.kyle.week4.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
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
        PostDetailResponse postDetailResponse = postService.createPost(1L, request);

        // then
        assertThat(postDetailResponse.getId()).isNotNull();
        assertThat(postDetailResponse)
          .extracting("title", "content", "likeCount", "viewCount", "commentCount", "isAuthor")
          .contains("제목1", "내용1", 0, 0, 0, true);
        assertThat(postDetailResponse.getImages()).hasSize(2)
          .containsExactlyInAnyOrder(
            "image1", "image2"
          );
    }

    @TestFactory
    @DisplayName("게시글 목록 조회 시나리오")
    Collection<DynamicTest> infiniteScroll() {
        // given
        for (int i = 1; i <= 8; i++) {
            Post post = createPost("제목" + i);
            postRepository.save(post);
        }

        int limit = 3;

        return List.of(
          DynamicTest.dynamicTest("lastPostId가 null 이면 가장 최신 게시글부터 조회한다.", () -> {
              // when
              List<PostResponse> responses = postService.infiniteScroll(null, limit);

              // then
              assertThat(responses).hasSize(3);
              assertThat(responses)
                .extracting(PostResponse::getId)
                .containsExactly(8L, 7L, 6L);
          }),
          DynamicTest.dynamicTest("다음 페이지에 lastPostId 보다 작은 게시글부터 조회한다.", () -> {
              // when
              List<PostResponse> responses = postService.infiniteScroll(6L, limit);

              // then
              assertThat(responses).hasSize(3);
              assertThat(responses)
                .extracting(PostResponse::getId)
                .containsExactly(5L, 4L, 3L);
          }),
          DynamicTest.dynamicTest("limit이 남은 게시글 수보다 클 경우 모든 게시글을 반환한다.", () -> {
              // when
              List<PostResponse> responses = postService.infiniteScroll(3L, limit);

              // then
              assertThat(responses).hasSize(2);
              assertThat(responses)
                .extracting(PostResponse::getId)
                .containsExactly(2L, 1L);
          }),
          DynamicTest.dynamicTest("lastPostId 보다 작은 게시글이 없다면 빈 리스트를 반환한다.", () -> {
              // when
              List<PostResponse> responses = postService.infiniteScroll(1L, limit);

              // then
              assertThat(responses).isEmpty();
          })
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