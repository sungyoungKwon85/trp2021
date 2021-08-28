package com.kkwonsy.trp.config;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RedisConfigTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void contextLoad() {
    }

    @Test
    public void redis_opsForList_and_order() throws InterruptedException {
        final String key = "test_key";

        redisTemplate.delete(key);
        ListOperations<String, Long> listOperations = redisTemplate.opsForList();
        listOperations.leftPush(key, 1L);
        listOperations.leftPush(key, 2L);

        List<Long> result = listOperations.range(key, 0, 9);
        List<Long> result2 = listOperations.range(key, 0, 9);

        List<Long> expectedList = Arrays.asList(2L, 1L);
        assertNotNull(result);
        assertNotNull(result2);
        assertEquals(result.size(), 2);
        assertEquals(result2.size(), 2);
        assertIterableEquals(expectedList, result);
        assertIterableEquals(expectedList, result2);
        redisTemplate.delete(key);
    }
}