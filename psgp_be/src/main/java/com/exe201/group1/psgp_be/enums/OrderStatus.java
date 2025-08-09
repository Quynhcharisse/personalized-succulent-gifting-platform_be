package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    PROCESSING("Đang xử lý"),
    SHIPPED("Đã gửi hàng"),
    DELIVERED("Đã giao hàng"),
    CANCELLED("Đã hủy"),
    REFUNDED("Đã hoàn tiền");

    private final String displayName;
} 