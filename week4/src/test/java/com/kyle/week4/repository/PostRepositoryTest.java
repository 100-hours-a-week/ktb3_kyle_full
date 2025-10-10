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

    private final User user = createUser();

    @BeforeEach
    void setUp() {
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.clear();
        postRepository.clear();
    }

    @Test
    @DisplayName("게시글을 저장한다.")
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

    @Test
    @DisplayName("게시글을 최신순으로 limit 개수만큼 조회한다.")
    void findAllInfiniteScrollFirst() {
        // given
        for (int i = 1; i <= 10; i++) {
            Post post = createPost("제목" + i);
            postRepository.save(post);
        }
        int limit = 5;

        // when
        List<Post> posts = postRepository.findAllInfiniteScroll(limit);

        // then
        assertThat(posts).hasSize(limit);
        assertThat(posts)
          .extracting(Post::getId)
          .isSortedAccordingTo((a, b) -> Long.compare(b, a));
    }

    @Test
    @DisplayName("게시글이 limit 보다 적을 경우, 남아있는 모든 게시글을 반환한다")
    void findAllInfiniteScrollFirst_whenLessThanLimit() {
        // given
        for (int i = 1; i <= 2; i++) {
            Post post = createPost("제목" + i);
            postRepository.save(post);
        }
        int limit = 5;

        // when
        List<Post> posts = postRepository.findAllInfiniteScroll(limit);

        // then
        assertThat(posts).hasSize(2);
        assertThat(posts)
          .extracting(Post::getId)
          .containsExactly(2L, 1L);
    }

    @Test
    @DisplayName("게시글이 존재하지 않으면 빈 리스트를 반환한다.")
    void findAllInfiniteScrollFirst_whenEmpty() {
        // given
        int limit = 5;

        // when
        List<Post> posts = postRepository.findAllInfiniteScroll(limit);

        // then
        assertThat(posts).isEmpty();
    }

    @Test
    @DisplayName("마지막 게시글 ID 이전의 게시글을 최신순으로 limit 개수만큼 조회한다.")
    void findAllInfiniteScrollLastId() {
        // given
        for (int i = 1; i <= 10; i++) {
            Post post = createPost("제목" + i);
            postRepository.save(post);
        }
        Long lastPostId = 6L;
        int limit = 5;

        // when
        List<Post> posts = postRepository.findAllInfiniteScroll(lastPostId, limit);

        // then
        assertThat(posts).hasSize(limit);
        assertThat(posts)
          .extracting(Post::getId)
          .isSortedAccordingTo((a, b) -> Long.compare(b, a));
        assertThat(posts)
          .allMatch(post -> post.getId() < lastPostId);
    }

    @Test
    @DisplayName("마지막 게시글 ID 이전의 게시글이 존재하지 않으면 빈 리스트를 반환한다.")
    void findAllInfiniteScrollLastId_whenLessThanLimit() {
        // given
        Long lastPostId = 1L;
        int limit = 5;

        // when
        List<Post> posts = postRepository.findAllInfiniteScroll(lastPostId, limit);

        // then
        assertThat(posts).isEmpty();
    }

    @Test
    @DisplayName("마지막 게시글 ID 이전의 게시글이 limit 보다 적을 경우, 남아있는 모든 게시글을 반환한다.")
    void findAllInfiniteScrollLastId_whenEmpty() {
        // given
        for (int i = 1; i <= 5; i++) {
            Post post = createPost("제목" + i);
            postRepository.save(post);
        }
        Long lastPostId = 3L;
        int limit = 5;

        // when
        List<Post> posts = postRepository.findAllInfiniteScroll(lastPostId, limit);

        // then
        assertThat(posts).hasSize(2);
        assertThat(posts)
          .extracting(Post::getId)
          .containsExactly(2L, 1L);
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
          .images(List.of("image1.jpg", "image2.jpg"))
          .build();
    }
}