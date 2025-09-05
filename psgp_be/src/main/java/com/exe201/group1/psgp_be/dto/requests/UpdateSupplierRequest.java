package com.exe201.group1.psgp_be.dto.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateSupplierRequest {
    int id;
    String supplierName;
    String contactPerson;
    String phone;
    String email;
    String address;
    String description;
    String status; // AVAILABLE, UNAVAILABLE
}
