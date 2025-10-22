package com.exe201.group1.psgp_be.dto.requests;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDiscountProgramRequest {
    String name;
    String description;
    BigDecimal minimumOrderValue;
    Integer usageLimit;
    BigDecimal discountValue;
    Boolean isPercentage;
}
