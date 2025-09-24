package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Occasion {
    SINH_NHAT("Sinh nhật"),
    KY_NIEM("Kỷ niệm"),
    TOT_NGHIEP("Tốt nghiệp"),
    THANG_CHUC("Thăng chức"),
    TAN_GIA("Tân gia"),
    NHA_GIAO_VIET_NAM("Ngày Nhà giáo Việt Nam"),
    QUOC_TE_PHU_NU("Ngày Quốc tế Phụ nữ"),
    PHU_NU_VIET_NAM("Ngày Phụ nữ Việt Nam"),
    NGAY_CUA_ME("Ngày của Mẹ"),
    NGAY_CUA_CHA("Ngày của Cha"),
    VALENTINE("Valentine"),
    GIANG_SINH("Giáng sinh"),
    NAM_MOI_TET("Năm mới/Tết"),
    CAM_ON("Cảm ơn"),
    CHIA_TAY("Chia tay/Chuyển công tác"),
    CHUC_SUCKHOE("Chúc sức khỏe/Bình an");

    private final String displayName;

}

