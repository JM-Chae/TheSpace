package com.thespace.spacechat.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSub implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            ChatMessage chatMessage =  objectMapper.readValue(json, ChatMessage.class);
            messagingTemplate.convertAndSend("/topic/chat/room/" + chatMessage.getRoomId(), chatMessage);

            log.info("Received and forwarded chat message: roomId={}, mid={}, sender={}", chatMessage.getRoomId(), chatMessage.getMessageId(), chatMessage.getSender());
        } catch (Exception e) {
            log.error("Failed to process Redis message", e);
        }
    }
}
