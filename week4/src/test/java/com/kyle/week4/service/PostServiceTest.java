package com.kyle.week4.service;

import com.kyle.week4.cache.CountCache;
import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.request.PostUpdateRequest;
import com.kyle.week4.controller.response.PostDetailResponse;
import com.kyle.week4.controller.response.PostResponse;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.PostRepository;
import com.kyle.week4.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.kyle.week4.exception.ErrorCode.POST_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PostServiceTest {
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountCache postViewCountCache;

    private final User user = createUser();

    @BeforeEach
    void setUp() {
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.clear();
        postRepository.clear();
        postViewCountCache.clear();
    }

    @Test
    @DisplayName("게시글을 저장한다.")
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
          .extracting("title", "content", "viewCount", "commentCount", "isAuthor")
          .contains("제목1", "내용1", 0, 0, true);
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
            PostCreateRequest request = PostCreateRequest.builder()
              .title("제목 " + i)
              .content("내용" + i)
              .images(List.of("image1", "image2"))
              .build();
            postService.createPost(user.getId(), request);
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

    @Test
    @DisplayName("게시글의 상세 정보를 조회한다.")
    void getPostDetail() {
        // given
        Post post = createPost("제목1");
        Long postId = postRepository.save(post).getId();

        // when
        PostDetailResponse response = postService.getPostDetail(1L, postId);

        // then
        assertThat(response)
          .extracting("id", "title")
          .containsExactlyInAnyOrder(postId, "제목1");
    }

    @Test
    @DisplayName("게시글이 존재하지 않으면 예외가 발생한다.")
    void getPostDetail_whenPostNotFound() {
        // given
        Long postId = 1L;

        // when // then
        assertThatThrownBy(() -> postService.getPostDetail(1L, postId))
          .isInstanceOf(CustomException.class)
          .hasFieldOrPropertyWithValue("errorCode", POST_NOT_FOUND);
    }

    @Test
    @DisplayName("게시글의 상세 정보를 조회하면 조회수가 증가한다.")
    void increaseViewCount_whenGetPostDetail() throws Exception {
        // given
        Post post = createPost("제목1");
        Long postId = postRepository.save(post).getId();

        final int totalViewCount = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(totalViewCount);

        // when
        for (int i = 0; i < totalViewCount; i++) {
            executor.submit(() -> {
                postService.getPostDetail(1L, postId);
                latch.countDown();
            });
        }
        latch.await();
        int viewCount = postViewCountCache.count(postId);

        // then
        assertThat(viewCount).isEqualTo(totalViewCount);
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void updatePostTest() {
        // given
        PostCreateRequest createRequest = PostCreateRequest.builder()
          .title("제목")
          .content("내용")
          .images(List.of("image1", "image2"))
          .build();
        PostDetailResponse post = postService.createPost(user.getId(), createRequest);

        PostUpdateRequest request = PostUpdateRequest.builder()
          .title("수정된 제목")
          .content("수정된 내용")
          .images(List.of("image100.jpg"))
          .build();

        // when
        PostDetailResponse response = postService.updatePost(user.getId(), post.getId(), request);

        // then
        assertThat(response)
          .extracting("id", "title", "content", "images")
          .containsExactlyInAnyOrder(post.getId(), "수정된 제목", "수정된 내용", List.of("image100.jpg"));
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
          .user(user)
          .build();
    }
}