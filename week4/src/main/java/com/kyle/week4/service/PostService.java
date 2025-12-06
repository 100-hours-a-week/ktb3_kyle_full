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
import java.util.stream.Collectors;

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

    @Transactional
    public PostDetailResponse createPostAndImage(Long userId, PostCreateRequest request, List<MultipartFile> images) {
        User user = findUserBy(userId);

        Post post = request.toEntity(user);
        Post savedPost = postRepository.save(post);

        List<PostImage> postImages = imageUpload(images, savedPost);
        List<PostImage> savedPostImages = postImageRepository.saveAll(postImages);// FIXME: 개별 INSERT 쿼리 발생
        List<PostImageResponse> postImageResponses = savedPostImages.stream()
            .map(postImage ->
                PostImageResponse.of(postImage.getId(), postImage.getImagePath())
            )
            .toList();

        CommentCount commentCount = new CommentCount(savedPost, 0);
        commentCountRepository.save(commentCount);

        postViewCountCache.initCache(savedPost.getId());
        postLikeCountCache.initCache(savedPost.getId());

        return PostDetailResponse.of(savedPost, userId, 0, 0, postImageResponses, userId);
    }

    public List<PostResponse> infiniteScroll(Long lastPostId, int limit) {
        List<Post> posts = (lastPostId == null) ?
            postRepository.findAllInfiniteScroll(limit) :
            postRepository.findAllInfiniteScroll(lastPostId, limit);

        List<Long> postIds = posts.stream()
            .map(Post::getId)
            .toList();

        Map<Long, Integer> commentCountMap = getCommentCounts(postIds);
        Map<Long, Integer> viewCountMap = postViewCountCache.getCounts(postIds);
        Map<Long, Integer> likeCountMap = postLikeCountCache.getCounts(postIds);

        return posts.stream()
            .map(post ->
                PostResponse.of(
                    post,
                    commentCountMap.get(post.getId()),
                    viewCountMap.get(post.getId()),
                    likeCountMap.get(post.getId()))
            )
            .toList();
    }

    public PostDetailResponse getPostDetail(Long userId, Long postId) {
        Post post = postRepository.findWithUserAndPostImagesById(postId)
            .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        int viewCount = postViewCountCache.increase(postId);
        int commentCount = getCommentCount(postId);

        List<PostImageResponse> postImageResponses = post.getPostImages().stream()
            .map(postImage ->
                PostImageResponse.of(postImage.getId(), postImage.getImagePath())
            )
            .toList();

        return PostDetailResponse.of(post, userId, commentCount, viewCount, postImageResponses, post.getUser().getId());
    }

    @Transactional
    public PostDetailResponse updatePost(Long userId, Long postId, PostUpdateRequest request) {
        Post post = postRepository.findWithUserAndPostImagesById(postId)
            .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        if (post.isNotAuthor(userId)) {
            throw new CustomException(PERMISSION_DENIED);
        }
        post.updatePost(request);

        int viewCount = postViewCountCache.count(post.getId());
        int commentCount = getCommentCount(postId);

        List<PostImageResponse> postImageResponses = getPostImageResponses(post.getPostImages());

        return PostDetailResponse.of(post, userId, commentCount, viewCount, postImageResponses, post.getUser().getId());
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = findPostBy(postId);

        if (post.isNotAuthor(userId)) {
            throw new CustomException(PERMISSION_DENIED);
        }

        if (post.isDeleted()) {
            throw new CustomException(ALREADY_DELETED_POST);
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

    private int getCommentCount(Long postId) {
        return commentCountRepository.findById(postId)
            .orElseThrow(() -> new CustomException(POST_NOT_FOUND))
            .getCount();
    }

    private Map<Long, Integer> getCommentCounts(List<Long> postIds) {
        List<CommentCount> commentCounts = commentCountRepository.findCommentCountInPostIds(postIds);
        return commentCounts.stream().collect(
            Collectors.toMap(
                CommentCount::getPostId,
                CommentCount::getCount
            )
        );
    }

    private List<PostImageResponse> getPostImageResponses(List<PostImage> postImages) {
        return postImages.stream()
            .map(postImage ->
                PostImageResponse.of(postImage.getId(), postImage.getImagePath())
            )
            .toList();
    }
}
