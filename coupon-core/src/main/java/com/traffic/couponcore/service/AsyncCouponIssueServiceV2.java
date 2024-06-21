package com.traffic.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.couponcore.component.DistributeLockExecutor;
import com.traffic.couponcore.exception.CouponIssueException;
import com.traffic.couponcore.repository.redis.RedisRepository;
import com.traffic.couponcore.repository.redis.dto.CouponIssueRequest;
import com.traffic.couponcore.repository.redis.dto.CouponRedisEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.traffic.couponcore.exception.ErrorCode.FAIL_COUPON_ISSUE_REQUEST;
import static com.traffic.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static com.traffic.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV2 {

    private final RedisRepository redisRepository;
    private final CouponCacheService couponCacheService;

    public void issue(long couponId, long userId) {
        CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);
        coupon.checkIssuableCoupon();
        issueRequest(couponId, userId, coupon.totalQuantity());
    }

    private void issueRequest(long couponId, long userId, Integer totalIssueQuantity) {
        if(totalIssueQuantity == null) {
            redisRepository.issueRequest(couponId, userId, Integer.MAX_VALUE);
        }

        redisRepository.issueRequest(couponId, userId, totalIssueQuantity);
    }

}
