package com.kyle.week4.service;

import com.kyle.week4.controller.request.ChatMessageRequest;
import com.kyle.week4.entity.ChatMessage;
import com.kyle.week4.repository.chat.ChatMessageRepository;
import com.kyle.week4.utils.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

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

    public List<ChatMessage> getMessages(Long roomId, Long lastMessageId, int limit) {
        List<ChatMessage> messages = chatMessageRepository.findBeforeMessagesByRoomId(roomId, lastMessageId, limit);
        messages.sort(Comparator.comparing(ChatMessage::getId));
        return messages;
    }
}
