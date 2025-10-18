package com.kyle.week4.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class CacheConfig {
    @Bean
    public CountCache postViewCountCache() {
        return new InMemoryCache<>(new ConcurrentHashMap<>());
    }

    @Bean
    public CountCache postLikeCountCache() {
        return new InMemoryCache<>(new ConcurrentHashMap<>());
    }
}
