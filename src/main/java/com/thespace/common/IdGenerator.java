package com.thespace.common;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class IdGenerator {

    private final StringRedisTemplate redisTemplate;
    private final String ID_GENERATOR = ":id:generator";

    public IdGenerator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Long midGenerate(Long rid, DomainNames domainNames) {
        return redisTemplate.opsForValue().increment("room:" + rid + domainNames.name().toLowerCase() + ID_GENERATOR);
    }

    public Long generate(DomainNames domainNames) {
        return redisTemplate.opsForValue().increment(domainNames.name().toLowerCase() + ID_GENERATOR);
    }
}
