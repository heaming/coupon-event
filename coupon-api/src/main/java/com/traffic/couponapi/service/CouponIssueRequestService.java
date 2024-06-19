package com.traffic.couponapi.service;

import com.traffic.couponapi.controller.dto.CouponIssueRequestDto;
import com.traffic.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;

    public void issueReqeustV1(CouponIssueRequestDto request) {
        couponIssueService.issue(request.couponId(), request.userId());
        log.info("[쿠폰 발급 완료] couponId: %s, userId: %s".formatted(request.couponId(), request.userId()));
    }



}
