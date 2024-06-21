package com.traffic.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.couponcore.TestConfig;
import com.traffic.couponcore.exception.CouponIssueException;
import com.traffic.couponcore.model.Coupon;
import com.traffic.couponcore.model.CouponType;
import com.traffic.couponcore.repository.mysql.CouponJpaRepository;
import com.traffic.couponcore.repository.redis.dto.CouponIssueRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.IntStream;

import static com.traffic.couponcore.exception.ErrorCode.*;
import static com.traffic.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;
import static com.traffic.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static com.traffic.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

class AsyncCouponIssueServiceV2Test extends TestConfig {

    @Autowired
    AsyncCouponIssueServiceV2 asyncCouponIssueServiceV2;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰이 존재하지 않는다면 예외 반환")
    void issue_1() {
        long couponId = 1;
        long userId = 1;

        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () ->{
            asyncCouponIssueServiceV2.issue(couponId, userId);
        });

        Assertions.assertEquals(exception.getErrorCode(), COUPON_NOT_EXIST);
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 가능 수량 x -> 예외 반환")
    void issue_2() {
        long userId = 1000;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        IntStream.range(0, coupon.getTotalQuantity()).forEach(idx -> {
            redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(idx));
        });

        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () ->{
            asyncCouponIssueServiceV2.issue(coupon.getId(), userId);
        });

        Assertions.assertEquals(exception.getErrorCode(), INVALID_COUPON_ISSUE_QUANTITY);
    }
    @Test
    @DisplayName("쿠폰 발급 - 이미 발급된 유저 -> 예외 반환")
    void issue_3() {
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);
        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(userId));


        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () ->{
            asyncCouponIssueServiceV2.issue(coupon.getId(), userId);
        });

        Assertions.assertEquals(exception.getErrorCode(), DUPLICATED_COUPON_ISSUE);
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 기한 유효 x -> 예외 반환")
    void issue_4() {
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        couponJpaRepository.save(coupon);
        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(userId));


        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () ->{
            asyncCouponIssueServiceV2.issue(coupon.getId(), userId);
        });

        Assertions.assertEquals(exception.getErrorCode(), INVALID_COUPON_ISSUE_DATE);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급 기록")
    void issue_5() {
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        couponJpaRepository.save(coupon);

        asyncCouponIssueServiceV2.issue(coupon.getId(), userId);

        Boolean isSaved = redisTemplate.opsForSet().isMember(getIssueRequestKey(coupon.getId()), String.valueOf(userId));

        Assertions.assertTrue(isSaved);

    }

    @Test
    @DisplayName("쿠폰 발급 - 발급이 성공하면 쿠폰 발급 queue에 적재된다")
    void issue_6() throws JsonProcessingException {
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        couponJpaRepository.save(coupon);
        CouponIssueRequest request = new CouponIssueRequest(coupon.getId(), userId);

        asyncCouponIssueServiceV2.issue(coupon.getId(), userId);

        String savedIssueRequest = redisTemplate.opsForList().leftPop(getIssueRequestQueueKey());

        Assertions.assertEquals(new ObjectMapper().writeValueAsString(request), savedIssueRequest);

    }

}