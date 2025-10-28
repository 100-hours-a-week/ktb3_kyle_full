package com.kyle.week4.repository.post;

import com.kyle.week4.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Post p set p.commentCount = p.commentCount + 1 where p.id = :postId")
    void increaseCommentCount(Long postId);

    @Query("select p from Post p")
    List<Post> findAllInfiniteScroll(Pageable pageable);

    @Query("select p from Post p where p.id < :lastPostId")
    List<Post> findAllInfiniteScroll(Long lastPostId, Pageable pageable);
}
