package com.kyle.week4.cache;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisCountCache implements CountCache {
    private final StringRedisTemplate redisTemplate;
    private final String keyFormat;

    public RedisCountCache(StringRedisTemplate redisTemplate, String keyFormat) {
        this.redisTemplate = redisTemplate;
        this.keyFormat = keyFormat;
    }

    @Override
    public void initCache(Long postId) {
        redisTemplate.opsForValue().set(generateKey(postId), "0");
    }

    @Override
    public int count(Long postId) {
        String result = redisTemplate.opsForValue().get(generateKey(postId));
        return result == null ? 0 : Integer.parseInt(result);
    }

    @Override
    public Map<Long, Integer> getCounts(List<Long> postIds) {
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long postId : postIds) {
                String key = generateKey(postId);
                connection.stringCommands().get(key.getBytes(StandardCharsets.UTF_8));
            }
            return null;
        });

        Map<Long, Integer> countMap = new HashMap<>();
        for (int i = 0; i < postIds.size(); i++) {
            Long id = postIds.get(i);
            Object value = results.get(i);
            countMap.put(id, value != null ? Integer.parseInt(value.toString()) : 0);
        }

        return countMap;
    }

    @Override
    public int increase(Long postId) {
        Long result = redisTemplate.opsForValue().increment(generateKey(postId));
        return result == null ? 0 : Math.toIntExact(result);
    }

    @Override
    public int decrease(Long postId) {
        Long result = redisTemplate.opsForValue().decrement(generateKey(postId));
        return result == null ? 0 : Math.toIntExact(result);
    }

    @Override
    public void clear() {

    }

    private String generateKey(Long postId) {
        return keyFormat.formatted(postId);
    }
}
