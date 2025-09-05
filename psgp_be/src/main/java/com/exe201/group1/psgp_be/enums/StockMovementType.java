package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StockMovementType {
    PURCHASE_IN("Nhập hàng từ nhà cung cấp"),
    ADJUSTMENT_IN("Điều chỉnh tăng tồn kho"),
    ADJUSTMENT_OUT("Điều chỉnh giảm tồn kho"),
    SALE_OUT("Xuất bán cho khách hàng"),
    DAMAGE_OUT("Xuất do hỏng hóc"),
    RETURN_OUT("Trả hàng cho nhà cung cấp"),
    RETURN_IN("Nhận hàng trả từ khách");

    private final String displayName;
}
