package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.GhnApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ghn")
@RequiredArgsConstructor
public class GhnController {

    private final GhnApiService ghnApiService;

    // 🏙️ Danh sách tỉnh
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
}
