package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccessoryCategory {
    PLANT_POT("Chậu cây"),
    SOIL("Đất trồng"),
    DECOR_ACCESSORY("Phụ kiện trang trí");

    private final String displayName;
    }
