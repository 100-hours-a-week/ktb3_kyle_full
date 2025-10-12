package com.kyle.week4.service;

import com.kyle.week4.controller.request.CommentCreateRequest;
import com.kyle.week4.controller.request.CommentUpdateRequest;
import com.kyle.week4.controller.response.CommentResponse;
import com.kyle.week4.entity.Comment;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.CommentRepository;
import com.kyle.week4.repository.PostRepository;
import com.kyle.week4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kyle.week4.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public Long createComment(Long userId, Long postId, CommentCreateRequest request) {
        User user = findUserBy(userId);
        Post post = findPostBy(postId);

        Comment comment = request.toEntity(user, post);
        Comment savedComment = commentRepository.save(comment);
        postRepository.increaseCommentCount(postId);

        return savedComment.getId();
    }

    public List<CommentResponse> infiniteScroll(Long userId, Long postId, Long lastCommentId, int limit) {
        List<Comment> comments = (lastCommentId == null) ?
          commentRepository.findAllInfiniteScroll(postId, limit) :
          commentRepository.findAllInfiniteScroll(postId, lastCommentId, limit);

        return comments.stream()
          .map(comment -> CommentResponse.of(comment, userId))
          .toList();
    }

    public CommentResponse updateComment(Long userId, Long postId, Long commentId, CommentUpdateRequest request) {
        Post post = findPostBy(postId);
        Comment comment = findCommentBy(commentId);

        if (comment.isNotAuthor(userId)) {
            throw new CustomException(PERMISSION_DENIED);
        }

        comment.updateComment(request);
        return CommentResponse.of(comment, userId);
    }

    public void deleteComment(Long userId, Long postId, Long commentId) {
        Post post = findPostBy(postId);
        Comment comment = findCommentBy(commentId);

        if (comment.isNotAuthor(userId)) {
            throw new CustomException(PERMISSION_DENIED);
        }

        comment.delete();
    }

    private Comment findCommentBy(Long commentId) {
        return commentRepository.findById(commentId)
          .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
    }

    private User findUserBy(Long userId) {
        return userRepository.findById(userId)
          .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId)
          .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
    }
}
