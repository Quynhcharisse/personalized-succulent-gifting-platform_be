package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CreateCustomProductRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateRevisionRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomProductRequestDesignImageRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface CustomRequestService {
    //--------------------------------------------CUSTOM PRODUCT--------------------------------------------//
    ResponseEntity<ResponseObject> createCustomProductRequest(CreateCustomProductRequestRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> viewCustomProductRequest();

    ResponseEntity<ResponseObject> viewCustomProductRequestDetail(int id);

    ResponseEntity<ResponseObject> updateCustomProductRequestDesignImage(UpdateCustomProductRequestDesignImageRequest request, boolean approved);

    ResponseEntity<ResponseObject> createRevision(CreateRevisionRequest request);
}
