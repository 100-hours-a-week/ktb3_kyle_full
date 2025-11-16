package com.kyle.week4.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {
    private final StringRedisTemplate redisTemplate;

    @Bean
    public CountCache postViewCountCache() {
        return new RedisCountCache(redisTemplate, "post::view_count::%s");
    }

    @Bean
    public CountCache postLikeCountCache() {
        return new RedisCountCache(redisTemplate, "post::like_count::%s");
    }
}
