package com.kyle.week4.service;

import com.kyle.week4.cache.CountCache;
import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.request.PostUpdateRequest;
import com.kyle.week4.controller.response.PostDetailResponse;
import com.kyle.week4.controller.response.PostImageResponse;
import com.kyle.week4.controller.response.PostResponse;
import com.kyle.week4.entity.CommentCount;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.PostImage;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.comment.CommentRepository;
import com.kyle.week4.repository.post.CommentCountRepository;
import com.kyle.week4.repository.post.PostImageRepository;
import com.kyle.week4.repository.post.PostJpaRepository;
import com.kyle.week4.repository.post.PostRepository;
import com.kyle.week4.repository.user.UserRepository;
import com.kyle.week4.utils.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kyle.week4.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentCountRepository commentCountRepository;
    private final PostImageRepository postImageRepository;
    private final CountCache postViewCountCache;
    private final CountCache postLikeCountCache;
    private final ImageUploader imageUploader;

    private final PostJpaRepository postJpaRepository;

    @Deprecated
    @Transactional
    public PostDetailResponse createPost(Long userId, PostCreateRequest request) {
        User user = findUserBy(userId);

        Post post = request.toEntity(user);
        Post savedPost = postRepository.save(post);
        CommentCount commentCount = new CommentCount(post.getId(), 0);
        commentCountRepository.save(commentCount);

        postViewCountCache.initCache(post.getId());
        postLikeCountCache.initCache(post.getId());

        return PostDetailResponse.of(savedPost, userId, 0, List.of());
    }

    @Transactional
    public PostDetailResponse createPostAndImage(Long userId, PostCreateRequest request, List<MultipartFile> images) {
        User user = findUserBy(userId);

        Post post = request.toEntity(user);
        Post savedPost = postRepository.save(post);

        // TODO: 게시글 이미지 조회 방식 고민해보자.
        // TODO: 영속성 전이를 통한 저장으로 이 트랜잭션에서는 PostImage의 ID값을 받아올 수 없음.
        List<PostImage> postImages = imageUpload(images, savedPost);
        postImageRepository.saveAll(postImages); // 개별 INSERT 쿼리 발생

        List<PostImageResponse> postImageResponses = getPostImageResponses(savedPost.getId());

        CommentCount commentCount = new CommentCount(savedPost.getId(), 0);
        commentCountRepository.save(commentCount);

        postViewCountCache.initCache(savedPost.getId());
        postLikeCountCache.initCache(savedPost.getId());

        return PostDetailResponse.of(savedPost, userId, 0, postImageResponses);
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
        Post post = postRepository.findWithUserById(postId)
            .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        int viewCount = postViewCountCache.increase(postId);

        List<PostImageResponse> postImageResponses = getPostImageResponses(postId);

        return PostDetailResponse.of(post, userId, viewCount, postImageResponses);
    }

    @Transactional
    public PostDetailResponse updatePost(Long userId, Long postId, PostUpdateRequest request) {
        Post post = findPostBy(postId);

        if (post.isNotAuthor(userId)) {
            throw new CustomException(PERMISSION_DENIED);
        }
        post.updatePost(request);

        int viewCount = postViewCountCache.count(post.getId());

        List<PostImageResponse> postImageResponses = getPostImageResponses(postId);

        return PostDetailResponse.of(post, userId, viewCount, postImageResponses);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = findPostBy(postId);

        if (post.isNotAuthor(userId)) {
            throw new CustomException(PERMISSION_DENIED);
        }

        post.delete();
    }

    private List<PostImage> imageUpload(List<MultipartFile> images, Post post) {
        if (images == null || images.isEmpty()) return List.of();

        List<PostImage> postImages = new ArrayList<>();
        List<String> paths = imageUploader.uploadImages(images);
        for (String path : paths) {
            PostImage postImage = new PostImage(path);
            postImage.connectPost(post);
            postImages.add(postImage);
        }
        return postImages;
    }

    private User findUserBy(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
    }

    private List<PostImageResponse> getPostImageResponses(Long postId) {
        List<PostImage> postImages = postImageRepository.findByPostId(postId);
        return postImages.stream()
            .map(postImage ->
                PostImageResponse.of(postImage.getId(), postImage.getImagePath())
            )
            .toList();
    }


//    @Transactional
//    public PostDetailResponse updatePostTest(Long userId, Long postId, PostUpdateRequest request) {
//        Post post = findPostBy(postId);
//
//        if (post.isNotAuthor(userId)) {
//            throw new CustomException(PERMISSION_DENIED);
//        }
//
//        post.updatePost(request); // "Dirty Checking"
//        postJpaRepository.updateTitle(postId, "UPDATE"); // "Bulk UPDATE"
//
//        int viewCount = postViewCountCache.count(post.getId());
//
//        return PostDetailResponse.of(post, userId, viewCount);
//    }
}
