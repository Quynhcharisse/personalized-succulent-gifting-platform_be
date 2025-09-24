package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    //----------Succulent, Assessory, Product Status---------//
    AVAILABLE("Đang còn hàng"),
    OUT_OF_STOCK("Hết hàng"),

    //----------Supplier Status---------//
    ACTIVE("Đang hoạt động"),
    INACTIVE("Ngưng hoạt động");

    private final String value;

}
