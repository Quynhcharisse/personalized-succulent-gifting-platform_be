package com.exe201.group1.psgp_be.dto.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAccessoryRequest {

    boolean createAction;

    boolean createPot;
    PotData potData;// if createPot is false then send null here

    boolean createSoil;
    SoilData soilData;// if createSoil is false then send null here

    boolean createDecoration;
    DecorationData decorationData;// if createDecoration is false then send null here

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PotData {
        String name;
        String description;
        String material;
        String color;
        List<Image> images;
        List<Size> sizes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Size {
        String name;
        double potHeight;
        double potUpperCrossSectionArea; // The area of upper cross-section in a size of pot
        double maxSoilMassValue; // the maximum mass value for soil in this size of pot
        int availableQty;
        long price;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SoilData {
        String name;
        String description;
        int availableMassValue;
        BasePricing basePricing;
        List<Image> images;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BasePricing { // The base pricing is the price per mass value based on mass unit, Ex. 5000 vnd per 100 gram of soil
        double massValue; // the value of mass for base price
        String massUnit; // the unit of mass used, ex. gram, kg,..
        long price;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DecorationData {
        String name;
        String description;
        long price;
        int availableQty;
        List<Image> images;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Image {
        String image;
    }
}
