package com.kyle.week4.repository.post;

import com.kyle.week4.entity.CommentCount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentCountRepository extends JpaRepository<CommentCount, Long> {
    @Modifying
    @Query("update CommentCount c set c.count = c.count + 1 " +
            "where c.postId = :postId")
    void increase(@Param("postId") Long postId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CommentCount c where c.postId = :postId")
    Optional<CommentCount> findLockedByPostId(@Param("postId") Long postId);

    @Query("select c from CommentCount c where c.postId in :postIds")
    List<CommentCount> findCommentCountInPostIds(@Param("postIds")  List<Long> postIds);
}
