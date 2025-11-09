package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Type {
    DEPOSIT("Nạp tiền"),
    WITHDRAW("Rút tiền"),
    PAYMENT("Thanh toán"),
    TRANSFER("Chuyển tiền");

    private final String displayName;
}
