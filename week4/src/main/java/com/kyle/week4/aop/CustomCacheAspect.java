package com.kyle.week4.aop;

import com.kyle.week4.cache.SimpleCacheStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class CustomCacheAspect {
    private final SimpleCacheStore cacheStore;

    @Around("@annotation(CustomCacheable)")
    public Object applyCustomCache(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        CustomCacheable cacheable = method.getAnnotation(CustomCacheable.class);
        log.info("{}", method.getName());

        String key = SpelKeyGenerator.getKey(cacheable.key(), joinPoint);
        String cacheKey = cacheable.cacheName() + "::" + key;

        Object cachedValue = cacheStore.get(cacheKey);
        if (cachedValue != null) {
            log.info("{} {}", "[Cache Hit]", cacheKey);
            return cachedValue;
        }
        log.info("{} {}", "[Cache Miss]", cacheKey);

        Object result = joinPoint.proceed();

        cacheStore.put(cacheKey, result);
        log.info("{} {}", "[Cache Store]", cacheKey);

        return result;
    }

    @Before("@annotation(CustomCacheEvict)")
    public void evictCustomCache(JoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        CustomCacheEvict cacheEvict = method.getAnnotation(CustomCacheEvict.class);
        log.info("{}", method.getName());

        String key = SpelKeyGenerator.getKey(cacheEvict.key(), joinPoint);
        String cacheKey = cacheEvict.cacheName() + "::" + key;

        cacheStore.remove(cacheKey);
        log.info("{} {}", "[Cache Evict]", cacheKey);
    }
}
