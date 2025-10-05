package com.exe201.group1.psgp_be.dto.requests;

import com.exe201.group1.psgp_be.enums.FengShui;
import com.exe201.group1.psgp_be.enums.Zodiac;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateSucculentRequest {
    String speciesName;
    String description;
    String imageUrl;
    List<String> fengShuiList;
    List<String> zodiacList;
    List<Size> sizeList;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Size {
        String sizeName;
        long price;
        double minArea;
        double maxArea;
        int quantity;
    }
}
