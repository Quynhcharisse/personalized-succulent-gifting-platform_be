package com.exe201.group1.psgp_be.dto.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ConfirmPaymentUrlRequest {
    List<ProductData> products;
    long orderCode;
    long shippingFee;
    boolean success;
    Integer shippingAddressId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProductData {
        Integer productId;
        String size;
        long price;
        Integer quantity;
    }
}
