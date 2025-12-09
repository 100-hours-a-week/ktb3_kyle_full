package com.kyle.week4.repository.chat;

import com.kyle.week4.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, Long>, ChatMessageCustomRepository {
    List<ChatMessage> findByIdIn(List<Long> ids);
}
