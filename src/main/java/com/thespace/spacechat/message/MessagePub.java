package com.thespace.spacechat.message;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagePub {

    private static final String CHANNEL = "chatroom";
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChatMessage message) {
        redisTemplate.convertAndSend(CHANNEL, message);
    }
}
