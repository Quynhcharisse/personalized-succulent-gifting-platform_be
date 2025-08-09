package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestStatus {
    PENDING("Chờ xử lý"),
    APPROVED("Đã chấp nhận"),
    REJECTED("Đã từ chối"),
    IN_PROGRESS("Đang thực hiện"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy");

    private final String displayName;
} 