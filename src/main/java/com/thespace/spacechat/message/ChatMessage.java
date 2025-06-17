package com.thespace.spacechat.message;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMessage {

    private Long messageId;
    private Long roomId;
    private String senderId;
    private String content;
    private LocalDateTime sentAt;
    private MessageType type;

    public ChatMessage() {
    }

    @Builder
    public ChatMessage(Long messageId, Long roomId, String senderId, String content,
        LocalDateTime sentAt, MessageType type) {
        this.messageId = messageId;
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = sentAt;
        this.type = type;
    }


}