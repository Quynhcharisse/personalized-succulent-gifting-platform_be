package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Size {
    TINY("3cm"),
    SMALL("7cm"),
    MEDIUM("10cm"),
    LARGE("13cm"),
    EXTRA_LARGE("16-18cm");

    private final String displayName;
}
