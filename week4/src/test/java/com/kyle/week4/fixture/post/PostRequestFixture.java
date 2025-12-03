package com.kyle.week4.fixture.post;

import com.kyle.week4.controller.request.PostCreateRequest;

public class PostRequestFixture {

    public static PostCreateRequest create(String title, String content) {
        return PostCreateRequest.builder()
            .title(title)
            .content(content)
            .build();
    }

    public static PostCreateRequest create() {
        return create("title", "content");
    }
}
