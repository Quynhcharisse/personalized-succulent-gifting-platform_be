package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductCreateRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductUpdateRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

        // =========================== SELLER  =========================== \\

    @Override
    public ResponseEntity<ResponseObject> createProduct(ProductCreateRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> viewProduct() {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> updateProduct(ProductUpdateRequest request) {
        return null;
    }

        // =========================== ---------  =========================== \\















    // =========================== BUYER  =========================== \\

    @Override
    public ResponseEntity<ResponseObject> customRequestListByBuyer(HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> createCustomRequest(CreateCustomRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> updateCustomRequest(UpdateCustomRequestRequest request) {
        return null;
    }

    // =========================== ---------  =========================== \\



}
