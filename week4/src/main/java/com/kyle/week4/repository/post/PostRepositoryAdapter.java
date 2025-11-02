package com.kyle.week4.repository.post;

import com.kyle.week4.entity.Post;
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
public class PostRepositoryAdapter implements PostRepository {
    private final PostJpaRepository postJpaRepository;

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postJpaRepository.findById(id);
    }

    @Override
    public List<Post> findAllInfiniteScroll(int limit) {
        Pageable pageable = PageRequest.of(0, limit,  Sort.by(Sort.Direction.DESC, "id"));
        return postJpaRepository.findAllInfiniteScroll(pageable);
    }

    @Override
    public List<Post> findAllInfiniteScroll(Long lastPostId, int limit) {
        Pageable pageable = PageRequest.of(0, limit,  Sort.by(Sort.Direction.DESC, "id"));
        return postJpaRepository.findAllInfiniteScroll(lastPostId, pageable);
    }

    @Override
    public void increaseCommentCount(Long postId) {
        // postJpaRepository.increaseCommentCount(postId);
    }

    @Override
    public boolean notExistsById(Long postId) {
        return !postJpaRepository.existsById(postId);
    }

    @Override
    public Optional<Post> findLockedById(Long postId) {
        return postJpaRepository.findLockedById(postId);
    }

    public Optional<Post> findWithUserAndPostImagesById(Long postId) {
        return postJpaRepository.findWithUserAndPostImagesById(postId);
    }
}
