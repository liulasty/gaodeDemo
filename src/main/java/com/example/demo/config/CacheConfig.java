package com.example.demo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // 默认配置（适用于所有缓存）
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(10000)
                .expireAfterWrite(24, TimeUnit.HOURS)
                .recordStats());

        // 为geocodeCache设置特殊配置
        cacheManager.registerCustomCache("geocodeCache", Caffeine.newBuilder()
                .maximumSize(50000) // 地理编码缓存可以大一些
                .expireAfterWrite(7, TimeUnit.DAYS) // 地理数据变化不频繁
                .build());

        return cacheManager;
    }
}
