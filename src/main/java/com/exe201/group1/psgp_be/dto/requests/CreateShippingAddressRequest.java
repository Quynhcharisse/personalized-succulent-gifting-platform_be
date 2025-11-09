package com.exe201.group1.psgp_be.dto.requests;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateShippingAddressRequest {
    String  shippingAddress;
    Integer shippingProvinceId;
    Integer shippingDistrictId;
    String  shippingWardCode;
}
