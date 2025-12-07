package com.kyle.week4.controller;

import com.kyle.week4.controller.request.ChatMessageRequest;
import com.kyle.week4.controller.request.ChatRoomCreateRequest;
import com.kyle.week4.messaging.MessageProducer;
import com.kyle.week4.security.CustomUserDetails;
import com.kyle.week4.service.ChatMessageService;
import com.kyle.week4.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final MessageProducer messageProducer;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/message/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageRequest message) {
        log.info("roomId: {}, senderId {} , message: {}", roomId, message.getSenderId(), message.getContent());
        // 메세지 저장
        chatMessageService.createMessage(roomId, message, LocalDateTime.now());
        messageProducer.convertAndSend("/sub/room/" + roomId, message);
    }

    @PostMapping("/chat-rooms")
    public BaseResponse<?> createChatRoom(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestBody ChatRoomCreateRequest request
    ) {
        return BaseResponse.created(chatRoomService.createChatRoom(user.getUserId(), request.getOpponentId()));
    }

    @GetMapping("/chat-rooms")
    public BaseResponse<?>  getChatRooms(@AuthenticationPrincipal CustomUserDetails user) {
        return BaseResponse.ok(chatRoomService.getChatRoomList(user.getUserId()));
    }

    @GetMapping("/chat-rooms/{roomId}/nicknames")
    public BaseResponse<?> getNicknameMap(@PathVariable("roomId") Long roomId) {
        return BaseResponse.ok(chatRoomService.getNicknameMap(roomId));
    }
}
