package com.kyle.week4.repository.chat;

import com.kyle.week4.controller.response.ChatRoomSummary;
import com.kyle.week4.entity.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {

    @Query(value = "select new com.kyle.week4.controller.response.ChatRoomSummary(" +
        "cr.id, opponent.nickname, opponent.profileImage" +
        ")" +
        "from ChatRoom cr " +
        "join ChatRoomUser me on me.chatRoom = cr and me.user.id = :userId " +
        "join ChatRoomUser other on other.chatRoom = cr and other.user.id <> :userId " +
        "join User opponent on other.user = opponent")
    List<ChatRoomSummary> findChatRoomsWithOpponent(@Param("userId") Long userId);

    @Query("select cru from ChatRoomUser cru " +
        "join fetch cru.user " +
        "where cru.chatRoom.id = :roomId")
    List<ChatRoomUser> findByRoomId(@Param("roomId") Long roomId);
}
