package com.exe201.group1.psgp_be.dto.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAccessoryRequest {
       String name;
       String description;
       Integer quantity;
       String category;
       BigDecimal priceBuy;
}
