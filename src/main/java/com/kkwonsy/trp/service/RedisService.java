package com.kkwonsy.trp.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, Long> redisTemplate;
    private ZSetOperations<String, Long> zSetOps;

    private static final int TIME_TO_LIVE_INQUIRED_CITY = 7;
    private static final String PREFIX_KEY_INQUIRED_CITY = "inquired_city_";

    @PostConstruct
    private void postConstruct() {
        zSetOps = redisTemplate.opsForZSet();
    }

    public void putCityId(Long memberId, Long cityId) {
        final String key = getInquiredCityKey(memberId);
        redisTemplate.expire(key, TIME_TO_LIVE_INQUIRED_CITY, TimeUnit.DAYS);

        final Double scoreForZSet = getScoreForZSet();
        zSetOps.add(key, cityId, scoreForZSet);
    }


    public List<Long> getCityIds(Long memberId, List<Long> notInIds, int size) {
        final String key = getInquiredCityKey(memberId);
        final Long dataLength = zSetOps.size(key);
        List<Long> ids = zSetOps.range(key, dataLength - size, dataLength - 1)
            .stream().collect(Collectors.toList());
        if (notInIds != null) {
            ids.removeIf(id -> notInIds.contains(id));
        }
        Collections.reverse(ids);
        return ids;
    }

    private String getInquiredCityKey(Long memberId) {
        return PREFIX_KEY_INQUIRED_CITY + memberId;
    }

    private Double getScoreForZSet() {
        final Long l = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        return l.doubleValue();
    }
}
