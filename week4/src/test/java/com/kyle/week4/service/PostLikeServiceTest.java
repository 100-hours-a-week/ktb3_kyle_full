package com.kyle.week4.service;

import com.kyle.week4.cache.CountCache;
import com.kyle.week4.controller.response.PostLikeResponse;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.PostLike;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.exception.ErrorCode;
import com.kyle.week4.repository.MemoryClearRepository;
import com.kyle.week4.repository.postlike.PostLikeRepository;
import com.kyle.week4.repository.post.PostRepository;
import com.kyle.week4.repository.user.UserRepository;
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
    private CountCache postLikeCountCache;

    @Autowired
    private List<MemoryClearRepository> memoryClearRepositoryList;

    @AfterEach
    void tearDown() {
        postRepository.clear();
        postLikeRepository.clear();
        postLikeCountCache.clear();
        memoryClearRepositoryList.forEach(MemoryClearRepository::clear);
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

    @Test
    @DisplayName("좋아요를 취소한다.")
    void deletePostLike() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        postLikeService.like(user.getId(), post.getId());

        // when
        PostLikeResponse response = postLikeService.removeLike(user.getId(), post.getId());

        // then
        assertThat(response)
          .extracting("likeCount", "isLiked")
          .containsExactly(0, false);
    }

    @Test
    @DisplayName("좋아요가 취소되면 좋아요수가 감소해야 한다.")
    void deletePostLike_count() {
        // given
        User user1 = createUser();
        User user2 = createUser();
        User user3 = createUser();
        List<User> users = userRepository.saveAll(List.of(user1, user2, user3));

        Post post = createPost(user1, "제목");
        postRepository.save(post);

        for (User user : users) {
            postLikeService.like(user.getId(), post.getId());
        }
        postLikeService.removeLike(user1.getId(), post.getId());

        // when
        int likeCount = postLikeCountCache.count(post.getId());

        // then
        assertThat(likeCount).isEqualTo(2);
    }

    @Test
    @DisplayName("좋아요를 취소 할 게시글이 존재하지 않으면 예외가 발생한다.")
    void deletePostLike_whenPostNotFound() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        PostLike postLike = new PostLike(user.getId(), post.getId());
        postLikeRepository.save(postLike);

        // when // then
        assertThatThrownBy(() -> postLikeService.removeLike(user.getId(), 10L))
          .isInstanceOf(CustomException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("좋아요를 누르지 않았는데, 좋아요를 취소하려고 하는 경우 예외가 발생한다.")
    void deletePostLike_whenDoNotLike() {
        // given
        User user = createUser();
        userRepository.save(user);

        Post post = createPost(user, "제목");
        postRepository.save(post);

        // when // then
        assertThatThrownBy(() -> postLikeService.removeLike(user.getId(), post.getId()))
          .isInstanceOf(CustomException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_LIKE_NOT_FOUND);
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

    private PostLike createPostLike(User user, Post post) {
        return new PostLike(user.getId(), post.getId());
    }

    private String generateKey(Long userId, Long postId) {
        return userId + ":" + postId;
    }
}