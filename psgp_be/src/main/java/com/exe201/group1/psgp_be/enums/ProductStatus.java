package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {
    ACTIVE("Hoạt động"),
    INACTIVE("Không hoạt động"),
    OUT_OF_STOCK("Hết hàng"),
    DELETED("Đã xóa");

    private final String displayName;
} 