package com.kyle.week4.repository.post;

import com.kyle.week4.entity.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<Post, Long> {
//    @Modifying
//    @Query("update Post p set p.commentCount = p.commentCount + 1 " +
//            "where p.id = :postId")
//    void increaseCommentCount(@Param("postId") Long postId);

    @EntityGraph(attributePaths = {"user"})
    @Query("select p from Post p")
    List<Post> findAllInfiniteScroll(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("select p from Post p where p.id < :lastPostId")
    List<Post> findAllInfiniteScroll(@Param("lastPostId") Long lastPostId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Post p where p.id = :postId")
    Optional<Post> findLockedById(@Param("postId") Long postId);

    @EntityGraph(attributePaths = {"user", "postImages"})
    Optional<Post> findWithUserAndPostImagesById(@Param("postId") Long postId);

    @Modifying
    @Query("update Post p set p.title = :title where p.id = :postId")
    void updateTitle(@Param("postId") Long postId, @Param("title") String title);
}
