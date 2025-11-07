package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.ConfirmPaymentUrlRequest;
import com.exe201.group1.psgp_be.dto.requests.CreatePaymentUrlRequest;
import com.exe201.group1.psgp_be.services.PayOsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PayOsController {

    private final PayOsService payOsService;

    @PostMapping
    public ResponseEntity<?> createPaymentUrl(@RequestBody CreatePaymentUrlRequest request) {
      return payOsService.createPaymentUrl(request);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody ConfirmPaymentUrlRequest request, HttpServletRequest httpServletRequest) {
        return payOsService.confirmPayment(request, httpServletRequest);
    }

}
