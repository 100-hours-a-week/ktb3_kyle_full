package com.kyle.week4.service;

import com.kyle.week4.controller.request.CommentCreateRequest;
import com.kyle.week4.controller.request.CommentUpdateRequest;
import com.kyle.week4.controller.response.CommentResponse;
import com.kyle.week4.entity.Comment;
import com.kyle.week4.entity.CommentCount;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.MemoryClearRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.kyle.week4.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = "decorator.datasource.enabled=false")
@ActiveProfiles("test")
class CommentServiceTest {
    @Autowired
    private CommentService commentService;

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

    @Autowired
    private List<MemoryClearRepository> memoryClearRepositoryList;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        commentCountRepository.deleteAllInBatch();
        commentJpaRepository.deleteAllInBatch();
        postJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();
        memoryClearRepositoryList.forEach(MemoryClearRepository::clear);

        jdbcTemplate.execute("ALTER TABLE post AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE comment AUTO_INCREMENT = 1");
    }

    @Test
    @DisplayName("테이블 분리 후(벌크성 update 쿼리) - 댓글이 작성되면 게시글의 댓글수가 증가한다.")
    void increaseCommentCount_whenCreateCommentPessimistic1() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        CommentCount commentCount = CommentCount.builder()
                .post(post)
                .count(0)
                .build();
        commentCountRepository.save(commentCount);

        CommentCreateRequest request = new CommentCreateRequest("댓글");

        final int totalCommentCount = 1000;
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
        executor.shutdown();

        // then
        CommentCount findCommentCount = commentCountRepository.findById(post.getId()).orElseThrow();
        assertThat(findCommentCount.getCount()).isEqualTo(totalCommentCount);
    }

    @Test
    @DisplayName("댓글을 수정한다.")
    void updateCommentTest() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        Comment comment = createComment(user, post, "댓글");
        commentRepository.save(comment);

        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");

        // when
        CommentResponse response = commentService.updateComment(user.getId(), post.getId(), comment.getId(), request);

        // then
        assertThat(response.getContent()).isEqualTo("수정된 댓글");
    }

    @Test
    @DisplayName("댓글 수정 시 댓글이 존재하지 않으면 예외가 발생한다.")
    void updateComment_whenCommentNotFound() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        Comment comment = createComment(user, post, "댓글");
        commentRepository.save(comment);

        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");

        // when // then
        assertThatThrownBy(() -> commentService.updateComment(user.getId(), post.getId(), 10L, request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("댓글 수정 시 게시글이 존재하지 않으면 예외가 발생한다.")
    void updateComment_whenPostNotFound() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        Comment comment = createComment(user, post, "댓글");
        commentRepository.save(comment);

        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");

        // when // then
        assertThatThrownBy(() -> commentService.updateComment(user.getId(), 10L, comment.getId(), request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", POST_NOT_FOUND);
    }

    @Test
    @DisplayName("댓글 수정 시 작성자가 아닌 경우 예외가 발생한다.")
    void updateComment_whenUserNotAuthor() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        Comment comment = createComment(user, post, "댓글");
        commentRepository.save(comment);

        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");

        // when // then
        assertThatThrownBy(() -> commentService.updateComment(10L, post.getId(), comment.getId(), request))
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