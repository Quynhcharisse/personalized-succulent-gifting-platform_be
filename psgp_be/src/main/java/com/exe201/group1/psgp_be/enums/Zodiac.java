package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Zodiac {
    BACH_DUONG("Bạch Dương"),
    KIM_NGUU("Kim Ngưu"),
    SONG_TU("Song Tử"),
    CU_GIAI("Cự Giải"),
    SU_TU("Sư Tử"),
    XU_NU("Xử Nữ"),
    THIEN_BINH("Thiên Bình"),
    BO_CAP("Bọ Cạp"),
    NHAN_MA("Nhân Mã"),
    MA_KET("Ma Kết"),
    BAO_BINH("Bảo Bình"),
    SONG_NGU("Song Ngư");

    private final String displayName;
}
