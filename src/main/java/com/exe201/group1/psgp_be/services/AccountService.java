package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CreateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.ProcessAccountRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateProfileRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierStatusRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AccountService {
    ResponseEntity<ResponseObject> logout(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<ResponseObject> updateProfile(UpdateProfileRequest request, HttpServletRequest httRequest);

    ResponseEntity<ResponseObject> viewProfile(HttpServletRequest httRequest);

    ResponseEntity<ResponseObject> getAccessToken(HttpServletRequest request);

    ResponseEntity<ResponseObject> getAllBuyerAccounts(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getTotalBuyerCount(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> processAccount(ProcessAccountRequest request, String action);

    //--------------------------------------------SUPPLIER--------------------------------------------//
    ResponseEntity<ResponseObject> createSupplier(CreateSupplierRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getSupplierList(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> updateSupplier(UpdateSupplierRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> updateSupplierStatus(UpdateSupplierStatusRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getTotalSupplierCount(HttpServletRequest httpRequest);
}
