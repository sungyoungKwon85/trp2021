package com.kkwonsy.trp.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate redisTemplate;

    private Long memberId;

    @BeforeEach
    public void beforeEach() {
        memberId = Long.MAX_VALUE;
    }

    @Test
    public void test_putCityId_AND_getCityIds()
        throws InterruptedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        redisTemplate.delete(getInquiredCityKey());
        redisService.putCityId(memberId, 5l);
        Thread.sleep(1000);
        redisService.putCityId(memberId, 6l);
        redisService.putCityId(memberId, 7l);
        Thread.sleep(1000);
        long latest = 5l;
        redisService.putCityId(memberId, latest); // renew!!!
        List<Long> cityIds = redisService.getCityIds(memberId, null, 2);

        assertNotNull(cityIds);
        assertEquals(cityIds.size(), 2);
        assertEquals(cityIds.get(0), latest);
    }

    private String getInquiredCityKey()
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = redisService.getClass().getDeclaredMethod("getInquiredCityKey", Long.class);
        method.setAccessible(true);
        return (String) method.invoke(redisService, memberId);
    }
}