package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CreatePaymentUrlRequest;
import com.exe201.group1.psgp_be.models.Product;
import com.exe201.group1.psgp_be.repositories.ProductRepo;
import com.exe201.group1.psgp_be.services.PayOsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PayOsServiceImpl implements PayOsService {


    @Override
    public ResponseEntity<?> createPaymentUrl(@RequestBody CreatePaymentUrlRequest request) {

        long totalAmount = 0;

        for( CreatePaymentUrlRequest.ProductData product : request.getProducts())
        {
            totalAmount = product.getPrice() + totalAmount;
        }

        String clientId = "4d90388d-fd71-47c0-a672-778405bbe418";
        String apiKey = "9296bfcc-7c4e-400f-b290-23ce1e9a1c2b";
        String checksumKey = "eb51df336f9a5a174a4a2f15faf6ee433dbc619a83ede6b8a8803a6179445013";

        PayOS payOS = new PayOS(clientId, apiKey, checksumKey);

        long orderCode = System.currentTimeMillis() / 1000;

        String returnUrl = "http://localhost:5173/buyer/payment";

        long finalAmount = (long) Math.ceil(totalAmount + request.getShippingFee());

        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .returnUrl(returnUrl)
                .cancelUrl(returnUrl)
                .amount((int) finalAmount)
                .description("Thanh toán đơn hàng")
                .build();

        try {
            CheckoutResponseData result = payOS.createPaymentLink(paymentData);

            String embeddedUrl = result.getCheckoutUrl().replace("/web/", "/embedded/");

            return ResponseEntity.ok(Map.of(
                    "checkoutUrl", embeddedUrl,
                    "paymentLinkId", result.getPaymentLinkId(),
                    "orderCode", result.getOrderCode(),
                    "status", result.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> buildProductData(CreatePaymentUrlRequest.ProductData productData) {
        Map<String, Object> result = new HashMap<>();
        result.put("productId", productData.getProductId());
        result.put("size", productData.getSize());
        result.put("price", productData.getPrice());
        return result;
    }




}
