package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CaculateFeeRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.GhnApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ghn")
@RequiredArgsConstructor
public class GhnController {

    private final GhnApiService ghnApiService;

    // Danh sách tỉnh
    @GetMapping("/provinces")
    public ResponseEntity<ResponseObject> getProvinces() {
        return ghnApiService.getProvinces();
    }

    @GetMapping("/districts")
    public ResponseEntity<ResponseObject> getDistricts(@RequestParam Integer provinceId) {
        return ghnApiService.getDistricts(provinceId);
    }

    @GetMapping("/wards")
    public ResponseEntity<ResponseObject> getWards(@RequestParam Integer districtId) {
        return ghnApiService.getWards(districtId);
    }

    @PostMapping("/calculate/fee")
    public ResponseEntity<ResponseObject> calculateFee(@RequestBody CaculateFeeRequest request){
        return ghnApiService.caculateFee(request);
    }
}
