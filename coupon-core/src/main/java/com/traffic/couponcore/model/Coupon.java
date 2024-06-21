package com.traffic.couponcore.model;

import com.traffic.couponcore.exception.CouponIssueException;
import com.traffic.couponcore.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "coupons")
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    private Integer totalQuantity;

    @Column(nullable = false)
    private int issuedQuantity;

    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private int minAvailableAmount;

    @Column(nullable = false)
    private LocalDateTime dateIssueStart;

    @Column(nullable = false)
    private LocalDateTime dateIssueEnd;

    /**
     * @title 발급 수량 검증
     * @return boolean 발급 가능 여부
     */
    public boolean availableIssueQuantity() {
        if(totalQuantity == null) {
            return true;
        }
        return totalQuantity > issuedQuantity;
    }

    /**
     * @title 발급 기한 검증
     * @return boolean 발급 가능 여부
     */
    public boolean availableIssueDate() {
        LocalDateTime now = LocalDateTime.now();
        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);
    }

    public void issue() {
        if(!availableIssueQuantity()) {
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,
                    "발급 가능한 수량을 초과합니다. total : %s, issued: %s".formatted(totalQuantity, issuedQuantity));
        }

        if(!availableIssueDate()) {
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_DATE,
                    "발급 가능한 일자가 아닙니다. request: %s, valid: %s - %s".formatted(LocalDateTime.now(), dateIssueStart, dateIssueEnd));
        }

        issuedQuantity++;
    }
}
