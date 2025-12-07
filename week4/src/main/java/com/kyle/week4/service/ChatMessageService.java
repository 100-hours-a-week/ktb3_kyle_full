package com.kyle.week4.service;

import com.kyle.week4.controller.request.ChatMessageRequest;
import com.kyle.week4.entity.ChatMessage;
import com.kyle.week4.repository.chat.ChatMessageRepository;
import com.kyle.week4.utils.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final Snowflake snowflake;
    private final ChatMessageRepository chatMessageRepository;

    public void createMessage(Long roomId, ChatMessageRequest request, LocalDateTime createdAt) {
        ChatMessage chatMessage = ChatMessage.builder()
            .id(snowflake.nextId())
            .roomId(roomId)
            .senderId(request.getSenderId())
            .content(request.getContent())
            .createdAt(createdAt)
            .build();

        chatMessageRepository.save(chatMessage);
    }
}
