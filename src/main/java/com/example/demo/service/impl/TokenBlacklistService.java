package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_KEY = "jwt:blacklist";


    public void addToBlacklist(String token, long expiration) {
        redisTemplate.opsForValue().set(BLACKLIST_KEY + ":" + token, "logout", expiration, TimeUnit.SECONDS);
    }


    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_KEY + ":" + token));
    }
}