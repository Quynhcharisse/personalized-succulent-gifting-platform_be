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
    SMALL_POT("Chậu nhỏ (5-8cm)"),
    MEDIUM_POT("Chậu vừa (9-13cm)"),
    LARGE_POT("Chậu lớn (14-20cm)");
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
