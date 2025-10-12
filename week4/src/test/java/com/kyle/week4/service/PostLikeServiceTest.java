package com.kyle.week4.service;

import com.kyle.week4.cache.PostLikeCountCache;
import com.kyle.week4.controller.response.PostLikeResponse;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.PostLike;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.exception.ErrorCode;
import com.kyle.week4.repository.PostLikeRepository;
import com.kyle.week4.repository.PostRepository;
import com.kyle.week4.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PostLikeServiceTest {
    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostLikeCountCache postLikeCountCache;

    @AfterEach
    void tearDown() {
        postRepository.clear();
        userRepository.clear();
        postLikeRepository.clear();
    }

    @Test
    @DisplayName("게시글의 좋아요를 생성한다.")
    void createPostLikeTest() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        // when
        PostLikeResponse response = postLikeService.like(user.getId(), post.getId());

        // then
        assertThat(response)
          .extracting("likeCount", "isLiked")
          .containsExactly(1, true);
    }

    @Test
    @DisplayName("좋아요가 눌린 회수만큼 좋아요수가 증가해야 한다.")
    void createPostLike_count() {
        // given
        User user1 = createUser();
        User user2 = createUser();
        User user3 = createUser();
        List<User> users = userRepository.saveAll(List.of(user1, user2, user3));

        Post post = createPost(user1, "제목");
        postRepository.save(post);

        // when
        for (User user : users) {
            postLikeService.like(user.getId(), post.getId());
        }
        int likeCount = postLikeCountCache.count(post.getId());

        // then
        assertThat(likeCount).isEqualTo(3);
    }

    @Test
    @DisplayName("좋아요를 누른 게시글에 다시 좋아요를 누를 경우 예외가 발생한다.")
    void createPostLike_whenDuplicateLike() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        PostLike postLike = new PostLike(user.getId(), post.getId());
        postLikeRepository.save(postLike);

        // when // then
        assertThatThrownBy(() -> postLikeService.like(user.getId(), post.getId()))
          .isInstanceOf(CustomException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_LIKED_ERROR);
    }

    @Test
    @DisplayName("좋아요를 누를 게시글이 존재하지 않으면 예외가 발생한다.")
    void createPostLike_whenPostNotFound() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        // when // then
        assertThatThrownBy(() -> postLikeService.like(user.getId(), 10L))
          .isInstanceOf(CustomException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
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
          .images(List.of("image1.jpg", "image2.jpg"))
          .build();
    }

    private PostLike createPostLike(User user, Post post) {
        return new PostLike(user.getId(), post.getId());
    }
}