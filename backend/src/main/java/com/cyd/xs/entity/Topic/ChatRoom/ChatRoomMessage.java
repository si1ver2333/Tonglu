package com.cyd.xs.entity.Topic.ChatRoom;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chatroom_messages")
public class ChatRoomMessage {
    @Id
    private Long id;

    private Long chatRoomId;
    private Long userId;


    private String neckName;

    private String avatar;
    private String content;
    private LocalDateTime sendTime;
}