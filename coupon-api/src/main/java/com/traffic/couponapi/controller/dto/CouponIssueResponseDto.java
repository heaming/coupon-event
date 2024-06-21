package com.traffic.couponapi.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

@JsonInclude(value = NON_NULL)
public record CouponIssueResponseDto(boolean isSuccess, String comment) {
}
