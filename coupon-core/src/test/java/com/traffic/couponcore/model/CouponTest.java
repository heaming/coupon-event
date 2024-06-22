package com.traffic.couponcore.model;

import com.traffic.couponcore.exception.CouponIssueException;
import com.traffic.couponcore.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;

import static com.traffic.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;
import static com.traffic.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;

class CouponTest {

    @Test
    @DisplayName("발급 수량이 남아 있다면 -> true")
    void availableIssueQuantity_true() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("발급 수량이 남아 있다면 -> false")
    void availableIssueQuantity_false() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("최대 발급 수량 설정 X -> true")
    void availableIssueQuantity_null() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(null)
                .issuedQuantity(100)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("발급 기한 이전 -> false")
    void availableIssueDate_prev() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        Assertions.assertFalse(result);
    }
    @Test
    @DisplayName("발급 기한 해당 -> true")
    void availableIssueDate_in() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("발급 기한 이후 -> false")
    void availableIssueDate_next() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("발급 수량, 발급 기간 유효 -> 발급 성공")
    void issueTest1() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        coupon.issue();

        // then
        Assertions.assertEquals(coupon.getIssuedQuantity(), 100);
    }

    @Test
    @DisplayName("발급 수량 초과 -> 예외 반환")
    void issueTest2() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(exception.getErrorCode(), INVALID_COUPON_ISSUE_QUANTITY);

    }

    @Test
    @DisplayName("발급 기간 x -> 예외 반환")
    void issueTest3() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().plusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(3))
                .build();

        // when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(exception.getErrorCode(), INVALID_COUPON_ISSUE_DATE);

    }

    @Test
    @DisplayName("발급 기간 종료되면 true 반환")
    void isIssueComplete1() {
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();

        boolean result = coupon.isIssueComplete();

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("잔여 발급 수량이 없다면 true 반환")
    void isIssueComplete2() {
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        boolean result = coupon.isIssueComplete();

        Assertions.assertTrue(result);
    }
    @Test
    @DisplayName("발급 기한과 수량이 유효하면 false 반환")
    void isIssueComplete3() {
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        boolean result = coupon.isIssueComplete();

        Assertions.assertFalse(result);
    }
}