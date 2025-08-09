package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentStatus {
    ACTIVE("Hoạt động"),
    HIDDEN("Đã ẩn"),
    DELETED("Đã xóa");

    private final String displayName;
} 