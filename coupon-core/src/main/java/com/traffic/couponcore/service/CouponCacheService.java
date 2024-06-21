package com.traffic.couponcore.service;

import com.traffic.couponcore.model.Coupon;
import com.traffic.couponcore.repository.redis.dto.CouponRedisEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponCacheService {

    private final CouponIssueService couponIssueService;

    @Cacheable(cacheNames = "coupon")
    public CouponRedisEntity getCouponCache(long couponId) {
        Coupon coupon = couponIssueService.findCouponById(couponId);
        return new CouponRedisEntity(coupon);
    }

}
