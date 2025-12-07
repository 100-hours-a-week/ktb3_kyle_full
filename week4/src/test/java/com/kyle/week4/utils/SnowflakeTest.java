package com.kyle.week4.utils;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class SnowflakeTest {
    private final Snowflake snowflake = new Snowflake();

    @Test
    void nextId() {
        int threadCount = 100;
        int totalIds = 10_000;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalIds);
        Set<Long> ids = new ConcurrentSkipListSet<>();

        IntStream.range(0, totalIds).forEach(i -> executor.submit(() -> {
            await(startLatch);
            ids.add(snowflake.nextId());
            doneLatch.countDown();
        }));

        startLatch.countDown();
        await(doneLatch);
        executor.shutdown();

        assertEquals(totalIds, ids.size());
        assertTrue(ids.stream().noneMatch(id -> id == null));
    }

    private void await(CountDownLatch latch) {
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Latch waiting interrupted");
        }
    }
}
