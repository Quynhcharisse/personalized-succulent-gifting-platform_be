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
public class ProductUpdateRequest {
    Integer id;
    String name;
    String description;
    String size;
    BigDecimal price;
    Integer quantityInStock;
    String status;
    List<Integer> succulentIds;
    List<Integer> accessoryIds;
    List<CreateProductImageRequest> images;
}
