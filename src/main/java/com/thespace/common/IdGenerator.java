//package com.thespace.spacechat.service.general;
//
//import com.thespace.spacechat.domain.enums.DomainNames;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class IdGenerator {
//
//    private final StringRedisTemplate redisTemplate;
//    private final String ID_GENERATOR = ":id:generator";
//
//    public IdGenerator(StringRedisTemplate redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    public Long generate(DomainNames domainNames) {
//        return redisTemplate.opsForValue().increment(domainNames.name().toLowerCase() + ID_GENERATOR);
//    }
//}
