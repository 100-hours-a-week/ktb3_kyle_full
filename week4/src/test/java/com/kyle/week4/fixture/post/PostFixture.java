package com.kyle.week4.fixture.post;

import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.PostImage;
import com.kyle.week4.entity.User;

public class PostFixture {
    public static Post defaultPost(String title, String content, User user) {
        return Post.builder()
            .title(title)
            .content(content)
            .user(user)
            .build();
    }

    public static Post savedWithUser(Long id, User user) {
        Post post = defaultPost("title", "content", user);
        post.assignId(id);
        return post;
    }

    public static Post savedWithImages(Long id, User user, int count) {
        Post post = savedWithUser(id, user);

        for (int i = 1 ; i <= count; i++) {
            PostImage postImage = new PostImage("image" + i);
            postImage.connectPost(post);
        }

        return post;
    }
}
