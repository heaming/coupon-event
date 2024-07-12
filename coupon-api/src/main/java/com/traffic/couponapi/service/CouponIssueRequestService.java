package com.traffic.couponapi.service;

import com.traffic.couponapi.controller.dto.CouponIssueRequestDto;
import com.traffic.couponcore.component.DistributeLockExecutor;
import com.traffic.couponcore.service.AsyncCouponIssueServiceV1;
import com.traffic.couponcore.service.AsyncCouponIssueServiceV2;
import com.traffic.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final AsyncCouponIssueServiceV1 asyncCouponIssueServiceV1;
    private final AsyncCouponIssueServiceV2 asyncCouponIssueServiceV2;

    public void issueReqeustV1(CouponIssueRequestDto request) {
        couponIssueService.issue(request.couponId(), request.userId());
        log.info("[쿠폰 발급 완료] couponId: %s, userId: %s".formatted(request.couponId(), request.userId()));
    }

    public void asyncIssueReqeustV1(CouponIssueRequestDto request) {
        asyncCouponIssueServiceV1.issue(request.couponId(), request.userId());
        log.info("[쿠폰 발급 완료] couponId: %s, userId: %s".formatted(request.couponId(), request.userId()));
    }

    public void asyncCouponIssueServiceV2(CouponIssueRequestDto request) {
        asyncCouponIssueServiceV2.issue(request.couponId(), request.userId());
        log.info("[쿠폰 발급 완료] couponId: %s, userId: %s".formatted(request.couponId(), request.userId()));
    }
}
