package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Size {
    //sen đá 
    TINY("3cm"),
    SMALL("7cm"),
    MEDIUM("10cm"),
    LARGE("13cm"),
    EXTRA_LARGE("16-18cm"),

    // chậu cây
    XS("5–6cm"),
    S("7–8cm"),
    M("9–10cm"),
    L("12–13cm"),
    XL("15–16cm"),
    XXL("18–20cm");

    private final String displayName;

    public static Size fromDisplayName(String displayName) {
        for (Size size : Size.values()) {
            if (displayName.equals(size.displayName)) {
                return size;
            }
        }
        return null;
    }
}
