package com.kyle.week4.entity;

import java.time.LocalDateTime;

public abstract class BaseEntity {
    private final LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    protected BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = createdAt;
    }

    public void updateTimestamp() {
        modifiedAt = LocalDateTime.now();
    }
}
