package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    //----------Succulent, Assessory, Product Status---------//
    AVAILABLE("Đang còn hàng"),
    OUT_OF_STOCK("Hết hàng"),
    UNAVAILABLE("Ngừng kinh doanh"),

    //----------Supplier Status---------//
    ACTIVE("Đang hoạt động"),
    INACTIVE("Ngưng hoạt động");

    private final String value;

    public static Status getByValue(String value) {
        for (Status status : Status.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return OUT_OF_STOCK;
    }

}
