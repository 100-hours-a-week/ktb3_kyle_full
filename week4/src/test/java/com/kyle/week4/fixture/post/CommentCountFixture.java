package com.kyle.week4.fixture.post;

import com.kyle.week4.entity.CommentCount;
import com.kyle.week4.entity.Post;

public class CommentCountFixture {
    public static CommentCount withPost(Post post, int count) {
        return CommentCount.builder()
            .post(post)
            .count(count)
            .build();
    }


}
