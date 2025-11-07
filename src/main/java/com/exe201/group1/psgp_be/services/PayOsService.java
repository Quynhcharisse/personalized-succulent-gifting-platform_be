package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.ConfirmPaymentUrlRequest;
import com.exe201.group1.psgp_be.dto.requests.CreatePaymentUrlRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface PayOsService {
    ResponseEntity<?> createPaymentUrl(CreatePaymentUrlRequest request);
    ResponseEntity<ResponseObject> confirmPayment(ConfirmPaymentUrlRequest request, HttpServletRequest httpServletRequest);


}
