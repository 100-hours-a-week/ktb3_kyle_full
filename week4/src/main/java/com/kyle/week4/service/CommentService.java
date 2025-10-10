package com.kyle.week4.service;

import com.kyle.week4.controller.request.CommentCreateRequest;
import com.kyle.week4.entity.Comment;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.repository.CommentRepository;
import com.kyle.week4.repository.PostRepository;
import com.kyle.week4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kyle.week4.exception.ErrorCode.POST_NOT_FOUND;
import static com.kyle.week4.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public Long createComment(Long userId, Long postId, CommentCreateRequest request) {
        User user = userRepository.findById(userId)
          .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
          .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        Comment comment = request.toEntity(user, post);
        Comment savedComment = commentRepository.save(comment);
        return savedComment.getId();
    }
}
