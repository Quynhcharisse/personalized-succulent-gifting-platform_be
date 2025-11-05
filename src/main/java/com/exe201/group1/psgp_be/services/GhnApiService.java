package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CaculateFeeRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface GhnApiService {
    ResponseEntity<ResponseObject> getProvinces();
    ResponseEntity<ResponseObject> getDistricts(Integer provinceId);
    ResponseEntity<ResponseObject> getWards(Integer districtId);
    ResponseEntity<ResponseObject> caculateFee(CaculateFeeRequest request);
}
