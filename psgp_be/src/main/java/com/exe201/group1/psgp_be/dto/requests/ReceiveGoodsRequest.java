package com.exe201.group1.psgp_be.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiveGoodsRequest {

    private String supplierName;
    private String supplierPhone;
    private String note;
    private List<GoodsItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GoodsItem {
        private Integer succulentId;
        private Integer accessoryId;
        private String itemType;
        private Integer quantity;
        private BigDecimal priceBuy;
    }
}
