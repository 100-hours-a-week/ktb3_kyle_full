package com.kyle.week4.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostLike {
    private String id;

    public PostLike(String id) {
        this.id = id;
    }

    public boolean isNew() {
        return id == null;
    }
}
