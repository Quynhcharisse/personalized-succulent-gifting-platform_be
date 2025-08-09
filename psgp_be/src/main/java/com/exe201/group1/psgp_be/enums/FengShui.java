package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FengShui {
    KIM("Kim"),
    MOC("Mộc"),
    THUY("Thủy"),
    HOA("Hỏa"),
    THO("Thổ");

    private final String displayName;
}
