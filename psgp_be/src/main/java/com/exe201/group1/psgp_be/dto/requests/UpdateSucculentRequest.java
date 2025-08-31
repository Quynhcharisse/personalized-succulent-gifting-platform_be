package com.exe201.group1.psgp_be.dto.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateSucculentRequest {
    Integer id;
    String species_name;
    String description;
    Integer quantity;
    BigDecimal priceBuy;
    String fengShui;
    String zodiac;
    String status;
}
