package com.kyle.week4.service;

import com.kyle.week4.IntegrationTestSupport;
import com.kyle.week4.cache.CountCache;
import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.request.PostUpdateRequest;
import com.kyle.week4.controller.response.PostDetailResponse;
import com.kyle.week4.controller.response.PostResponse;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.PostImage;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.post.CommentCountRepository;
import com.kyle.week4.repository.post.PostImageRepository;
import com.kyle.week4.repository.post.PostJpaRepository;
import com.kyle.week4.repository.post.PostRepository;
import com.kyle.week4.repository.user.UserJpaRepository;
import com.kyle.week4.repository.user.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static com.kyle.week4.exception.ErrorCode.PERMISSION_DENIED;
import static com.kyle.week4.exception.ErrorCode.POST_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class PostServiceTest extends IntegrationTestSupport {
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostJpaRepository postJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private CommentCountRepository commentCountRepository;

    @Autowired
    private CountCache postViewCountCache;

    @Autowired
    private PostImageRepository postImageRepository;

    @BeforeEach
    void setUp() {
        postImageRepository.deleteAllInBatch();
        postJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();

        jdbcTemplate.execute("ALTER TABLE post AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1");
    }

    @AfterEach
    void tearDown() {
        postImageRepository.deleteAllInBatch();
        commentCountRepository.deleteAllInBatch();
        postJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();
        redisTemplate.delete(redisTemplate.keys("post::view_count::*"));
        redisTemplate.delete(redisTemplate.keys("post::like_count::*"));

        jdbcTemplate.execute("ALTER TABLE post AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE comment AUTO_INCREMENT = 1");
    }

    @Test
    @DisplayName("게시글을 작성한다.")
    void createPostTest() {
        // given
        User user = userRepository.save(createUser());

        PostCreateRequest request = PostCreateRequest.builder()
            .title("제목1")
            .content("내용1")
            .build();

        // when
        PostDetailResponse postDetailResponse = postService.createPostAndImage(user.getId(), request, null);

        // then
        assertThat(postDetailResponse.getId()).isNotNull();
        assertThat(postDetailResponse)
            .extracting("title", "content", "viewCount", "commentCount", "isAuthor")
            .contains("제목1", "내용1", 0, 0, true);
    }

    @Test
    @DisplayName("게시글 작성 시 등록된 이미지 개수만큼 게시글 이미지가 저장된다.")
    void createPost_imageUpload() {
        // given
        User user = userRepository.save(createUser());

        PostCreateRequest request = PostCreateRequest.builder()
            .title("제목1")
            .content("내용1")
            .build();

        String path1 = UUID.randomUUID().toString();
        String path2 = UUID.randomUUID().toString();
        String path3 = UUID.randomUUID().toString();

        List<MultipartFile> images = List.of(
            new MockMultipartFile("images", "a.jpg", "image/jpeg", "a".getBytes()),
            new MockMultipartFile("images", "b.jpg", "image/jpeg", "b".getBytes()),
            new MockMultipartFile("images", "c.jpg", "image/jpeg", "c".getBytes())
        );

        given(imageUploader.uploadImages(images))
            .willReturn(List.of(path1, path2, path3));

        // when
        PostDetailResponse post = postService.createPostAndImage(user.getId(), request, images);

        // then
        List<PostImage> findPostImages = postImageRepository.findByPostId(post.getId());
        assertThat(findPostImages).hasSize(3)
            .extracting("imagePath")
            .containsExactlyInAnyOrder(path1, path2, path3);
    }

    @Test
    @DisplayName("게시글 작성 시 이미지를 등록하지 않는 경우 저장되는 이미지가 없다.")
    void createPost_whenNotImageUpload() {
        // given
        User user = userRepository.save(createUser());

        PostCreateRequest request = PostCreateRequest.builder()
            .title("제목1")
            .content("내용1")
            .build();

        // when
        PostDetailResponse post = postService.createPostAndImage(user.getId(), request, null);

        // then
        List<PostImage> findPostImages = postImageRepository.findByPostId(post.getId());
        assertThat(findPostImages).isEmpty();
    }

    @Disabled
    @TestFactory
    @DisplayName("게시글 목록 조회 시나리오")
    Collection<DynamicTest> infiniteScroll() {
        // given
        User user = userRepository.save(createUser());


        for (int i = 1; i <= 8; i++) {
            PostCreateRequest request = PostCreateRequest.builder()
                .title("제목 " + i)
                .content("내용" + i)
                .images(List.of("image1", "image2"))
                .build();
            postService.createPostAndImage(user.getId(), request, null);
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

    @ParameterizedTest(name = "[{index}] lastPostId={0}, limit={1} → expected={2}")
    @MethodSource("infiniteScrollArguments")
    @DisplayName("게시글 무한 스크롤 동작 검증")
    void infiniteScrollTest(Long lastPostId, int limit, List<Long> expectedIds) {
        // given
        User user = userRepository.save(createUser());


        for (int i = 1; i <= 8; i++) {
            PostCreateRequest request = PostCreateRequest.builder()
                .title("제목 " + i)
                .content("내용" + i)
                .build();
            postService.createPostAndImage(user.getId(), request, null);
        }

        // when
        List<PostResponse> responses = postService.infiniteScroll(lastPostId, limit);

        // then
        assertThat(responses)
            .extracting(PostResponse::getId)
            .containsExactlyElementsOf(expectedIds);
    }

    private static Stream<Arguments> infiniteScrollArguments() {
        return Stream.of(
            Arguments.of(null, 3, List.of(8L, 7L, 6L)),
            Arguments.of(6L, 3, List.of(5L, 4L, 3L)),
            Arguments.of(3L, 3, List.of(2L, 1L)),
            Arguments.of(1L, 3, List.of())
        );
    }

    @Test
    @DisplayName("게시글의 상세 정보를 조회한다.")
    void getPostDetail() {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
            .title("제목1")
            .content("내용1")
            .build();

        User user = userRepository.save(createUser());

        Post post = createPost("제목1", user);
        Long postId = postService.createPostAndImage(user.getId(), request, null).getId();

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
        PostCreateRequest request = PostCreateRequest.builder()
            .title("제목1")
            .content("내용1")
            .build();

        User user = userRepository.save(createUser());

        Post post = createPost("제목1", user);
        Long postId = postService.createPostAndImage(user.getId(), request, null).getId();
        postViewCountCache.initCache(postId);

        final int totalViewCount = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(totalViewCount);

        // when
        for (int i = 0; i < totalViewCount; i++) {
            executor.submit(() -> {
                postService.getPostDetail(user.getId(), postId);
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();
        int viewCount = postViewCountCache.count(postId);

        // then
        assertThat(viewCount).isEqualTo(totalViewCount);
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void updatePostTest() {
        // given
        User user = userRepository.save(createUser());

        PostCreateRequest createRequest = PostCreateRequest.builder()
            .title("제목")
            .content("내용")
            .build();

        PostDetailResponse post = postService.createPostAndImage(user.getId(), createRequest, null);

        PostUpdateRequest request = PostUpdateRequest.builder()
            .title("수정된 제목")
            .content("수정된 내용")
            .build();

        // when
        PostDetailResponse response = postService.updatePost(user.getId(), post.getId(), request);

        // then
        assertThat(response)
            .extracting("id", "title", "content")
            .containsExactlyInAnyOrder(post.getId(), "수정된 제목", "수정된 내용");
    }

    @Test
    @DisplayName("게시글이 존재하지 않으면 수정을 할 수 없다.")
    void updatePost_whenPostNotFound() {
        // given
        User user = userRepository.save(createUser());

        PostUpdateRequest request = PostUpdateRequest.builder()
            .title("수정된 제목")
            .content("수정된 내용")
            .build();

        // when // then
        assertThatThrownBy(() -> postService.updatePost(user.getId(), 1L, request))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", POST_NOT_FOUND);
    }

    @Test
    @DisplayName("작성자가 아닐 경우 게시글을 수정할 수 없다.")
    void updatePost_whenIsNotAuthor() {
        // given
        User author = userRepository.save(createUser("author@test.com", "author"));
        User other = userRepository.save(createUser("other@test.com", "other"));

        PostCreateRequest createRequest = PostCreateRequest.builder()
            .title("제목")
            .content("내용")
            .build();

        PostDetailResponse post = postService.createPostAndImage(author.getId(), createRequest, null);

        PostUpdateRequest request = PostUpdateRequest.builder()
            .title("수정된 제목")
            .content("수정된 내용")
            .build();

        // when // then
        assertThatThrownBy(() -> postService.updatePost(other.getId(), post.getId(), request))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", PERMISSION_DENIED);
    }

    @Test
    @DisplayName("게시글 삭제 시 Soft Delete를 적용한다.")
    void deletePost() {
        // given
        User user = userRepository.save(createUser());
        Post post = postRepository.save(createPost("제목", user));

        // when
        postService.deletePost(user.getId(), post.getId());

        // then
        Post deletedPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(deletedPost.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("작성자가 아닐 경우 게시글을 삭제할 수 없다.")
    void deletePost_whenIsNotAuthor() {
        // given
        User author = userRepository.save(createUser("author@test.com", "author"));
        User other = userRepository.save(createUser("other@test.com", "other"));

        Post post = postRepository.save(createPost("제목", author));

        // when // then
        assertThatThrownBy(() -> postService.deletePost(other.getId(), post.getId()))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", PERMISSION_DENIED);
    }

    private User createUser() {
        return User.builder()
            .email("test@test.com")
            .nickname("test")
            .profileImage("image.jpg")
            .build();
    }

    private User createUser(String email, String nickname) {
        return User.builder()
            .email(email)
            .nickname(nickname)
            .profileImage("image.jpg")
            .build();
    }

    private Post createPost(String title, User user) {
        return Post.builder()
            .title(title)
            .content("내용입니다.")
            .user(user)
            .build();
    }
}