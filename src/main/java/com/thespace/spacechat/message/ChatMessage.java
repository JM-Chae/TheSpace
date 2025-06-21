package com.thespace.spacechat.message;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMessage {

    private Long messageId;
    private Long roomId;
    private String sender;
    private String content;
    private LocalDateTime sentAt;
    private MessageType type;

    public ChatMessage() {
    }

    @Builder
    public ChatMessage(Long messageId, Long roomId, String sender, String content,
        LocalDateTime sentAt, MessageType type) {
        this.messageId = messageId;
        this.roomId = roomId;
        this.sender = sender;
        this.content = content;
        this.sentAt = sentAt;
        this.type = type;
    }


}