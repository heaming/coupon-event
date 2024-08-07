package com.traffic.couponapi.controller;

import com.traffic.couponapi.controller.dto.CouponIssueRequestDto;
import com.traffic.couponapi.controller.dto.CouponIssueResponseDto;
import com.traffic.couponapi.service.CouponIssueRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CouponIssueController {

    private final CouponIssueRequestService couponIssueRequestService;

    @PostMapping("/v1/issue")
    public CouponIssueResponseDto issueV1(@RequestBody CouponIssueRequestDto request) {
        couponIssueRequestService.issueReqeustV1(request);
        return new CouponIssueResponseDto(true, null);
    }

    @PostMapping("/v1/issue-async")
    public CouponIssueResponseDto asyncIssueV1(@RequestBody CouponIssueRequestDto request) {
        couponIssueRequestService.asyncIssueReqeustV1(request);
        return new CouponIssueResponseDto(true, null);
    }

    @PostMapping("/v2/issue-async")
    public CouponIssueResponseDto asyncIssueV2(@RequestBody CouponIssueRequestDto request) {
        couponIssueRequestService.asyncCouponIssueServiceV2(request);
        return new CouponIssueResponseDto(true, null);
    }

}
