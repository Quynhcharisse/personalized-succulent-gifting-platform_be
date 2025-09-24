package com.exe201.group1.psgp_be.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "`coupons`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "code", length = 50, unique = true)
    String code;

    String name;

    String description;

    String discountType; // "PERCENTAGE", "FIXED_AMOUNT"

    @Column(name = "discount_value", precision = 10, scale = 2)
    BigDecimal discountValue;

    @Column(name = "min_order_amount", precision = 10, scale = 2)
    BigDecimal minOrderAmount;

    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    BigDecimal maxDiscountAmount;

    @Column(name = "usage_limit")
    Integer usageLimit;

    @Column(name = "used_count")
    @Builder.Default
    Integer usedCount = 0;

    @Column(name = "is_active")
    @Builder.Default
    boolean isActive = true;

    @Column(name = "start_date")
    LocalDateTime startDate;

    @Column(name = "expiry_date")
    LocalDateTime expiryDate;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    String createdBy;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    String updatedBy;
}
