package com.thespace.spacechat.message;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private String getRoomKey(Long roomId) {
        return "chat:room:" + roomId + ":messages";
    }

    public void save(ChatMessage message) {
        redisTemplate.opsForList().rightPush(getRoomKey(message.getRoomId()), message);
    }

    public List<Object> getRecentMessages(Long roomId, int count) {
        return redisTemplate.opsForList().range(getRoomKey(roomId), -count, -1);
    }
}
