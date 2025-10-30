package com.kyle.week4.repository;

import com.kyle.week4.entity.Comment;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import com.kyle.week4.repository.comment.CommentJpaRepository;
import com.kyle.week4.repository.comment.CommentRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostJpaRepository postJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private CommentJpaRepository commentJpaRepository;

    @Autowired
    private List<MemoryClearRepository> memoryClearRepositoryList;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        commentJpaRepository.deleteAllInBatch();
        postJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();
        memoryClearRepositoryList.forEach(MemoryClearRepository::clear);

        jdbcTemplate.execute("ALTER TABLE post ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE comment ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    @DisplayName("게시글의 댓글을 저장한다.")
    void save() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        Comment comment = createComment(user, post, "댓글 내용");

        // when
        Comment savedComment = commentRepository.save(comment);

        // then
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment)
          .extracting("user", "post", "content")
          .contains(user, post, "댓글 내용");
    }

    @Test
    @DisplayName("게시글의 댓글을 작성된 순서로 limit 개수만큼 조회한다.")
    void findAllInfiniteScroll() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        int limit = 3;

        for (int i = 1; i <= 5; i++) {
            Comment comment = createComment(user, post, "댓글" + i);
            commentRepository.save(comment);
        }

        // when
        List<Comment> comments = commentRepository.findAllInfiniteScroll(post.getId(), limit);

        // then
        assertThat(comments.size()).isEqualTo(3);
        assertThat(comments)
          .extracting(Comment::getId)
          .isSortedAccordingTo(Long::compare);
    }

    @Test
    @DisplayName("게시글의 댓글이 limit 보다 적을 경우, 남아있는 모든 댓글을 조회한다.")
    void findAllInfiniteScroll_whenLessThanLimit() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        int limit = 3;

        for (int i = 1; i <= 2; i++) {
            Comment comment = createComment(user, post, "댓글" + i);
            commentRepository.save(comment);
        }

        // when
        List<Comment> comments = commentRepository.findAllInfiniteScroll(post.getId(), limit);

        // then
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments)
          .extracting(Comment::getId)
          .isSortedAccordingTo(Long::compare);
    }

    @Test
    @DisplayName("게시글의 댓글이 존재하지 않으면 빈 리스트를 반환한다.")
    void findAllInfiniteScroll_whenEmpty() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        int limit = 3;

        // when
        List<Comment> comments = commentRepository.findAllInfiniteScroll(post.getId(), limit);

        // then
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("마지막 댓글 ID 이후의 댓글을 작성된 순서로 limit 개수만큼 조회한다.")
    void findAllInfiniteScrollLastId() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        int limit = 3;
        Long lastCommentId = 2L;

        for (int i = 1; i <= 5; i++) {
            Comment comment = createComment(user, post, "댓글" + i);
            commentRepository.save(comment);
        }

        // when
        List<Comment> comments = commentRepository.findAllInfiniteScroll(post.getId(), lastCommentId, limit);

        // then
        assertThat(comments.size()).isEqualTo(3);
        assertThat(comments)
          .extracting(Comment::getId)
          .isSortedAccordingTo(Long::compare);
        assertThat(comments)
          .allMatch(comment -> comment.getId() > lastCommentId);
    }

    @Test
    @DisplayName("마지막 댓글 ID 이후의 댓글이 limit 보다 적을 경우, 남아있는 모든 댓글을 조회한다.")
    void findAllInfiniteScrollLastId_whenLessThanLimit() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        int limit = 3;
        Long lastCommentId = 2L;

        for (int i = 1; i <= 4; i++) {
            Comment comment = createComment(user, post, "댓글" + i);
            commentRepository.save(comment);
        }

        // when
        List<Comment> comments = commentRepository.findAllInfiniteScroll(post.getId(), lastCommentId, limit);

        // then
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments)
          .extracting(Comment::getId)
          .containsExactly(3L, 4L);
    }

    @Test
    @DisplayName("마지막 댓글 ID 이후의 댓글이 존재하지 않으면 빈 리스트를 반환한다.")
    void findAllInfiniteScrollLastId_whenEmpty() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        int limit = 3;
        Long lastCommentId = 4L;

        for (int i = 1; i <= 4; i++) {
            Comment comment = createComment(user, post, "댓글" + i);
            commentRepository.save(comment);
        }

        // when
        List<Comment> comments = commentRepository.findAllInfiniteScroll(post.getId(), lastCommentId, limit);

        // then
        assertThat(comments).isEmpty();
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