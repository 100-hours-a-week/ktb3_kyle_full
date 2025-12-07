package com.kyle.week4.repository.chat;

import com.kyle.week4.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@RequiredArgsConstructor
public class ChatMessageCustomRepositoryImpl implements ChatMessageCustomRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public List<ChatMessage> findBeforeMessagesByRoomId(Long roomId, Long lastMessageId, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId));

        if (lastMessageId != null) {
            query.addCriteria(Criteria.where("_id").lt(lastMessageId));
        }

        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        query.limit(limit);

        return mongoTemplate.find(query, ChatMessage.class);
    }
}
