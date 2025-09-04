package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    AVAILABLE("Đang còn hàng"),
    OUT_OF_STOCK("Hết hàng"),
    UNAVAILABLE("Ngưng nhập hàng");
    private final String displayName;

}
