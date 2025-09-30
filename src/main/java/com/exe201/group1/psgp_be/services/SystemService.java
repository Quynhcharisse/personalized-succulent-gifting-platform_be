package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.UpdateBusinessConfigRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface SystemService {
    ResponseEntity<ResponseObject> getBusinessConfig();

    ResponseEntity<ResponseObject> updateBusinessConfig(UpdateBusinessConfigRequest request);
}
