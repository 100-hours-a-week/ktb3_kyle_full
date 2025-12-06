package com.kyle.week4.entity;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_message")
public class ChatMessage {
    @Id
    private Long id;

    private Long roomId;

    private Long senderId;

    private String content;

    private LocalDateTime createdAt;
}
