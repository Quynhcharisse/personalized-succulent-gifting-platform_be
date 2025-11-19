package com.exe201.group1.psgp_be.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    //----------Succulent, Accessory, Product Status---------//
    AVAILABLE("Đang còn hàng"),
    OUT_OF_STOCK("Hết hàng"),
    UNAVAILABLE("Ngừng kinh doanh"),

    //----------Supplier Status---------//
    ACTIVE("Đang hoạt động"),
    INACTIVE("Ngưng hoạt động"),

    //----------Post Status---------//
    DRAFT("Bản nháp"),
    PUBLISHED("Đã đăng"),
    ARCHIVED("Đã lưu trữ"),

    //----------Comment Status---------//
    VISIBLE("Hiển thị"),
    DELETED("Đã xóa"),

    //----------Custom Product Request Status---------//
    PENDING("Đang chờ duyệt"),
    APPROVE("Đã duyệt"),
    REJECT("Đã từ chối"),

    //----------SHIPPING STATUS---------//
    PACKAGING("Đang đóng gói"),
    SHIPPING("Đang vận chuyển"),
    DONE("Đơn hàng thành công"),
    FAILED_SHIPPING("Vận chuyển thất bại");

    private final String value;

    public static Status getByValue(String value) {
        for (Status status : Status.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return OUT_OF_STOCK;
    }
}
