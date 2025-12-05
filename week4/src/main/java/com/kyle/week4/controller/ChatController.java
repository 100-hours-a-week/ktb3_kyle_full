package com.kyle.week4.controller;

import com.kyle.week4.controller.request.ChatMessageRequest;
import com.kyle.week4.messaging.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageProducer messageProducer;

    @MessageMapping("/message/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageRequest message) {
        log.info("roomId: {}, message: {}", roomId, message.getContent());
        messageProducer.convertAndSend("/sub/room/" + roomId, message);
    }
}
