package com.traffic.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.couponcore.component.DistributeLockExecutor;
import com.traffic.couponcore.exception.CouponIssueException;
import com.traffic.couponcore.exception.ErrorCode;
import com.traffic.couponcore.model.Coupon;
import com.traffic.couponcore.repository.redis.RedisRepository;
import com.traffic.couponcore.repository.redis.dto.CouponIssueRequest;
import com.traffic.couponcore.repository.redis.dto.CouponRedisEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.traffic.couponcore.exception.ErrorCode.*;
import static com.traffic.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static com.traffic.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {

    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final CouponCacheService couponCacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId) {
        CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);
        coupon.checkIssuableCoupon();
        distributeLockExecutor.execute("lock_%s".formatted(couponId), 3000, 3000, () -> {
            couponIssueRedisService.checkCouponIssueQuantity(coupon, userId);
            issueRequest(couponId, userId);
        });
    }

    private void issueRequest(long couponId, long userId) {
        CouponIssueRequest issueRequest = new CouponIssueRequest(couponId, userId);

        try {
            String value = objectMapper.writeValueAsString(issueRequest);

            redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));
            redisRepository.rPush(getIssueRequestQueueKey(), value);

        } catch (JsonProcessingException e) {
            throw new CouponIssueException(FAIL_COUPON_ISSUE_REQUEST, "input: %s".formatted(issueRequest));
        }
    }

}
