package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CreateDiscountProgramRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface DiscountProgramService {
    ResponseEntity<ResponseObject> createDiscountPrograms(CreateDiscountProgramRequest request);
    ResponseEntity<ResponseObject> getDiscountPrograms();
}
