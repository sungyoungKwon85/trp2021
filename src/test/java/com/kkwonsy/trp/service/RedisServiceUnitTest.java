package com.kkwonsy.trp.service;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class RedisServiceUnitTest {

    @InjectMocks
    private RedisService redisService;

    @Mock
    private RedisTemplate redisTemplate;

    @Test
    public void getScoreForZSet() throws Exception {
        // given
        Method method = redisService.getClass().getDeclaredMethod("getScoreForZSet");
        method.setAccessible(true);
        // when
        Double invoke = (Double) method.invoke(redisService);

        // then
        assertNotNull(invoke);
        System.out.println(invoke);
        Thread.sleep(1000);
        System.out.println(method.invoke(redisService));
    }
}