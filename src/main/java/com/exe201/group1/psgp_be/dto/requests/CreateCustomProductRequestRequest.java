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
public class CreateCustomProductRequestRequest {
    Size size;
    List<CreateOrUpdateProductRequest.Image> images;
    String occasion; // User nhập tự do
    
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
}
