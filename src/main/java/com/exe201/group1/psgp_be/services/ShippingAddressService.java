package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CreateShippingAddressRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface ShippingAddressService {
    ResponseEntity<ResponseObject> CreateShippingAddress(CreateShippingAddressRequest request);
    ResponseEntity<ResponseObject> getShippingAddressList();
}
