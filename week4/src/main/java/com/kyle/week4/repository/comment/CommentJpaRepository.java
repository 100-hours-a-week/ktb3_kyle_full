package com.kyle.week4.repository.comment;

import com.kyle.week4.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.post.id = :postId")
    List<Comment> findAllInfiniteScroll(Long postId, Pageable pageable);

    @Query("select c from Comment c where c.post.id = :postId and c.id < :lastCommentId")
    List<Comment> findAllInfiniteScroll(Long postId, Long lastCommentId, Pageable pageable);
}
