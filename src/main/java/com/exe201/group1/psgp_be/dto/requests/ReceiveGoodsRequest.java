package com.exe201.group1.psgp_be.dto.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiveGoodsRequest {

    int supplierId; // Thay đổi từ supplierName sang supplierId
    String referenceCode; // Mã hóa đơn, mã đơn hàng
    String note;
    List<GoodsItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GoodsItem {
        int succulentId; // Sử dụng Integer để khớp với entity
        int accessoryId; // Sử dụng Integer để khớp với entity
        String itemType; // SUCCULENT hoặc ACCESSORY
        int quantity;
        BigDecimal priceBuy;
    }
}
