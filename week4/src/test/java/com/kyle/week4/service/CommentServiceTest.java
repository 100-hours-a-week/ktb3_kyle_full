package com.kyle.week4.service;

import com.kyle.week4.IntegrationTestSupport;
import com.kyle.week4.controller.request.CommentCreateRequest;
import com.kyle.week4.controller.request.CommentUpdateRequest;
import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.response.CommentResponse;
import com.kyle.week4.controller.response.PostDetailResponse;
import com.kyle.week4.entity.Comment;
import com.kyle.week4.entity.CommentCount;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.comment.CommentJpaRepository;
import com.kyle.week4.repository.comment.CommentRepository;
import com.kyle.week4.repository.post.CommentCountRepository;
import com.kyle.week4.repository.post.PostJpaRepository;
import com.kyle.week4.repository.post.PostRepository;
import com.kyle.week4.repository.user.UserJpaRepository;
import com.kyle.week4.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static com.kyle.week4.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentServiceTest extends IntegrationTestSupport {
    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostJpaRepository postJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private CommentJpaRepository commentJpaRepository;

    @Autowired
    private CommentCountRepository commentCountRepository;

    private static final Long UNKNOWN_USER_ID = Long.MAX_VALUE;
    private static final Long UNKNOWN_POST_ID = Long.MAX_VALUE;
    private static final Long UNKNOWN_COMMENT_ID = Long.MAX_VALUE;

    @AfterEach
    void tearDown() {
        commentCountRepository.deleteAllInBatch();
        commentJpaRepository.deleteAllInBatch();
        postJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();

        jdbcTemplate.execute("ALTER TABLE post AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE comment AUTO_INCREMENT = 1");
    }

    @Test
    @DisplayName("댓글을 작성한다.")
    void createComment() {
        // given
        User user = userRepository.save(createUser());

        PostCreateRequest postRequest = PostCreateRequest.builder()
            .title("제목1")
            .content("내용1")
            .build();
        PostDetailResponse post = postService.createPostAndImage(user.getId(), postRequest, null);

        CommentCreateRequest request = new CommentCreateRequest("댓글");

        // when
        Long commentId = commentService.createComment(user.getId(), post.getId(), request);

        // then
        Comment findComment = commentRepository.findById(commentId).orElseThrow();
        assertThat(findComment.getId()).isNotNull();
        assertThat(findComment)
            .extracting("id", "content")
            .containsExactlyInAnyOrder(commentId, "댓글");
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 댓글을 작성할 수 없다.")
    void createComment_whenPostNotFound() {
        // given
        User user = userRepository.save(createUser());

        CommentCreateRequest request = new CommentCreateRequest("댓글");

        // when // then
        assertThatThrownBy(() -> commentService.createComment(user.getId(), UNKNOWN_POST_ID, request))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", POST_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 댓글을 작성할 수 없다.")
    void createComment_whenUserNotFound() {
        // given
        User user = userRepository.save(createUser());
        Post post = postRepository.save(createPost(user, "제목"));

        CommentCreateRequest request = new CommentCreateRequest("댓글");

        // when // then
        assertThatThrownBy(() -> commentService.createComment(UNKNOWN_USER_ID, post.getId(), request))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", USER_NOT_FOUND);
    }

    @Test
    @DisplayName("댓글이 작성된 횟수만큼 게시글의 댓글수가 증가한다.")
    void increaseCommentCount_whenCreateCommentPessimistic() throws Exception {
        // given
        User user = userRepository.save(createUser());

        PostCreateRequest postRequest = PostCreateRequest.builder()
            .title("제목1")
            .content("내용1")
            .build();
        PostDetailResponse postResponse = postService.createPostAndImage(user.getId(), postRequest, null);

        CommentCreateRequest request = new CommentCreateRequest("댓글");

        final int totalCommentCount = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(totalCommentCount);

        // when
        for (int i = 0; i < totalCommentCount; i++) {
            executor.submit(() -> {
                commentService.createComment(user.getId(), postResponse.getId(), request);
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();

        // then
        CommentCount findCommentCount = commentCountRepository.findById(postResponse.getId()).orElseThrow();
        assertThat(findCommentCount.getCount()).isEqualTo(totalCommentCount);
    }

    @Test
    @DisplayName("댓글을 수정한다.")
    void updateCommentTest() {
        // given
        User user = userRepository.save(createUser());
        Post post = postRepository.save(createPost(user, "제목"));

        Comment comment = commentRepository.save(createComment(user, post, "댓글"));

        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");

        // when
        CommentResponse response = commentService.updateComment(user.getId(), post.getId(), comment.getId(), request);

        // then
        assertThat(response.getContent()).isEqualTo("수정된 댓글");
    }

    @Test
    @DisplayName("존재하지 않는 댓글을 수정할 수 없다.")
    void updateComment_whenCommentNotFound() {
        // given
        User user = userRepository.save(createUser());
        Post post = postRepository.save(createPost(user, "제목"));

        Comment comment = commentRepository.save(createComment(user, post, "댓글"));

        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");

        // when // then
        assertThatThrownBy(() -> commentService.updateComment(user.getId(), post.getId(), UNKNOWN_COMMENT_ID, request))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않는 게시글의 댓글을 수정할 수 없다.")
    void updateComment_whenPostNotFound() {
        // given
        User user = userRepository.save(createUser());
        Post post = postRepository.save(createPost(user, "제목"));

        Comment comment = commentRepository.save(createComment(user, post, "댓글"));

        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");

        // when // then
        assertThatThrownBy(() -> commentService.updateComment(user.getId(), UNKNOWN_POST_ID, comment.getId(), request))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", POST_NOT_FOUND);
    }

    @Test
    @DisplayName("작성자가 아닌 경우 댓글을 수정할 수 없다.")
    void updateComment_whenUserNotAuthor() {
        // given
        User author = userRepository.save(createUser("author@test.com", "author"));
        User other = userRepository.save(createUser("other@test.com", "other"));

        Post post = postRepository.save(createPost(author, "제목"));

        Comment comment = commentRepository.save(createComment(author, post, "댓글"));
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");

        // when // then
        assertThatThrownBy(() -> commentService.updateComment(other.getId(), post.getId(), comment.getId(), request))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", PERMISSION_DENIED);
    }

    @Test
    @DisplayName("댓글 삭제 시 Soft Delete가 적용된다.")
    void deleteComment() {
        // given
        User user = userRepository.save(createUser());
        Post post = postRepository.save(createPost(user, "제목"));
        Comment comment = commentRepository.save(createComment(user, post, "내용"));

        // when
        commentService.deleteComment(user.getId(), post.getId(), comment.getId());

        // then
        Comment deletedComment = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(deletedComment.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("댓글 삭제 시 게시글의 댓글수가 감소한다.")
    void deleteComment_thenDecreaseCommentCount() {
        // given
        User user = userRepository.save(createUser());

        PostCreateRequest postRequest = PostCreateRequest.builder()
            .title("제목1")
            .content("내용1")
            .build();
        PostDetailResponse post = postService.createPostAndImage(user.getId(), postRequest, null);

        CommentCreateRequest request = new CommentCreateRequest("댓글");
        Long commentId = commentService.createComment(user.getId(), post.getId(), request);
        CommentCount beforeDelete = commentCountRepository.findById(commentId).orElseThrow();

        // when
        commentService.deleteComment(user.getId(), post.getId(), commentId);

        // then
        CommentCount afterDelete = commentCountRepository.findById(post.getId()).orElseThrow();
        assertThat(beforeDelete.getCount()).isEqualTo(1);
        assertThat(afterDelete.getCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("댓글 작성자가 아닐 경우 댓글을 삭제할 수 없다.")
    void deleteComment_whenIsNotAuthor() {
        // given
        User author = userRepository.save(createUser("author@test.com", "author"));
        User other = userRepository.save(createUser("other@test.com", "other"));

        Post post = postRepository.save(createPost(author, "제목"));
        Comment comment = commentRepository.save(createComment(author, post, "내용"));

        // when // then
        assertThatThrownBy(() -> commentService.deleteComment(other.getId(), post.getId(), comment.getId()))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", PERMISSION_DENIED);
    }

    @Test
    @DisplayName("이미 삭제된 댓글은 삭제할 수 없다.")
    void deleteComment_whenAlreadyDeleted() {
        // given
        User user = userRepository.save(createUser());
        Post post = postRepository.save(createPost(user, "제목"));
        Comment comment = commentRepository.save(createComment(user, post, "내용"));

        commentService.deleteComment(user.getId(), post.getId(), comment.getId());

        // when // then
        assertThatThrownBy(() -> commentService.deleteComment(user.getId(), post.getId(), comment.getId()))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ALREADY_DELETED_COMMENT);
    }

    @ParameterizedTest(name = "[{0}] lastCommentId={1}, limit={2} → expected={3}")
    @MethodSource("infiniteScrollArguments")
    @DisplayName("댓글 무한 스크롤 동작 검증")
    void infiniteScrollTest(String displayName, Long lastCommentId, int limit, List<Long> expectedIds) {
        // given
        User user = userRepository.save(createUser());
        Post post = postRepository.save(createPost(user, "제목"));

        for (int i = 1; i <= 5; i++) {
            commentService.createComment(user.getId(), post.getId(), new CommentCreateRequest("댓글" + i));
        }

        // when
        List<CommentResponse> responses = commentService.infiniteScroll(user.getId(), post.getId(), lastCommentId, limit);

        // then
        assertThat(responses)
            .extracting(CommentResponse::getId)
            .containsExactlyElementsOf(expectedIds);
    }

    private static Stream<Arguments> infiniteScrollArguments() {
        return Stream.of(
            Arguments.of("lastCommentId가 null이면 댓글을 작성된 순서로 조회한다.", null, 2, List.of(1L, 2L)),
            Arguments.of("lastCommentId보다 큰 댓글부터 조회된다.", 2L, 2, List.of(3L, 4L)),
            Arguments.of("limit이 남은 댓글보다 클 경우 모든 댓글을 조회한다.", 4L, 2, List.of(5L)),
            Arguments.of("lastCommentId보다 큰 게시글이 없다면 빈 리스트를 반환한다.", 5L, 2, List.of())
        );
    }

    private User createUser() {
        return User.builder()
            .email("test@test.com")
            .nickname("test")
            .build();
    }

    private User createUser(String email, String nickname) {
        return User.builder()
            .email(email)
            .nickname(nickname)
            .build();
    }

    private Post createPost(User user, String title) {
        return Post.builder()
            .title(title)
            .content("내용입니다.")
            .user(user)
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