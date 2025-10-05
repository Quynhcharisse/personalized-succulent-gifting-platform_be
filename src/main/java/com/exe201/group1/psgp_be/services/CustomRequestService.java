package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.DeleteCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface CustomRequestService {

    //--------------------------------------------CUSTOM REQUEST--------------------------------------------//
    ResponseEntity<ResponseObject> customRequestListByBuyer(HttpServletRequest request);

    ResponseEntity<ResponseObject> createCustomRequest(CreateCustomRequest request);

    ResponseEntity<ResponseObject> updateCustomRequest(UpdateCustomRequestRequest request);

    ResponseEntity<ResponseObject> deleteCustomRequest(DeleteCustomRequestRequest request);

}
