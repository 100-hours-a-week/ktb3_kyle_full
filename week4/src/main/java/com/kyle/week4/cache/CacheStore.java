package com.kyle.week4.cache;

public interface CacheStore {
    Object get(String key);
    void put(String key, Object value);
    void remove(String key);
    void clear();
}
