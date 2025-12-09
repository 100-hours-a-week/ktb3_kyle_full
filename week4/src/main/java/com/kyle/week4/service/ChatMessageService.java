package com.kyle.week4.service;

import com.kyle.week4.controller.request.ChatMessageRequest;
import com.kyle.week4.controller.response.ChatMessageResponse;
import com.kyle.week4.entity.ChatMessage;
import com.kyle.week4.messaging.MessageProducer;
import com.kyle.week4.repository.chat.ChatMessageRepository;
import com.kyle.week4.repository.chat.ChatRoomRepository;
import com.kyle.week4.utils.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final Snowflake snowflake;
    private final MessageProducer messageProducer;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public void createMessage(Long roomId, ChatMessageRequest request, LocalDateTime createdAt) {
        Long chatMessageId = snowflake.nextId();
        ChatMessage chatMessage = ChatMessage.builder()
            .id(chatMessageId)
            .roomId(roomId)
            .senderId(request.getSenderId())
            .content(request.getContent())
            .createdAt(createdAt)
            .build();

        // 채팅방 마지막 메시지 반영
        chatMessageRepository.save(chatMessage);
        messageProducer.convertAndSend("/sub/room/" + roomId, ChatMessageResponse.of(chatMessage));
        chatRoomRepository.updateLastChatMessageId(roomId, chatMessageId);
    }

    public List<ChatMessageResponse> getMessages(Long roomId, Long lastMessageId, int limit) {
        List<ChatMessage> messages = chatMessageRepository.findBeforeMessagesByRoomId(roomId, lastMessageId, limit);
        return messages.stream()
            .sorted(Comparator.comparing(ChatMessage::getId))
            .map(ChatMessageResponse::of)
            .toList();
    }
}
