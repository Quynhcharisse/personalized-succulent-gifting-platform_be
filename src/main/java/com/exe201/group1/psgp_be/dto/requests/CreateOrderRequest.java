package com.exe201.group1.psgp_be.dto.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderRequest {

    BigDecimal shippingFee;
    BigDecimal discountAmount;
    BigDecimal finalAmount;

    String shippingAddress;
    String shippingPhone;
    String shippingNote;
    String paymentMethod;

    List<Integer> productIds;
}
