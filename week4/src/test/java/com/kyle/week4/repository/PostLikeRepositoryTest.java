package com.kyle.week4.repository;

import com.kyle.week4.IntegrationTestSupport;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.PostLike;
import com.kyle.week4.entity.User;
import com.kyle.week4.repository.post.PostJpaRepository;
import com.kyle.week4.repository.post.PostRepository;
import com.kyle.week4.repository.postlike.PostLikeJpaRepository;
import com.kyle.week4.repository.postlike.PostLikeRepository;
import com.kyle.week4.repository.user.UserJpaRepository;
import com.kyle.week4.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PostLikeRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostLikeJpaRepository postLikeJpaRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostJpaRepository postJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @AfterEach
    void tearDown() {
        postLikeJpaRepository.deleteAllInBatch();
        postJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();

        jdbcTemplate.execute("ALTER TABLE post AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1");
    }


    @Test
    @DisplayName("사용자ID와 게시글ID로 사용자의 좋아요 여부를 알 수 있다.")
    void existsByUserIdAndPostId() {
        // given
        User likedUser = createUser("user1@test.com", "user1");
        User unlikedUser = createUser("user2@test.com", "user2");
        userRepository.save(likedUser);
        userRepository.save(unlikedUser);

        Post post = createPost("제목1", likedUser);
        postRepository.save(post);

        PostLike like = new PostLike(likedUser.getId(), post.getId());
        postLikeRepository.save(like);

        // when
        boolean liked = postLikeRepository.existsByUserIdAndPostId(likedUser.getId(), post.getId());
        boolean unliked = postLikeRepository.existsByUserIdAndPostId(unlikedUser.getId(), post.getId());

        //then
        assertThat(liked).isTrue();
        assertThat(unliked).isFalse();
    }

    @Test
    @DisplayName("좋아요를 눌렀다면 사용자ID와 게시글ID로 조회할 수 있다.")
    void findByUserIdAndPostId() {
        // given
        User user = createUser("user1@test.com", "user1");
        userRepository.save(user);

        Post post = createPost("제목1", user);
        postRepository.save(post);

        PostLike like = new PostLike(user.getId(), post.getId());
        postLikeRepository.save(like);

        // when
        Optional<PostLike> findLike = postLikeRepository.findByUserIdAndPostId(user.getId(), post.getId());

        //then
        assertThat(findLike).isPresent();
    }

    @Test
    @DisplayName("좋아요를 누르지 않으면 사용자ID와 게시글ID로 조회할 수 없다.")
    void findByUserIdAndPostId_notLiked() {
        // given
        User user = createUser("user1@test.com", "user1");
        userRepository.save(user);

        Post post = createPost("제목1", user);
        postRepository.save(post);

        // when
        Optional<PostLike> findLike = postLikeRepository.findByUserIdAndPostId(user.getId(), post.getId());

        //then
        assertThat(findLike).isEmpty();
    }

    private User createUser(String email, String nickname) {
        return User.builder()
            .email(email)
            .nickname(nickname)
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