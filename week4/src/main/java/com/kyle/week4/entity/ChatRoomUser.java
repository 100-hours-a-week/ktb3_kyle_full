package com.kyle.week4.entity;

import com.kyle.week4.entity.embedded.ChatRoomUserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoomUser {
    @EmbeddedId
    private ChatRoomUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatRoomId")
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public ChatRoomUser(ChatRoom chatRoom, User user) {
        this.id = new ChatRoomUserId(chatRoom.getId(), user.getId());
        this.chatRoom = chatRoom;
        this.user = user;
    }
}
