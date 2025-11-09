package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CreatePaymentUrlRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.WalletService;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping()
    public ResponseEntity<ResponseObject> getWallet(HttpServletRequest request) {
        return walletService.getWallet(request);
    }

    @DeleteMapping()
    public ResponseEntity<ResponseObject> cancelPaymentLink(@RequestParam long orderCode) {

        String clientId = "4d90388d-fd71-47c0-a672-778405bbe418";
        String apiKey = "9296bfcc-7c4e-400f-b290-23ce1e9a1c2b";
        String checksumKey = "eb51df336f9a5a174a4a2f15faf6ee433dbc619a83ede6b8a8803a6179445013";

        PayOS payOS = new PayOS(clientId, apiKey, checksumKey);

        try {

            payOS.cancelPaymentLink(orderCode, "Hủy đơn hàng");
            return ResponseBuilder.build(HttpStatus.OK,"Cancel payment link successfully",null);

        } catch (Exception e) {
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR,"Fail",Map.of("error", e.getMessage()));
        }
    }

}
