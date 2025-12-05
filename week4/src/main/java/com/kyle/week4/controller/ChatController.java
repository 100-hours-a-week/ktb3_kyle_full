package com.kyle.week4.controller;

import com.kyle.week4.controller.request.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/message/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageRequest message) {
        log.info("roomId: {}, message: {}", roomId, message.getContent());
        messagingTemplate.convertAndSend("/sub/room/" + roomId, message);
    }
}
