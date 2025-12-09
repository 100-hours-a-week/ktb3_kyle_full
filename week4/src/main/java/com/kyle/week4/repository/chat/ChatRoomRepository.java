package com.kyle.week4.repository.chat;

import com.kyle.week4.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Modifying
    @Query("update ChatRoom cr set cr.lastChatMessageId = :lastChatMessageId " +
        "where cr.id = :id ")
    int updateLastChatMessageId(@Param("id") Long id,
                                @Param("lastChatMessageId") Long lastChatMessageId);
}
