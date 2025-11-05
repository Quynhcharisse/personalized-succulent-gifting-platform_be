package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CreatePaymentUrlRequest;
import org.springframework.http.ResponseEntity;

public interface PayOsService {
    ResponseEntity<?> createPaymentUrl(CreatePaymentUrlRequest request);
}
