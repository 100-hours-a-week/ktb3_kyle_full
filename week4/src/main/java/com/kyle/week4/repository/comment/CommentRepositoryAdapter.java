package com.kyle.week4.repository.comment;

import com.kyle.week4.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepository {
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public Comment save(Comment comment) {
        return commentJpaRepository.save(comment);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentJpaRepository.findById(id);
    }

    @Override
    public List<Comment> findAllInfiniteScroll(Long postId, int limit) {
        Pageable pageable = PageRequest.of(0, limit,  Sort.by(Sort.Direction.ASC, "id"));
        return commentJpaRepository.findAllInfiniteScroll(postId, pageable);
    }

    @Override
    public List<Comment> findAllInfiniteScroll(Long postId, Long lastCommentId, int limit) {
        Pageable pageable = PageRequest.of(0, limit,  Sort.by(Sort.Direction.ASC, "id"));
        return commentJpaRepository.findAllInfiniteScroll(postId, lastCommentId, pageable);
    }
}
