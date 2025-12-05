package com.kyle.week4.messaging;

import com.kyle.week4.controller.request.ChatMessageRequest;

public interface MessageProducer {

    void convertAndSend(String destination, ChatMessageRequest message);
}
