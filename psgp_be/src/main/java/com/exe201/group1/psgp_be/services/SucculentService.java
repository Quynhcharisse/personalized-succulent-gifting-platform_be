package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSucculentRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface SucculentService {
    ResponseEntity<ResponseObject> createSucculent(CreateSucculentRequest request);

    ResponseEntity<ResponseObject> getSucculents();

    ResponseEntity<ResponseObject> updateSucculent(UpdateSucculentRequest request);
}
