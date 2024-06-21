package com.traffic.couponcore.service;

import com.traffic.couponcore.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.stream.IntStream;

import static com.traffic.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static org.junit.jupiter.api.Assertions.*;

class CouponIssueRedisServiceTest extends TestConfig {

    @Autowired
    CouponIssueRedisService  couponIssueRedisService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 발급 가능 수량이 존재하면 true 반환")
    void availableTotalIssueQuantity_1() {

        int totalQuantity = 10;
        int couponId = 1;

        boolean result = couponIssueRedisService.availableTotalIssueQuantity(totalQuantity, couponId);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 발급 가능 수량이 소진되면 false 반환")
    void availableTotalIssueQuantity_2() {

        int totalQuantity = 10;
        int couponId = 1;
        IntStream.range(0, totalQuantity).forEach(userId -> {
            redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));
        });


        boolean result = couponIssueRedisService.availableTotalIssueQuantity(totalQuantity, couponId);

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하지 않으면 true 반환")
    void availableUserIssueQuantity_1() {
        long couponId = 1;
        long userId = 1;

        boolean result = couponIssueRedisService.availableUserIssueQuantity(couponId, userId);

        Assertions.assertTrue(result);

    }
    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하면 false 반환")
    void availableUserIssueQuantity_2() {
        long couponId = 1;
        long userId = 1;
        redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));

        boolean result = couponIssueRedisService.availableUserIssueQuantity(couponId, userId);

        Assertions.assertFalse(result);

    }



}