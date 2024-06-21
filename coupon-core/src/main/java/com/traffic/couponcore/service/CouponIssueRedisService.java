package com.traffic.couponcore.service;

import com.traffic.couponcore.exception.CouponIssueException;
import com.traffic.couponcore.repository.redis.RedisRepository;
import com.traffic.couponcore.repository.redis.dto.CouponRedisEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.traffic.couponcore.exception.ErrorCode.DUPLICATED_COUPON_ISSUE;
import static com.traffic.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;
import static com.traffic.couponcore.util.CouponRedisUtils.getIssueRequestKey;

@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {

    private final RedisRepository redisRepository;

    public void checkCouponIssueQuantity(CouponRedisEntity coupon, long userId) {
        if (!availableUserIssueQuantity(coupon.id(), userId)) {
            throw new CouponIssueException(DUPLICATED_COUPON_ISSUE, "발급 가능한 수량을 초과합니다. couponId : %s, userId: %s".formatted(coupon.id(), userId));
        }
        if (!availableTotalIssueQuantity(coupon.totalQuantity(), coupon.id())) {
            throw new CouponIssueException(INVALID_COUPON_ISSUE_QUANTITY, "발급 가능한 수량을 초과합니다. couponId : %s, userId : %s".formatted(coupon.id(), userId));
        }
    }

    /**
     * @title 쿠폰 발급 수량 확인
     * @param totalQuantity
     * @param couponId
     * @return
     */
    public boolean availableTotalIssueQuantity(Integer totalQuantity, long couponId) {
        if (totalQuantity == null) {
            return true;
        }
        String key = getIssueRequestKey(couponId);
        return totalQuantity > redisRepository.sCard(key);
    }

    /**
     * @title 중복발급 확인
     * @param couponId
     * @return
     */
    public boolean availableUserIssueQuantity(long couponId, long userId) {
        String key = getIssueRequestKey(couponId);
        return !redisRepository.sIsmember(key, String.valueOf(userId));
    }

}
