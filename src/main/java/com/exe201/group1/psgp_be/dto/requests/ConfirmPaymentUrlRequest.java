package com.exe201.group1.psgp_be.dto.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
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
    Integer shippingAddressId;


    ConfirmPaymentUrlRequest.Size size;
    List<CreateOrUpdateProductRequest.Image> images;
    String occasion;
    BigDecimal amount;
    boolean isCustomRequest;

    boolean success;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Size {
        List<CreateOrUpdateProductRequest.Succulent> succulents;
        CreateOrUpdateProductRequest.Pot pot;
        CreateOrUpdateProductRequest.Soil soil;
        CreateOrUpdateProductRequest.Decoration decoration;
    }

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
