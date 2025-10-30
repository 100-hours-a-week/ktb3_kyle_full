package com.kyle.week4.service;

import com.kyle.week4.cache.CountCache;
import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.request.PostUpdateRequest;
import com.kyle.week4.controller.response.PostDetailResponse;
import com.kyle.week4.controller.response.PostResponse;
import com.kyle.week4.entity.CommentCount;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.post.CommentCountRepository;
import com.kyle.week4.repository.comment.CommentRepository;
import com.kyle.week4.repository.post.PostJpaRepository;
import com.kyle.week4.repository.post.PostRepository;
import com.kyle.week4.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.kyle.week4.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private static final int COMMENT_PAGE_SIZE = 10;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentCountRepository commentCountRepository;
    private final CountCache postViewCountCache;
    private final CountCache postLikeCountCache;

    private final PostJpaRepository postJpaRepository;

    @Transactional
    public PostDetailResponse createPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Post post = request.toEntity(user);
        Post savedPost = postRepository.save(post);
        CommentCount commentCount = new CommentCount(post.getId(), 0);
        commentCountRepository.save(commentCount);

        postViewCountCache.initCache(post.getId());
        postLikeCountCache.initCache(post.getId());

        return PostDetailResponse.of(savedPost, userId, 0);
    }

    public List<PostResponse> infiniteScroll(Long lastPostId, int limit) {
        List<Post> posts = (lastPostId == null) ?
                postRepository.findAllInfiniteScroll(limit) :
                postRepository.findAllInfiniteScroll(lastPostId, limit);

        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        Map<Long, Integer> viewCountMap = postViewCountCache.getCounts(postIds);
        Map<Long, Integer> likeCountMap = postLikeCountCache.getCounts(postIds);

        return posts.stream()
                .map(post ->
                        PostResponse.of(
                                post,
                                viewCountMap.get(post.getId()),
                                likeCountMap.get(post.getId()))
                )
                .toList();
    }

    public PostDetailResponse getPostDetail(Long userId, Long postId) {
        Post post = findPostBy(postId);

        int viewCount = postViewCountCache.increase(postId);

        return PostDetailResponse.of(post, userId, viewCount);
    }

    @Transactional
    public PostDetailResponse updatePost(Long userId, Long postId, PostUpdateRequest request) {
        Post post = findPostBy(postId);

        if (post.isNotAuthor(userId)) {
            throw new CustomException(PERMISSION_DENIED);
        }
        post.updatePost(request);

        int viewCount = postViewCountCache.count(post.getId());

        return PostDetailResponse.of(post, userId, viewCount);
    }

    @Transactional
    public PostDetailResponse updatePostTest(Long userId, Long postId, PostUpdateRequest request) {
        Post post = findPostBy(postId);

        if (post.isNotAuthor(userId)) {
            throw new CustomException(PERMISSION_DENIED);
        }

        post.updatePost(request); // "Dirty Checking"
        postJpaRepository.updateTitle(postId, "UPDATE"); // "Bulk UPDATE"

        int viewCount = postViewCountCache.count(post.getId());

        return PostDetailResponse.of(post, userId, viewCount);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = findPostBy(postId);

        if (post.isNotAuthor(userId)) {
            throw new CustomException(PERMISSION_DENIED);
        }

        post.delete();
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
    }
}
