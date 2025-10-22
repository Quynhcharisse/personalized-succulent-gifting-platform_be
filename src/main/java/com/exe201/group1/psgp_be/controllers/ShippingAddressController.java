package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CreateShippingAddressRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.ShippingAddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/address")
@Tag(name = "Shipping Address", description = "APIs for shipping address")
public class ShippingAddressController {

    private final ShippingAddressService shippingAddressService;

    @PostMapping
    public ResponseEntity<ResponseObject> CreateShippingAddress(@RequestBody CreateShippingAddressRequest request){
        return shippingAddressService.CreateShippingAddress(request);
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseObject> GetAllShippingAddresses(){
        return shippingAddressService.getShippingAddressList();
    }

}
